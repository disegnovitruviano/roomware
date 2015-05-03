# Introduction #

So you've downloaded all those good looking JARs and now your looking to play with them right? We'll you can't right away. First you'll need to configure your server a bit.

Were working on a system that will automatically load any modules you might want to use but for now it's a bit of typing thats needed.


# Details #

This instruction presumes that you have already setup your environemnt variables and copied the module and communicator JAR files into their respective directories.

In your Server/conf/ directory you'll find the following congiguration file; roomware.conf

In a standard setup with the bluetooth module and the http communicator loaded your config file will have to include the following lines at a minimum:

```
# comma seperated list of modules to load
modules: bluetooth

# bluetooth class definition
bluetooth-class: org.roomwareproject.module.bluetooth.Module
bluetooth-name-lookup-policy: always
bluetooth-update-interval-time: 30
bluetooth-device-clear-time: 160
bluetooth-zone: hybrid2
bluetooth-stop-on-nerrors: 0

# comma seperated list of communicators to load
communicators: console, http

# console class definition
console-class: org.roomwareproject.communicator.console.Communicator

# http class definition
http-class: org.roomwareproject.communicator.http.Communicator

# admin class definition
admin-class: org.roomwareproject.communicator.admin.Communicator

# You can configure the roomware server to exit if there is a error detected
# during the startup.
#
# stop-on-error: 'false' || 'true'
stop-on-error:	true
```

With this config and the JARs in place you should be well on your way. Now just follow along with the instal guide again: http://roomwareproject.org/pages/download

have fun!