import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun main(args: Array<String>) = runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val socket = aSocket(selectorManager).tcp().connect("192.168.178.248", 24800)

    val sendChannel = socket.openWriteChannel(autoFlush = true)
    val receiveChannel = socket.openReadChannel()

    sendChannel.writePacket(createClientHello("ANTON"))
    receiveChannel.readAvailable { byteBuffer ->
        print("server: " + byteBuffer.int + ",")
        repeat(7) {
            print(byteBuffer.get().toInt().toChar())
        }
        print(",${byteBuffer.short},${byteBuffer.short}")
        println()
    }
    delay(1000)
    sendChannel.writePacket(createClientData())
    while (!socket.isClosed) {
        delay(10)
        receiveChannel.awaitContent()
        receiveChannel.readAvailable { byteBuffer ->
            val numberOfBytes = byteBuffer.int
            print("${byteBuffer.limit()} server: $numberOfBytes,")
            repeat(4) {
                print(byteBuffer.get().toInt().toChar())
            }
            repeat(numberOfBytes - 4) {
                print("[${byteBuffer.get()}]")
            }
            println()
        }
        receiveChannel.awaitContent()
    }
}

private fun createClientHello(name: String): ByteReadPacket {
    return buildPacket {
        writeInt(15 + name.length)
        writeText("Barrier")
        writeShort(1)
        writeShort(6)
        writeInt(name.length)
        writeText(name)
    }
}

private fun createNoOperationPacket(): ByteReadPacket {
    return buildPacket {
        writeInt(4)
        writeText("CNOP")
    }
}

private fun createClientData(): ByteReadPacket {
    return buildPacket {
        writeInt(18)
        writeText("DINF")
        // Coordinate of leftmost pixel on secondary screen,
        writeShort(0)
        // Coordinate of topmost pixel on secondary screen,
        writeShort(0)
        // Width of secondary screen in pixels,
        writeShort(1536)
        // Height of secondary screen in pixels,
        writeShort(864)
        // Size of warp zone, (obsolete)
        writeShort(0)
        // x position of the mouse
        writeShort(0)
        // y position of the mouse
        writeShort(0)
    }
}

// client data:  secondary -> primary
// $1 = coordinate of leftmost pixel on secondary screen,
// $2 = coordinate of topmost pixel on secondary screen,
// $3 = width of secondary screen in pixels,
// $4 = height of secondary screen in pixels,
// $5 = size of warp zone, (obsolete)
// $6, $7 = the x,y position of the mouse on the secondary screen.
//
// the secondary screen must send this message in response to the
// kMsgQInfo message.  it must also send this message when the
// screen's resolution changes.  in this case, the secondary screen
// should ignore any kMsgDMouseMove messages until it receives a
// kMsgCInfoAck in order to prevent attempts to move the mouse off
// the new screen area.
//extern const char*        kMsgDInfo;
//DINF%2i%2i%2i%2i%2i%2i%2i