# Structure of a message
`<Integer> | <Message code> | <Optional data>`
1. The first 4 bytes are an integer that denotes the size of the whole message.
2. Next is a String of 4 characters (Except for the hello messages for some reason)
3. (Optional) Extra data

## Example
Todo: too lazy now

# Hello Codes
The first message sent from both the client and server containing version codes for handling compatibility.

## Hello from server
`Barrier%2i%2i`

say hello to client;  primary -> secondary
$1 = protocol major version number supported by server.  
$2 = protocol minor version number supported by server.

## Hello (back) from client
`Barrier%2i%2i%s`

respond to hello from server;
$1 = protocol major version number supported by client.
$2 = protocol minor version number supported by client.  
$3 = client name

# Command codes

## No operation (sent by client)
`CNOP`

Clients sents a message about no operation (Not sure what this means)

## Close connection (sent by server)
`CBYE`

Sounds self explanatory but not sure yet how the client should handle this.

## Enter screen (Sent by server)
`CINN%2i%2i%4i%2i`

entering screen at screen position
$1 = x, $2 = y.  x,y are absolute screen coordinates.  
$3 = sequence number, which is used to order messages between screens.  the secondary screen must return this number with some messages.  
$4 = modifier key mask.  this will have bits set for each toggle modifier key that is activated on entry to the screen.  the secondary screen should adjust its toggle modifiers to reflect that state.

## Exit/leave screen (Sent by server)
`COUT`

Sent when the server exits the clients screen. (i.e. moving the mouse out of clients screen)
the secondary screen should send clipboard data in response to this message for those clipboards that it has grabbed (i.e. has sent a kMsgCClipboard for and has not received a kMsgCClipboard for with a greater sequence number) and that were grabbed or have changed since the last leave.

## Clipboard (Sent by client and server)
`CCLP%1i%4i`

Sent by screen when some other app on that screen grabs a clipboard.  
* $1 = the clipboard identifier
* $2 = sequence number

Secondary screens(clients) must use the sequence number passed in the most recent kMsgCEnter.  
the server always sends 0.

## Clipboard (Sent by client and server)
`CCLP%1i%4i`

## Screensaver started (Sent by server)
`CSEC%1i`

Screensaver on server has started ($1 == 1) or closed ($1 == 0)

## Screen (Sent by server)
`CROP`

Client should reset all of its options to their defaults.

## Info acknowledge (Sent by server)
`CIAK`

sent by primary in response to a secondary screen's kMsgDInfo. this is sent for every <kMsgDInfo>, whether or not the primary had sent a kMsgQInfo.

## Keep Alive (Sent by client and server)
`CALV`

Sent by the server periodically to verify that connections are still up and running.  clients must reply in kind on receipt.  if the server gets an error sending the message or does not receive a reply within a reasonable time then the server disconnects the client.  if the client doesn't receive these (or any message) periodically then it should disconnect from the server.  the appropriate interval is defined by an option (Default 3 seconds).

# Data codes

## Mouse down (Sent by server)
`DMDN%1i`

mouse button pressed
* $1 = ButtonID

## Mouse up (Sent by server)
`DMUP%1i`

mouse button released
* $1 = ButtonID

## Mouse move (Sent by server)
`DMMV%2i%2i`

mouse moved
* $1 = x
* $2 = y x,y are absolute screen coordinates.

## Set options (Sent by server)
`DSOP%4I`

client should set the given option/value pairs.
* $1 = option/value pairs.

## Clipboard data (Sent by server and client)
`DCLP%1i%4i%1i%s`

clipboard data:  primary <-> secondary
* $1 = clipboard identifier.
* $2 = sequence number
* $3 = mark 
* $4 = clipboard data.

the sequence number is 0 when sent by the primary. 
secondary screens should use the sequence number from the most recent kMsgCEnter.

# Query codes

## Query screen code (Sent by server)
`QINF`

client should reply with a kMsgDInfo.

# Error codes

## Incompatible versions (Sent by server)
`EICV%2i%2i`

incompatible versions
$1 = major version of primary
$2 = minor version of primary

## Busy name in use (Sent by server)
`EBSY`

name provided when connecting is already in use

## unknown client (Sent by server)
`EUNK`

name provided when connecting is not in primary's screen configuration map.

## Protocol violation
`EBAD`

Server should disconnect after sending this message

## Todo

Too lazy to do all of them now to be continued

* kMsgDKeyDown        = "DKDN%2i%2i%2i";
* kMsgDKeyDown1_0        = "DKDN%2i%2i";
* kMsgDKeyRepeat        = "DKRP%2i%2i%2i%2i";
* kMsgDKeyRepeat1_0    = "DKRP%2i%2i%2i";
* kMsgDKeyUp            = "DKUP%2i%2i%2i";
* kMsgDKeyUp1_0        = "DKUP%2i%2i";
* kMsgDMouseRelMove    = "DMRM%2i%2i";
* kMsgDMouseWheel        = "DMWM%2i%2i";
* kMsgDMouseWheel1_0    = "DMWM%2i";
* kMsgDInfo            = "DINF%2i%2i%2i%2i%2i%2i%2i;
* kMsgDFileTransfer    = "DFTR%1i%s";
* kMsgDDragInfo        = "DDRG%2i%s";