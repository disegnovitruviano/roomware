# Introduction #

The HTTPD Communicator was written to be a replacement, or addition on top of, the httpxml communicator. This communicator now ships per default and provides support for multiple formats to get your data. Included formats are XML, JSON and CSV.


# Connecting and getting the data #

When you want to get the data in one of the supported formats, just open a HTTP connection to your roomware server and use the format as its location. Non-existing formats will throw a 404 header, so be sure to handle this in your application.

| XML | http://yourroomwareserver:port/xml/ |
|:----|:------------------------------------|
| JSON | http://yourroomwareserver:port/json/ |
| CSV | http://yourroomwareserver:port/csv/ |

(default settings would be localhost:4040)

There is one important notice, the CSV format has it's fieldnames printed on the first line of the output, so strip it off if you input the raw data into your application.

When you access http://yourroomwareserver:port/ you will be served a small html file linking to the site and documenation. If you do not want to expose this information, edit the /build/communicators/httpd/docs/index.html file.

# Migrating old httpxml based applications #

If your application uses the httpxml communicator, you can simply adjust the uri to one of the above. The xml format and behaviour is exactly the same as the old httpxml service.

# Relevant configuration options #

Inside your roomware.conf the following should be present per default. The service should bind a socket to the first available network interface.

```
httpd-class: org.roomwareproject.communicator.httpd.Communicator
httpd-web-server-port: 4040
```

# Sample application using JSON #

The following example is fully selfcontained.

```
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

	<title>Available</title>
	<script language="javascript">
		var serverPath = "http://localhost:4040/json/";
		var timeOut = 16000;
		var devices = new Array();
	</script>
</head>

<body>
<ul id="container"></ul>
<script src="http://www.google.com/jsapi"></script>
<script language="javascript">
	google.load("jquery", "1.3.2");
	
	google.setOnLoadCallback(function() {
		doRefresh();
		setInterval(doRefresh, 16000);
	});
	
	function doRefresh() {
		$.getJSON(serverPath, onDataReceived);
	}
	
	function onDataReceived(aData) {
		devices = new Array();
		$.each(aData, function(i, item) {
			if (item.name != null)
				devices.push(item);
		});
		
		redraw();
	}
	
	function redraw() {
		$("#container").empty();

		$.each(devices, function(i, item) {
			$("#container").append("<li id=\"" + item.name + "\">" + item.name + "</li>");
		});
	}
</script>
</body>
</html>
```