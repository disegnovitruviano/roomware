# Introduction #


# Install #

The allinone donwload contains this module and a JSR-82 implementation for Windows and OSX, you probably don't need to do this unless you are sure about it.

1. Put the Module\_Bluetooth-1.0.0.jar in your lib directory of your roomware server tree.

2. Add the [BlueCove](http://code.google.com/p/bluecove/)-2.0.2.jar to your lib directory. (Or any other JSR-082).

# Configuration #

1. Add a new module to the roomware server configuration (eg. 'blue'):
> `modules: <list, of, previous, modules>, blue`

2. Load the module class for the 'blue' module:
> `blue-class: org.roomwareproject.module.bluetooth.Module`

3. You can configure the next options:
> a. update-interval-time

> Sets the interval time. After each interval a new device inquiry will start.
> Default value is 40 seconds. Since a device inquiy takes around 11 seconds,
> using values below 15 seconds could have issues (if you also try name lookup).

> `blue-update-interval-time: 30`

> b. device-clear-time

> Device don't actively broadcast: "I'm gone!" - - - hmmm.. if they are gone, they can't broadcast... - - - . So when is a device gone? Well the module will think it is gone after some  default seconds. To adjust this value for your needs do:

> `blue-device-clear-time: 40`


> c. name-lookup-policy

> If you don't want to lookup the names specify _never_. If you only need to know the name and you are not interested in changes, specify _once_. You can also ask every time the name of the device, specify _always_.

> Note: name lookup is only done in the idle time between the inquiry length (11 secs) and the time left to the new interval. If you specify the interval time as only 12 seconds, there will be only 1 name lookup.

> `blue-name-lookup-policy: once`

> d. stop-on-nerrors

> this option stops the roomware server after n number of errors from the bluetooth module. set this to 2 to stop the server after 2 errors for instance or 0 (or a negative number) to never stop the roomware server on bluetooth errors.

> `blue-stop-on-nerrors: 0`

> for most uses it's not needed to stop the roomware server when the bt module returs errors since it will keep running even if scanning fails now and then.