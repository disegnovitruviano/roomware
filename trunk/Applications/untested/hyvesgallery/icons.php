<?php

include_once("config.inc.php");
include_once("functions.inc.php");

// Pillage database for one random Flickr users (and great justice)
$result = mysql_query("SELECT * FROM icons");
header('X-JSON: ({"icon_id":1})'); // all in one go...

while ($row = mysql_fetch_assoc($result)) {

	if (!empty($row['filename']) && !empty($row['profileurl']) && !empty($row['iconurl']))
	{	
	
		$filename = $row['filename'];
		$profileurl = $row['profileurl'];
		$iconurl = $row['iconurl'];
		$name = $row['name'];
		$localfile = $cache_dir. "/icons/" . $filename; //Name cache file based on profile URL

		if (!file_exists($localfile)) //if cache file doesn't exist
		{ 
			touch($localfile); //create it
			chmod($localfile, 0666);
			fetchfile($iconurl, $localfile); //then cache the icon
		}
		else if (((time()-filemtime($localfile))/60)>$cacheminutes) //if age of cache file great than cache minutes setting
		{
			fetchfile($iconurl, $localfile); 
		}
		
		$avatars .= "<li><a href=\"{$profileurl}\" target=\"_blank\" style=\"background:url({$localfile}) center center;\" id=\"$name\" > </a></li>";
		$width = $width + 110;
	}
}
?><ul style="width:<?php echo $width; ?>px"><?php echo $avatars; ?></ul>
