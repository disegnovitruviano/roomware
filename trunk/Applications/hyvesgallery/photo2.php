<?php

include_once("config.inc.php");
include_once("functions.inc.php");

// Pillage database for one random Flickr users (and great justice)
$result = mysql_query("SELECT * FROM photos ORDER BY rand() DESC LIMIT 1");
$row = mysql_fetch_array($result);

$width = 0;
if (!empty($row['filename']) && !empty($row['photourl']))
{
	$filename = $row['filename'];
	$photourl = $row['photourl'];
	$name = $row['name'];
	
	$localfile = $cache_dir. "/photos/" . $filename; //Name cache file based on profile URL

	if (!file_exists($localfile)) //if cache file doesn't exist
	{ 
		touch($localfile); //create it
		chmod($localfile, 0666);
		fetchfile($photourl, $localfile); //then cache the icon
	}
	else if (((time()-filemtime($localfile))/60)>$cacheminutes) //if age of cache file great than cache minutes setting
	{
		fetchfile($photourl, $localfile); 
	}

	// echo "<img src=\"{$localfile}\" id=\"photo-new\" style=\"display:none\" /><span><strong>{$row['name']}</strong>.hyves.nl</span>";
	// echo "<span><strong>{$row['name']}</strong>.hyves.nl</span><div style=\"background:url({$localfile}) no-repeat center center;display:none;\" id=\"photo-new\" /></div>";
	?>
	<style type="text/css">
	.img {
		background-image:url('<?php echo $localfile; ?>');
	}
	</style>
	<script type="text/javascript">
	document.body.className = "img";
	</script>
	<?php
}
?>
