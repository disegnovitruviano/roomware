<?php

class dbcache {
	

	public static function contains_event($id, $type, $event, $clean_name) {
		 $sql = "SELECT id 
			       FROM  events
					   WHERE mac_id = '" . $id . "' AND 
					         type = '" . $type . "'  AND 
								   event = '" . $event . "'  AND 
									 name = '" . $clean_name . "'";

		 $results = mysql_query($sql);// or die ($sql . "<br />" . mysql_error());
		 return (mysql_num_rows($results) != 0);
	}



	public static function add_event($id, $type, $event, $data, $clean_name) {
		$time = time();

		$sql = "INSERT 
		 		   INTO events
				   SET mac_id = '" . $id . "', 
				         type = '" . $type . "',
							   event = '" . $event . "',
								 data = '" . utf8_decode($data) . "',
								 name = '" . $clean_name . "',
								 timestamp = '" . $time . "'";

	  $results = mysql_query($sql);// or die ($sql . "<br />" . mysql_error());
	}


	public static function photo_exists($photo_url) {
		$sql = "SELECT id
					  FROM photos
						WHERE photourl = '" . $photo_url ."'";
		$results = mysql_query($sql) ; //or die ($sql . "<br />". mysql_error());
		return (mysql_num_rows($results) > 0);
	}


	public static function photo_insert($filename, $photo_url, $clean_name) {
		$sql = "INSERT
			      INTO   photos
						SET    filename = '" . $filename . "',
		               photourl = '" . $photo_url . "',
									 name = '" . $clean_name . "'";

	   mysql_query($sql); // or die ($sql . "<br />" . mysql_error());
	}


	public static function photo_insert_once($filename, $photo_url, $clean_name) {
		if(!dbcache::photo_exists($photo_url)) {
			dbcache::photo_insert($filename, $photo_url, $clean_name);
		}
	}


	public static function avatar_exists($clean_name) {
		$sql = "SELECT *
			      FROM icons
						WHERE name ='". $clean_name ."'";
		$results = mysql_query($sql);// or die ($sql . "<br />" . mysql_error());

		return (mysql_num_rows($results) > 0);
	}


	public static function avatar_insert($filename, $avatar_url, $profile_url, $clean_name) {
		$sql = "INSERT
					  INTO   icons
						SET    filename = '" . $filename . "',
						       iconurl = '". $avatar_url ."',
									 profileurl = '". $profile_url ."',
									 name = '". $clean_name ."'";
		mysql_query($sql);// or die ($sql . "<br />" . mysql_error());
	}


	public static function avatar_insert_once($filename, $avatar_url, $profile_url, $clean_name) {
		if(!dbcache::avatar_exists($clean_name)) {
			dbcache::avatar_insert($filename, $avatar_url, $profile_url, $clean_name);
		}
	}


}
