package barrier.network

sealed class Message {

    //region Hello Messages
    /**
     * sent by server
     * format "Barrier%2i%2i"
     * $1 = protocol major version number supported by server.
     * $2 = protocol minor version number supported by server.
     */
    data class ServerHello(
        val majorVersion: Short, val minorVersion: Short
    ) : Message()

    /**
     * sent by client
     * format "Barrier%2i%2i%s"
     * respond to hello from server;
     * $1 = protocol major version number supported by client.
     * $2 = protocol minor version number supported by client.
     * $3 = client name
     */
    data class ClientHello(
        val majorVersion: Short, val minorVersion: Short, val name: String
    ) : Message()
    //endregion

    //region Command codes
    /**
     * sent by server
     * format "CNOP"
     * Clients sents a message about no operation (Not sure what this means yet)
     */
    object NoOperation : Message()

    /**
     * sent by server
     * format "CBYE"
     * TODO: add comments; Sounds self explanatory but not sure yet how the client should handle this.
     */
    object CloseConnection : Message()

    /**
     * sent by server
     * format "CINN%2i%2i%4i%2i"
     * entering screen at screen position
     * $1 = x
     * $2 = y. x,y are absolute screen coordinates.
     * $3 = sequence number, which is used to order messages between screens. the secondary screen must return this number with some messages.
     * $4 = modifier key mask. this will have bits set for each toggle modifier key that is activated on entry to the screen.
     *      the secondary screen should adjust its toggle modifiers to reflect that state.
     */
    data class EnterScreen(
        val x: Short,
        val y: Short,
        val sequenceNumber: Int,
        val modifierKeyMask: Short
    ) : Message()

    /**
     * sent by server
     * format "COUT"
     * Sent when the server exits the clients screen. (i.e. moving the mouse out of clients screen)
     * the secondary screen should send clipboard data in response to this message for those clipboards that it has grabbed (i.e. has sent a kMsgCClipboard for and has not received a kMsgCClipboard for with a greater sequence number) and that were grabbed or have changed since the last leave.
     */
    object ExitScreen : Message()

    /**
     * Client should reset all of its options to their defaults.
     */
    object ResetOptions : Message()

    /**
     * sent by server
     * format "CIAK"
     * sent by server in response to a secondary screen's info.
     * this is sent for every screen info, whether or not the server had sent a Query for info.
     */
    object InfoAcknowledge : Message()

    /**
     * sent by both server and client
     * format "CALV"
     * Sent by the server periodically to verify that connections are still up and running.
     * Clients must reply in kind on receipt. if the server gets an error sending the message
     *  or does not receive a reply within a reasonable time then the server disconnects the client.
     * If the client doesn't receive these (or any message) periodically then it should disconnect from the server.
     *  the appropriate interval is defined by an option (Default 3 seconds).
     */
    object KeepAlive : Message()
    //endregion

    //region Data codes
    /**
     * sent by server
     * format "DMMV%2i%2i"
     * mouse move x,y are absolute screen coordinates.
     * $1 = x
     * $2 = y
     */
    data class MouseMove(
        val x: Short,
        val y: Short
    ) : Message()

    /**
     * sent by server
     * mouse button pressed
     * $1 = ButtonID (1 is left mouse, 3 is right mouse click)
     */
    data class MouseDown(val buttonId: Byte) : Message()

    /**
     * sent by server
     * mouse button released
     * $1 = ButtonID (1 is left mouse, 3 is right mouse click)
     */
    data class MouseUp(val buttonId: Byte) : Message()
    //endregion

    /**
     * query screen info client should reply with a kMsgDInfo.
     */
    object QueryInfo : Message()

    /**
     * Unknown message that aren't implemented yet
     */
    @Deprecated("Should be removed when all codes have been implemented")
    data class Unknown(
        val message: String
    ) : Message()
}

