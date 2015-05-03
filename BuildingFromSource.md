# Introduction #

The build from source wiki in initial state. Not tested yet.


# Details #

  * Getting the code
> ` svn checkout http://roomware.googlecode.com/svn/trunk/ roomware-read-only `
  * Go into the server source directory
> ` cd roomware-read-only/Server `
  * Build the server and default modules/communicators
> ` ant all `
  * Download the JSR-082 for bluetooth
> ` cd lib && wget http://bluecove.googlecode.com/files/bluecove-2.1.0.jar `

> or (if wget is not on your system i.e. mac os)

> ` cd lib && curl -C - -O http://bluecove.googlecode.com/files/bluecove-2.1.0.jar `

  * If you are on linux add an additional file:
> ` wget http://bluecove.googlecode.com/files/bluecove-gpl-2.1.0.jar `
  * Build the bluetooth module
> ` ant bluemod `
  * Install the base and bluetooth module
> ` ant install `
  * Run the server
> ` bin/run `