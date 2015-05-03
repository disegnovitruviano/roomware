# Introduction #

With the latest version of the Roomware server it's become possible to configure multiple roomware servers in a master & slave setup. In this configuration multiple slaves will scan and send their data to a master server which collects all the inputs and makes it available to it's communicators; console, http, post, etc.

# Details #

In this configuration one roomware server instance is the master. For this a module is added to it;s configuration which listens on port 4003 (by default) and collects incoming data from other roomware servers.

**configuration for master server**

Let's say the master server collects data but also scans for bluetooth devices itself, then we should add both a bluetooth and master module:

> `modules: bluetooth, master`

The bluetooth module is setup as described in the BluetoothModule page, the master module simply needs to be 'attached' to it's class:

> `master-class: org.roomwareproject.module.roomware.Module`

**Configuration for slave server**

Once you've set this up you can move onto the slave(s). For each slave server add the following to the configuration.

> `communicators: console, http, slave`

In the above example the slave server is running a console and http communicator to look at the data it is collecting locally and a slave communicator to send it's data off to a master server on the IP address 192.168.2.101

> `slave-host: 192.168.2.101`

and the default port number.

> `#slave-port: 4003`

to make sure the slave identifier actually loads the communicator you'll have to attach it to the class as well.

> `slave-class: org.roomwareproject.communicator.roomware.Communicator`

