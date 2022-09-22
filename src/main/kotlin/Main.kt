import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val socket = aSocket(selectorManager).tcp().connect("192.168.178.248", 24800)

    val sendChannel = socket.openWriteChannel(autoFlush = true)
    val receiveChannel = socket.openReadChannel()

    sendChannel.writePacket(createClientHello("ANTON"))
    launch {
        while (!socket.isClosed) {
            val readPacket = receiveChannel.readPacket()
            println("server: $readPacket")
            if (readPacket == QueryInfoMessage) {
                println("ClientData sent")
                sendChannel.writePacket(createClientData())
            } else if (readPacket == KeepAliveMessage) {
                println("Keep alive sent")
                sendChannel.writePacket(createKeepAlivePacket())
            }
        }
    }
}

@OptIn(InternalAPI::class)
private suspend fun ByteReadChannel.readPacket(): Message? {
    val nrOfBytes = readInt()
    val data = ByteArray(nrOfBytes)
    readFully(data)
    return when(val msgCode = String(data, 0, 4)) {
        "Barr" -> {
            ServerHelloMessage(data.readShort(7), data.readShort(9))
        }
        "QINF" -> QueryInfoMessage
        "CALV" -> KeepAliveMessage
        else -> {
            UnknownMessage(msgCode)
        }
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

private fun createKeepAlivePacket(): ByteReadPacket {
    return buildPacket {
        writeInt(4)
        writeText("CALV")
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