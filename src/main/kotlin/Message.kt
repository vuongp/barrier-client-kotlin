sealed class Message()

data class ServerHelloMessage(
    val majorVersion: Short,
    val minorVersion: Short
) : Message()

// query screen info:  primary -> secondary
// client should reply with a kMsgDInfo.
object QueryInfoMessage : Message()

// keep connection alive:  primary <-> secondary
// sent by the server periodically to verify that connections are still
// up and running.  clients must reply in kind on receipt.  if the server
// gets an error sending the message or does not receive a reply within
// a reasonable time then the server disconnects the client.  if the
// client doesn't receive these (or any message) periodically then it
// should disconnect from the server.  the appropriate interval is
// defined by an option.
object KeepAliveMessage : Message()

data class UnknownMessage(
    val message: String
) : Message()