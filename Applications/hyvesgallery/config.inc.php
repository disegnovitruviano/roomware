<?php
// PHP settings
ini_set("register_globals",0);
ini_set("error_reporting",E_ALL ^ E_NOTICE);

// Database config
mysql_connect("127.0.0.1", "root", "");
mysql_query("SET NAMES 'utf8'");
mysql_select_db("roomware");

// Set to true if you want to parse the local example_scan.xml file instead of a roomware server
// TODO: make local parsing work
$debug = false;

// cache variables
$cache_dir = 'cache';
$cache_minutes = 60;

// the amount of minutes after which devices are purged from the database
$clear_minutes = 1;

// Get your api key on http://www.flickr.com/services/api/
$flickr_api_key = "3921eb4af459ded0fb490cc7d9716bfb";

// Roomware server can be downloaded on http://roomwareproject.org
$roomware_server = "tcp://127.0.0.1";
$roomware_server_port = 4040;
?>