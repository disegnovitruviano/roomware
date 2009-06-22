This client came out of the February '08 Roomware meeting. It's just a one day hack so lots more to be done
People in a roomware room can install the app to see who is present in the room, and send messages to the Roomware 
back channel.

Tested to work on S60 and Ericsson JP-7

TODO's

* Currently the communicator uses http via a public ip (the ip's used the day of the workshop
  were the ExMachina's ip's as this is where it was held) to communicate with the roomware server. 
   This needs to instead be changed so that this is done directly over bluetooth once there is a mechanism
   in place to identify/announce the server to the clients and distinguish it's bluetooth id as the server as 
   opposed to all the other clients in the room.
   
* Improve the messaging functionality.
