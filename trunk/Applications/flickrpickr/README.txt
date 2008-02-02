# ---------------------------------------------------
# FLICKR PICKR
#
# v.0.1, april 2007, Robert Gaal
# v.0.2, may 2007, Tijs Teulings
#
# http://roomwareproject.org/
# ---------------------------------------------------

# ---------------------------------------------------
# description
# ---------------------------------------------------

Flickr Pickr displays the photo's of all people in the room
who are a member of http://flickr.com. 

It does this through a Roomware server, who scans the room for
available bluetooth devices (like a mobile phone or a laptop). 
If the name of a device is similar to a member on flickr.com, 
it displays their icon and one random picture.

# ---------------------------------------------------
# requirements
# ---------------------------------------------------

- Apache server running PHP & MySQL
- Ability to run cronjobs (to run the parser.cron.php file on a scheduled interval)
- Roomware server (http://roomwareproject.org/pages/download)
- Roomware scanner (http://roomwareproject.org/pages/download)

# ---------------------------------------------------
# installation
# ---------------------------------------------------

- Alter config.inc.php and insert your database connection information
- Run schema.sql on your chosen database table
- Setup a cronjob or script to run simple_parser.cron.php every 15 seconds (or other interval)
- Run index.php and sit back, maybe have a beer or something
