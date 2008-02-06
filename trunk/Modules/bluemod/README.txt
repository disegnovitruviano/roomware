This is the too short README file for Module bluemod

  Module: bluemod
  Contact: tom@roomwareproject.org
  Web: www.roomwareproject.org

  Version: 0.2


CHANGES 0.2:

- output "update-interval-time" bug, fixed


WHAT IS THIS?:

This module will enable bluetooth device discovery.
It does currently not support sending and receiving message and service
registration or discovery.

To build the module and install the module, see PROTO module.


DEPENDENCIES:

This module depends a JSR-082 implementation. See our website for more info
on which implementations there exists. For your platform you have 1 or 2 files
that should be copied into the lib dir before building (and for running they
must also be copied to the RW server lib dir).

If you use the BlueDentist implementation you must copy these files:
bluedentist.jar, bluedentist.jnilib.

I advise to use BlueCove. It has been tested on Windows Vista, Linux and
Mac OS X (Tiger, PowerPC). Except for some warnings during name update it
works fine.


- Tom
