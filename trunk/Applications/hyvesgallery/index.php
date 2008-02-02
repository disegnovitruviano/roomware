<?php header("Content-Type:text/html;charset=utf-8"); ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>		
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Hyves Gallery - Powered by Roomware</title>
	<script src="javascripts/prototype.js" type="text/javascript"></script>
	<script src="javascripts/effects.js" type="text/javascript"></script>
<style type="text/css">
html, body {
	font-family: Verdana, Arial, sans;
	font-size: 16px;
	color: #FFF;
	background:#000;
	margin:0;
	padding:0;
	height:100%;
	background-position:center center;
}

* {
	overflow:hidden;
}

#logos {
	width:100%;
	clear:both;
}

#notice {
	width:100%;
	clear:both;
	text-align:center;
	margin:5px;
	padding:10px 0;
	border-top: 1px solid #fff;
}

#transparant, #header {
	position:absolute;
	width:100%;
	height:107px;
	top:0;
}

#transparant, #trans_footer {
	background:#000;
	opacity:.7;
}

#trans_footer, #footer {
	position:absolute;
	width:100%;
	height:103px;
	bottom:0;
}



#icons {
	overflow: hidden;
	clear:both;
}

#icons ul {
	margin:0;
	padding:0;
	clear:both;
	list-style-type:none;
	text-align:center;
	overflow: hidden;
	height:93px;
}

#icons li {
	display:inline;
	float:left;
	margin:0;
	padding:0;
}

#icons a {
  float: left;
	display:block;
	width:75px;
	height:75px;
	margin: 10px;
	border:4px solid #fff;
}

#photo { 	
	text-align:center;
	clear:both;
	height:722px;
}

#photo div {
	height:700px;
}

#photo span {
	display:block;
	background:#fff;
	padding-bottom: 4px;
	color:#000;
}

#photo img {
	border:4px solid #fff; 
}

#content {
	width:100%;
	height:100%;
}
</style>

<script type="text/javascript">
var delay     = 10000;
var delay2    = 10000;

window.onload = function () {
  push_icons();
  push_photo();
}

function push_icons() {
	new Ajax.Updater('icons', 'icons.php', {
	  onComplete:function(request){
		new Effect.Appear('icons');
	  }
	}); 
	setTimeout("push_icons()", delay);
}

function push_photo() {
	new Ajax.Updater('photo', 'photo2.php', {
	  evalScripts: true,
	  onComplete:function(request){
		new Effect.Appear('photo-new');
		$(request.name).className = "current";
	  }
	}); 
	setTimeout("push_photo()", delay2);
}
</script>
</head>
<body>
  <div id="transparant"></div>
  <div id="header">
	  <div id="logos">
	  	<img src="images/logo.png" alt="Hyves.nl" style="float:left" /> 
		<img src="images/roomware_logo.png" alt="Powered by The Roomware Project" style="float:right" />
	  </div>

	  <div id="notice">
	  	Jouw foto's ook hier? Verander de <strong>bluetooth naam</strong> van je mobiele telefoon in je <strong>Hyves username</strong>!
	  </div>
  </div>

  <div id="photo"></div>

  <div id="trans_footer"></div>
  <div id="footer">
	<div id="icons"></div>
  </div>
</body>
</html>
