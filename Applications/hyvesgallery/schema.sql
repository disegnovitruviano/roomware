# Dump of table events
# ------------------------------------------------------------

CREATE TABLE `events` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `mac_id` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `type` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `event` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `data` blob NOT NULL,
  `timestamp` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table icons
# ------------------------------------------------------------

CREATE TABLE `icons` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `filename` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `iconurl` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `profileurl` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table photos
# ------------------------------------------------------------

CREATE TABLE `photos` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `filename` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `photourl` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



