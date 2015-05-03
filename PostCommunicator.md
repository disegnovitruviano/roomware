# Introduction #

With the latest version of the Roomware server we now support the posting of events directly to an URI. This allows you to write an application that reacts on events as they ahppen in realtime as opposed to polling an XML file for changes.

# Details #

To start using the Post Communicator you simple add the post communicator to your list of communicators:

```
communicators: console, http, post
```

The name you chose for your communicator will still have to be attached to the class that contains the communicator code:

```
post-class: org.roomwareproject.communicator.post.Communicator
```

And finally you will have to set the configuration options for your new Communicator:

```
# post class definition
post-host: 192.168.2.152
post-port: 9019
post-path: /eventlist.php
```

The above configuration will post the following URI: `http://192.168.2.152:9019/eventlist.php`.

After you've got that working you could even go really wild and add another post communicator that posts events to another URI entrirely! Madness we know...

```
# comma seperated list of communicators to load
communicators: console, http, post, post2

post2-class: org.roomwareproject.communicator.post.Communicator
post2-host: 192.168.2.152
post2-port: 9019
post2-path: /debug.php
```

# Handling POST calls in your script, aka wtf is being sent #
The Roomware server notifies you in 2 scenarios, when a message is sent or when one of it's property changes. For instance on adding/removing you get a PropertyChangeEvent with a propertyName of inrange with a newValue/oldValue.

When a message is sent around:
| event | hardcoded value, "messageReceived" |
|:------|:-----------------------------------|
| message | the message for the device |
| date | timestamp for the message |
| deviceAddress | the inner device address |
| deviceName | the device's name |

When a property on a device changes:
| event | hardcoded value, "propertyChange" |
|:------|:----------------------------------|
| oldValue | previous value for this property |
| newValue | new value for this property |
| deviceAddress | the inner device address |
| deviceName | the device's name |
| propertyName | the property that changed |

Both event's register null values as "(null)", so you can check against that for a proper null value, this also means that a post value is always set for all of the above keys.