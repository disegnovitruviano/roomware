<?php

include './hyves.inc.php';			// Hyves API wrapper
include './cache.inc.php';		// Caching class

/* Config options */

$api_key = 'OF-4mV56miaX_RAdVMwi5kOn';
$shared_key = 'OF8X7GejtZXHpxTudURpE7PM';

$ttl = 1800; // (in secs) all data is cached for half an hour
$debug = true;

/* end: Config options */

$uname = $_GET['uname']; // one or more (comma seperated) Hyves uname's

if (!empty($uname)) 
{	

	$cacheId = strip($uname); 	// unique id for this request
	$typeId = "people"; 			// id for request group
	
	if (!$output = dataCache::get($typeId, $cacheId)) {
		$output = getFreshResult($uname);
        dataCache::put($typeId, $cacheId, $ttl, $output);
    }

}
else
{
	header("HTTP/1.0 400 Bad Request");
	die('uname parameter required');
}

function getFreshResult($uname) {
	global $api_key, $shared_key;
	
	$api = new Hyves($api_key, $shared_key);
	
	$xmlstr = <<<XML
<?xml version='1.0' standalone='yes'?>
<people>
</people>
XML;

	$output = new SimpleXMLElement($xmlstr);

	$aids = array();
	$uids = array();
	$pids = array();
	$photos = array();

	$users = $api->call_method('users.get', 'uname', $uname); // get users

	if (!$users) die();

	foreach($users as $user) {
		$pids[] = $user->pid;
	}

	$avatars = $api->call_method('photos.get', 'pid', implode(',', $pids)); // get avatars for users


	foreach($users as $user) { // get photos from albums & avatar for user

		$avatar_image = "";

		foreach($avatars as $avatar) {
			if ((string) $avatar->pid == (string) $user->pid)
			{
				$avatar_image = $avatar->image_large->src;
			}
		}

		$person = $output->addChild('person');
		$person->addAttribute('name', $user->nickname);
		$person->addAttribute('avatar', $avatar_image);

		$albums = $api->call_method('photos.getAlbums', 'uid', $user->uid);

		if ($albums)
		{
			foreach($albums as $album) {
				$aids[] = (string) $album->aid;
			}
		}


		if ($albums) 
		{

			if (count($aids) > 0) {

				foreach($aids as $aid) {
					$imgs = $api->call_method('photos.get', 'aid', $aid);
				}

				if ($imgs)
				{

					foreach($imgs as $img) { // store the xlarge version of each image
						$photo = $person->addChild('photo', $img->image_xlarge->src);
					}

				}

			}

		}
	}
	
	return $output->asXML();
	
}


function debug($object) {
	global $debug;
	
	if ($debug)
	{
		print("---------------------------------------------\n");
		print_r($object);
		print("\n---------------------------------------------\n");
	}
	
}

function strip($string) {
	return ereg_replace("[^A-Za-z0-9]", "", $string);
}


?>