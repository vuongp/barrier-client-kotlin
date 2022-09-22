package barrier.network

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

// TODO: 22/09/2022 refactor
class MessageHandler(
    private val sendChannel: ByteWriteChannel
) {

    suspend fun handle(readPacket: Message) {
        if (readPacket == Message.QueryInfo) {
            sendChannel.writePacket(createClientData())
        } else if (readPacket == Message.KeepAlive) {
            sendChannel.writePacket(createKeepAlivePacket())
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
}