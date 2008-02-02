<html>
<head>
	<meta http-equiv="refresh" content="20">
</head>
<body>
<?php

include_once("config.inc.php");
include_once("functions.inc.php");

// Open pointer to XML
if ($debug) {
	$file = "example_scan.xml";
	$fp = fopen($file, "r");
} else {
	$fp = fsockopen($roomware_server, $roomware_server_port, $errno, $errstr);
  if (!$fp) {
     echo "ERROR: $errno - $errstr<br />\n";
  } else {
     fwrite($fp, "\n");
  }
}

// Collect data
$data = fread($fp, 2600);
fclose($fp);
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
  
  	if (!empty($name) && $name != '(null)') // the scanner might return (null) for some devices, ignore those
  	{
  
		print $name."<br />"; // just some debugging info for when you are looking at parser output
		
		$clean_name = mysql_real_escape_string(utf8_decode(strtolower($name)));
		array_push($latest_scan, $clean_name); // save for later
	
		$sql = "SELECT id 
				FROM	events
				WHERE	mac_id = '" . $id . "' AND 
						type = '" . $type . "'  AND 
						event = '" . $event . "'  AND 
						name = '" . $clean_name . "'"; 
		$result = mysql_query($sql) or die ($sql . "<br />" . mysql_error());
		
		if(mysql_num_rows($result) == 0) 
		{
			$sql = "INSERT
					INTO	events
					SET		mac_id = '" . $id . "',
							type = '" . $type . "',
							event = '" . $event . "',
							data = '" . utf8_decode($data) . "',
							name = '" . $clean_name . "',
							timestamp = '" . $time . "'";
			mysql_query($sql) or die ($sql . "<br />" . mysql_error());
	
			// if we have someone with a name recognized by Flickr get the last 15 pictures
			if($nsid = getNSID($clean_name)) 
			{

				// Get the photo data
				$params = array(
					'api_key'	 => $flickr_api_key,
					'method'	 => 'flickr.people.getPublicPhotos',
					'user_id' => $nsid,
					'format'	 => 'php_serial',
					'per_page' => '45',
				);
			
				$encoded_params = array();
				foreach ($params as $k => $v) {
					$encoded_params[] = urlencode($k).'='.urlencode($v);
				}
			
				$url = "http://api.flickr.com/services/rest/?".implode('&', $encoded_params);
				$rsp = file_get_contents($url);
				$rsp_obj = unserialize($rsp);
				
				// save the pictures to the database so we won't have to poll Flickr for them again
				foreach($rsp_obj['photos']['photo'] as $photo)
				{
				
					$server_id = $photo['server'];
					$photo_id = $photo['id'];
					$secret = $photo['secret'];
				
					if (!empty($photo_id) && !empty($secret))
					{
						$filename = "{$photo_id}_{$secret}.jpg";
						$photourl = "{$server_id}/{$filename}";
						
						$sql = "SELECT id 
								FROM	photos
								WHERE	filename = '" . $filename . "' AND 
										name = '" . $clean_name . "'";
						$result = mysql_query($sql) or die ($sql . "<br />" . mysql_error());

						// make sure we are not inserting duplicates
						if(mysql_num_rows($result) == 0) 
						{
							$sql = "INSERT
									INTO	photos
									SET		filename = '" . $filename . "',
											photourl = '" . $photourl . "',
											name = '" . $clean_name . "'";
							mysql_query($sql) or die ($sql . "<br />" . mysql_error());
						}						
					}
		
				}
				
				// Get the icon data
				$params = array(
					'api_key'	 => $flickr_api_key,
					'method'	 => 'flickr.people.getInfo',
					'user_id' => $nsid,
					'format'	 => 'php_serial',
				);
			
				$encoded_params = array();
				foreach ($params as $k => $v){
					$encoded_params[] = urlencode($k).'='.urlencode($v);
				}
			
				$url = "http://api.flickr.com/services/rest/?".implode('&', $encoded_params);
				$rsp = file_get_contents($url);
				$rsp_obj = unserialize($rsp);
			
				$icon_server = $rsp_obj['person']['iconserver'];
				$profileurl = $rsp_obj['person']['profileurl']['_content'];

				if (!empty($profileurl) && !empty($icon_server))
				{

					$filename = "{$nsid}.jpg";
					$iconurl = "{$icon_server}/buddyicons/{$filename}";
					
					$sql = "SELECT id 
							FROM	icons
							WHERE	filename = '" . $filename . "' AND 
									name = '" . $clean_name . "'";
					$result = mysql_query($sql) or die ($sql . "<br />" . mysql_error());

					// make sure we are not inserting duplicates
					if(mysql_num_rows($result) == 0) 
					{
						$sql = "INSERT
								INTO	icons
								SET		filename = '" . $filename . "',
										iconurl = '" . $iconurl . "',
										profileurl = '" . $profileurl . "',
										name = '" . $clean_name . "'";
						mysql_query($sql) or die ($sql . "<br />" . mysql_error());
					}						
				}
			
				
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