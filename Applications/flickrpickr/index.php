<?php header("Content-Type:text/html;charset=utf-8"); ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>		
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>flickrpickr | roomwareproject.org</title>
	<script src="javascripts/prototype.js" type="text/javascript"></script>
	<script src="javascripts/effects.js" type="text/javascript"></script>
<style type="text/css">
html, body {
  font-family: Arial, Verdana, sans;
  font-size: 14px;
  color: #FFF;
  background:#000;
}

h1 {
background:url(images/logo.gif);
width:200px;
height:44px;
display:block;
margin:0;
padding:0;
}

h1 span {
display:none;
}

h4 {
	margin: 0 0 0 5px;
	font-size: 120%;
}

#icons {
  width: 100px;
  height: 100%;
  float:left;
  overflow: hidden;
  
}
#icons img, #photo img { 
  border: 1px solid #666;
  margin: 10px;
  padding: 10px;
}
/*
#photo img { 
	width: 70%;
	height: 70%;
}
*/
#photo {
  width: 80%;
  margin-left: 100px;
}
#photo #subtext {
  margin: 6px 0 0 10px;
}

</style>
<script type="text/javascript">
var delay     = 10000;
var delay2    = 8000;

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
	new Ajax.Updater('photo', 'photo.php', {
	  onComplete:function(request){
		new Effect.Appear('photo-new');
	  }
	}); 
	setTimeout("push_photo()", delay2);
}
</script>
</head>
<body>
  <h1><span>flickrpickr</span></h1>
  <h4>roomwareproject.org | change your bluetooth device name to your Flickr name!</h4>
  <div id="icons">
  </div>
  
  <div id="photo">
  </div>
</body>
</html>