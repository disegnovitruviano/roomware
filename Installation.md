# Introduction #

The RoomWare server code is currently released in a beta stage under the MIT license. Feel free to download and experiment. The zip package includes installation documentation, if you have any other questions or nee more help please join us on the [RoomWare mailing list](http://groups.google.com/group/roomware).

# Howto: RoomWare installation #

The software has, so far, been developed by a small group of people that often meet in real live. The roomware server has run mainly on a PowerBook (PowerPC) with Tiger. This means a lot of documentation should be done for other configurations. Please try it out and if you have any difficulties I can help to solve the problem and document it for other users.

Enjoy your RoomWare experience!
Contact: tom@roomwareproject.org

## Server ##

Here you can download the core, the RoomWare server. It doesn't contain any modules or communicators. These can be downloaded below.

### Install Notes ###

  1. Configure your environment; You must edit the file in `conf/setenv`.
  1. Optional: Configure the RoomWare Server; You can configure the server by editing `conf/roomware.conf`.

For more information on the configuration options check out RoomwareConfiguration

### Run Howto ###

  1. Setup your environment; Type in your shell `source conf/setenv`. This will set variables needed by the RoomWare Server for you.
  1. Run the server; Type in your shell `bin/run`.

### Dependencies ###

  * Java Virtual Machine (JRE) 1.5; You need at least Java Virtual Machine 1.5 to run the compiled byte code.
  * Optional: Java Compiler (JDK) 1.5; When compiling the Java Development Kit should be at least version 1.5.

### Download ###

You can download the RoomWare Server 1.0 beta release from our [Google Code downloads page](http://code.google.com/p/roomware/downloads/list).

## Modules ##

### Install Howto ###

  1. Copy the module; You must copy the module classes (jar file located in build dir) into the roomware server module directory `Server/modules`.
  1. Copy any dependencies; Now you need the dependencies copied into the `Server/lib` dir. These are located in the lib dir of the module tree.
  1. Edit the RoomWare Server configuration; This must be done in the file `Server/conf/roomware.conf`.

For more information on the configuration options check out RoomwareConfiguration

### Bluetooth Module ###

**JSR-082 Implementation**
This is the Java Bluetooth API. You need to download an implementation and copy the jar (and possibly a `so`, `jnilib` or `dll`) to the Server's lib dir.

[Blue Cove](http://bluecove.org/) supports all major platforms so this is probably your best bet. As an alternative you could use our own implementation called BlueDentist which is available by checking out the project from SVN.

### Download ###

You can download our own Bluetooth Module 0.1 from our [Google Code downloads page](http://code.google.com/p/roomware/downloads/list)

## Communicators ##

### Install Howto ###

  1. Copy the communicator; You must copy the communicator classes (jar file located in build dir) into the roomware server communicator directory Server/communicators.
  1. Edit the RoomWare Server configuration; This must be done in the file `Server/conf/roomware.conf`.

### Network Communicator (XML) ###

### Download ###

You can download our Network Communicator 0.2 module from our [Google Code downloads page](http://code.google.com/p/roomware/downloads/list)

## Coming soon: All-in-One ##

This will follow. Please help by submitting your working RoomWare server with all modules and communicators from this page to us. I will then make them available for 1-click run.

### Support ###

Please try it out. Use it. Ask questions. Email us. We will help you and make a better documentation. We will release much more documentation about RoomWare and computer related information soon.

# Demo Applications #

## FlickrPickr ##

The FlickrPickr is our first demo application and highlights the use of the Bluetooth module that comes with the standard RoomWare server installation. It's a basic web application built using PHP & MySQL and it uses the Bluetooth device data obtained from the RoomWare server to display the Flickr avatars and the latest pictures of the people present in the room based on their device's Bluetooth name.

### Download ###

You can download our  flickrpickr-0.2.zip from our [Google Code downloads page](http://code.google.com/p/roomware/downloads/list)

## HyvesGallery ##

The HyvesGallery is the dutch stepchild of the FlickrPickr. Using a similar principal as the FlickrPickr this app downloads and presents photos from people's Hyves profile instead. If you are unfamiliar with Hyves; it's the largest social networks in the Netherlands.

### Download ###

You can download our  hyvesgallery\_1.0.zip from our [Google Code downloads page](http://code.google.com/p/roomware/downloads/list)