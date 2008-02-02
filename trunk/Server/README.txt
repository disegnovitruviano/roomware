Welcome: you found the README file
----------------------------------

  RoomWare Server: v1.0beta
  Date: January 23, 2008
  Contact: tom@roomwareproject.org
  General Info: www.roomwareproject.org
  License: MIT, see website for full details


TABLE OF CONTENTS:
  - what is this
  - dependencies
  - how to run the roomware server
  - how to use a module
  - how to use a communicator
  - how to edit the roomware server code


What is this?
  Hi this is the RoomWare server. It is a framework that hides the
  obscurities of all diverent protocols. This makes it possible for
  webdevelopers, programmers, interaction designers to enrich their
  systems with all kind of technology. This is done in 2 steps.

  First a communicator should be installed and configured to interface
  the RoomWare server. See SVN or our website for available communicators.
  Now you can get the data out of the server.

  But how does the server gets the data? This is done by modules. For every
  tech you wish to use a module should be installed (and configured).
  Again SVN or our website shows the available modules you can use. If there
  is no module for your desired tech, or platform, you should send us an email,
  or even better, start developing your own module.

  You can donate your own written modules and communicators and we can 
  redistribute them using our SVN repository. This makes it even more fun
  for more people!


Dependencies
  See 'conf/setenv' for dependencies.


How to run the roomware server?

  1. configure your environment "vim conf/setenv"
       the project path currently points to:
       $HOME/Projects/RoomWare/Server
       probably you have a different directory layout!

  2. setup your (just edited) environment with "source conf/setenv"

  3. run the roomware server with "bin/run"


How to configure the roomware server?

  1. edit the file "conf/roomware.conf"


How to use a module?

  1. copy the JAR into the dir modules "cp newmodule.jar modules"
  3. edit the roomware config to load your module "vim conf/roomware.conf"
  3. runn the roomware server "bin/run"

 
How to use a communicator?

  - see above at module, only replace 'module' with 'communicator'.


How to edit the roomware server?

  1. edit the source in "src/roomware/"
  2. compile the roomware server "bin/build_server"
  3. run the server "bin/run"


Enjoy your RoomWare experience!

- Tom 
