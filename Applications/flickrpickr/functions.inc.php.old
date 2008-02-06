<?php
// Basic functions

function getNSID($username) 
{
  global $flickr_api_key;
  // Get the user data
  $params = array(
  	'api_key'	 => $flickr_api_key,
  	'method'	 => 'flickr.people.findByUsername',
  	'username' => $username,
  	'format'	 => 'php_serial',
  );
  
  $encoded_params = array();
  foreach ($params as $k => $v){
  	$encoded_params[] = urlencode($k).'='.urlencode($v);
  }
  
  $url = "http://api.flickr.com/services/rest/?".implode('&', $encoded_params);
  $rsp = file_get_contents($url);
  $rsp_obj = unserialize($rsp);
  if($rsp_obj['stat'] == 'ok') {
    $nsid = $rsp_obj['user']['nsid'];
    return $nsid;
  } else {
    return false;
  }
}


function fetchfile($remoteurl, $localfile)
{	
	$contents=file_get_contents('http://static.flickr.com/'.$remoteurl); //fetch icon from Flickr
	$fp=fopen($localfile, "w");
	fwrite($fp, $contents); //write contents to cache file
	fclose($fp);
}

?>