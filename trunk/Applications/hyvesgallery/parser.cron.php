<html>
<head>
<meta http-equiv="refresh" content="120">
</head>
<body>
<?php

include_once("config.inc.php");
include_once("functions.inc.php");
include_once("hyves/hyves.inc.php");
include_once("hyves/hyvesuser.inc.php");
include_once("hyves/dbcache.inc.php");


$api_key = 'OF-4mV56miaX_RAdVMwi5kOn';
$shared_key = 'OF8X7GejtZXHpxTudURpE7PM';
$hyvesApi = new Hyves($api_key, $shared_key);

// Open pointer to XML
if ($debug) {
	$file = "example_scan.xml";
	$fp = fopen($file, "r");
} else {
	$fp = fsockopen($roomware_server, $roomware_server_port, $errno, $errstr);
	if (!$fp) {
		echo "ERROR: $errno - $errstr<br />\n";
	} else {
		fwrite($fp, ".\n");
	}
}

// TODO: 2.600 was too small -> now 26.000 bytes
// Collect data
$data = fread($fp, 1000000);
fclose($fp);
print "<br/>$data<br/>";
preg_match_all("/\<device\>(.*)\<\/device\>/i", $data, $matches);

$latest_scan = array();

// Put it in the db
foreach($matches[1] as $val) {

	$arr = explode("<",$val);
	$id = str_replace("id>","",$arr[1]);
	$name = str_replace("name>","",$arr[3]);
	$type = str_replace("type>","",$arr[5]);
	$event = str_replace("event>","",$arr[7]);
	$data = str_replace("data>","",$arr[9]);
	//$time = str_replace("time>","",$arr[11]); // scanner does not seem to return the time
	$time = time();


	if (!empty($name) && $name != 'null') // the scanner might return (null) for some devices, ignore those
	{

		print $name."<br />"; // just some debugging info for when you are looking at parser output

		$clean_name = mysql_real_escape_string(utf8_decode(strtolower($name)));
		array_push($latest_scan, $clean_name); // save for later

		$known = dbcache::contains_event($id, $type, $event, $clean_name);
		if(!$known) {
			dbcache::add_event($id, $type, $event, $data, $clean_name);
		}

	  $hyvesUser = HyvesUser::get_hyves_user($clean_name, $hyvesApi);
		if($hyvesUser === false) {
			print("$clean_name is not a hyves user!<br />");
		} else {

			if($hyvesUser === false) {
				print("not a hyves user: $clean_name<br />");
				continue;
			}

			$avatar_url = $hyvesUser->get_avatar_url();
			if($avatar_url === false) {
				print("user has not an avatar: $clean_name<br />");
			} else {
				$uniq_filename = $hyvesUser->get_uid().".jpg";
				$profile_url = $hyvesUser->get_profile_url();
				dbcache::avatar_insert_once($uniq_filename, $avatar_url, $profile_url, $clean_name);
			}

			$photo_urls = $hyvesUser->get_all_photo_urls();
			if($photo_urls === false) continue;
			foreach($photo_urls as $photo_url) {
				$uniq_filename = md5($photo_url).".jpg";
				dbcache::photo_insert_once($uniq_filename, $photo_url, $clean_name);
			}
		}
	}
}

// clean up the database
$sql = "SELECT name, timestamp, id FROM events";
$result = mysql_query($sql) or die ($sql . "<br />" . mysql_error());

$seconds = floor($clear_minutes * 60);

while ($row = mysql_fetch_assoc($result)) {

	$now = time();
	$last_seen = $row['timestamp'];
	$diff = ($now - $last_seen);

	// check time for names we aren't seeing anymore
	if (!in_array($row['name'], $latest_scan) && ($diff > $seconds)) 
	{

		$gone = mysql_real_escape_string($row['name']);

		// clear device
		$sql = "DELETE FROM events WHERE name = '$gone'";
		mysql_query($sql) or die ($sql . "<br />" . mysql_error());

		// clear photos
		$sql = "DELETE FROM photos WHERE name = '$gone'";
		mysql_query($sql) or die ($sql . "<br />" . mysql_error());

		// clear icon
		$sql = "DELETE FROM icons WHERE name = '$gone'";
		mysql_query($sql) or die ($sql . "<br />" . mysql_error());

		echo "device lost: ".$row['name'] . "<br />";
	}
}	

?>
</body>
</html>
