package barrier.network

import barrier.network.Message.*
import io.ktor.utils.io.core.*

class MessagePacketCreator {

    // TODO: 22/09/2022 Refactor to move away from when/switch statements
    fun createPacketFromMessage(message: Message): ByteReadPacket {
        return when (message) {
            is ClientHello -> createClientHello(message.name)
            is CloseConnection -> TODO()
            is EnterScreen -> TODO()
            is ExitScreen -> TODO()
            is KeepAlive -> TODO()
            is NoOperation -> TODO()
            is QueryInfo -> TODO()
            is ServerHello -> TODO()
            is Unknown -> TODO()
            is MouseMove -> TODO()
            else -> TODO()
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
}
