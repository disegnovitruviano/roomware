# Introduction #

This is really a short description of the 'fake device' module:

While working on a communicator, you'll probably want to test a number of
use-cases with devices.
For instance:

  * no devices
  * 1 device
  * many devices
  * devices with particular names (I needed it for this case)


Edit the roomware server configuration file in combination with using the 'fake device module', to specify a list of devices for testing purposes. Following this technique can be useful during application development as it saves some time rather than, during testing, having to sniff each time for 10+ bluetooth devices, or having to constantly press the bluetooth "on" and then "off" button/setting on your mobile/cell phone to affect the bluetooth [friendly name](http://www.ir.dcu.ie/343/).

# Install #

The allinone download contains this module, you probably don't need to do this unless you are sure about it.

1. Put the file Module\_Fake-1.0.1.jar to your lib directory of the roomware server tree.


# Configuration #

1. Add a new module to the roomware server configuration (eg. 'fake'):
> modules: _list, of, previous, modules_, **fake**

2. Load the module class for the 'fake device' module:
> fake-class: **org.roomwareproject.module.fake.Module**

3. You **must** define the devices:
> fake-devices: test1, 0123456789

4. You **can** add a [friendly name](http://www.ir.dcu.ie/343/) to a device:
> fake-0123456789-name: my simple demo device that has address 0123456789