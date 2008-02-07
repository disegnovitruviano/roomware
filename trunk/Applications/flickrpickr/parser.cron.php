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
	$file = "http://localhost/example_scan.xml";
	$fp = fopen($file, "r");
} else {
	$fp = fsockopen($roomware_server, $roomware_server_port, $errno, $errstr);
 	$file = $roomware_server.":".$roomware_server_port;
  if (!$fp) {
     echo "ERROR: $errno - $errstr<br />\n";
  } else {
     fwrite($fp, "\n");
  }
}

// Collect data
$xmldata = "";
if($fp)
{
 while (!feof($fp)) {
   $xmldata .= fread($fp, 8192);
 }
fclose($fp);
}

print $xmldata;flush(); //test if xml was read

//preg_match_all("/\<device\>(.*)\<\/device\>/i", $data, $matches);
//print_r($matches);flush();
$latest_scan = array();


//use simplexml comes with php 5

//check if file exists  
if (url_exists($file)) {
    $xml = simplexml_load_file($file);
} else {
    exit('Failed to open '.$file);
}
//$xml = simplexml_load_file($file);
/*print attributes*/
print("<pre>");
print_r($xml);
print("</pre>");


/*
foreach ($xml->device as $device) {
   echo $device->id, '<br />';
   echo $device->name, '<br />';
   echo $device->type, '<br />';
   echo $device->event, '<br />';
   echo $device->data, '<br />';
   echo $device->time, '<br />';
	 echo '<br />';
}
*/

// Put it in the db
$i=0;

foreach ($xml->device as $device) {
print $i++;flush();
  
  	if (!empty($device->name) && $device->name != '(null)') // the scanner might return (null) for some devices, ignore those
  	{
  
		print $device->name."=name<br />"; // just some debugging info for when you are looking at parser output
		
		$clean_name = mysql_real_escape_string(utf8_decode(strtolower($device->name)));
		array_push($latest_scan, $clean_name); // save for later
	
		$sql = "SELECT id 
				FROM	events
				WHERE	mac_id = '" . $device->id . "' AND 
						type = '" . $device->type . "'  AND 
						event = '" . $device->event . "'  AND 
						name = '" . $clean_name . "'"; 
		$result = mysql_query($sql) or die ($sql . "<br />" . mysql_error());
		
		if(mysql_num_rows($result) == 0) 
		{
			$sql = "INSERT
					INTO	events
					SET		mac_id = '" . $device->id . "',
							type = '" . $device->type . "',
							event = '" . $device->event . "',
							data = '" . utf8_decode($device->data) . "',
							name = '" . $clean_name . "',
							timestamp = '" . $device->time . "'";
			mysql_query($sql) or die ($sql . "<br />" . mysql_error());
	
			// if we have someone with a name recognized by Flickr get the last 15 pictures
print "cleanname".$clean_name."<BR>";
	print (string)getNSID($clean_name);
			if($nsid = getNSID($clean_name)) 
			{
print "yep";
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
				print $url; flush();
				$rsp = file_get_contents($url);
				$rsp_obj = unserialize($rsp);
				
				print "a";print_r($rsp_obj);				print "a";

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