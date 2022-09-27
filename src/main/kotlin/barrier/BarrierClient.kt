package barrier

import barrier.network.Message
import barrier.network.MessageByteReader
import barrier.network.MessageHandler
import barrier.network.MessagePacketCreator
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

// TODO: 22/09/2022 add comments
class BarrierClient(
    private val name: String,
    private val majorVersion: Short = 1,
    private val minorVersion: Short = 6,
) {
    private val eventListeners = mutableListOf<(Event) -> Unit>()
    private val messageByteReader = MessageByteReader()

    fun addEventListener(eventListener: (Event) -> Unit) {
        eventListeners.add(eventListener)
    }

    suspend fun connect(hostname: String, port: Int = 24800) {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager)
            .tcp()
            .connect(hostname, port)

        val sendChannel = socket.openWriteChannel(autoFlush = true)
        val receiveChannel = socket.openReadChannel()

        val messageHandler = MessageHandler(sendChannel)
        val clientHelloMessage = Message.ClientHello(majorVersion, minorVersion, name)
        val clientHelloPacket = MessagePacketCreator().createPacketFromMessage(clientHelloMessage)

        sendChannel.writePacket(clientHelloPacket)
        while (!socket.isClosed) {
            val readPacket = messageByteReader.readPacket(receiveChannel)
            messageHandler.handle(readPacket)

            if (readPacket is Message.MouseMove) {
                eventListeners.forEach {
                    it.invoke(Event.MouseMove(readPacket.x.toInt(), readPacket.y.toInt()))
                }
            }
        }
    }

}
