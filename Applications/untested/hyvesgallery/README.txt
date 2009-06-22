# ---------------------------------------------------
# Hyves Gallery
# Made by Tijs Teulings, Tom Burger & Robert Gaal, december 2007
# http://roomwareproject.org/
# ---------------------------------------------------

# ---------------------------------------------------
# description
# ---------------------------------------------------

Displays random photo's from Hyves.nl users, looking at Bluetooth username = Hyves username.

# ---------------------------------------------------
# requirements
# ---------------------------------------------------

- Apache server running PHP & MySQL
- Ability to run cronjobs (to run the .cron file on a scheduled interval)
- Roomware server (see www.roomwareproject.org for download)

# ---------------------------------------------------
# installation
# ---------------------------------------------------

- Alter config.inc.php and insert your database connection information
- Run schema.sql on your chosen database table
- Setup a cronjob to run simple_parser.cron.php every 15 seconds (or other interval)
- Run index.php and sit back, maybe have a beer or something
