package barrier.network

import io.ktor.utils.io.*

class MessageByteReader {

    suspend fun readPacket(byteReadChannel: ByteReadChannel): Message {
        val nrOfBytes = byteReadChannel.readInt()
        val data = ByteArray(nrOfBytes)
        byteReadChannel.readFully(data)

        return createMessage(data)
    }

    // TODO: Refactor to not use a when/switch statement maybe factory pattern?
    fun createMessage(data: ByteArray): Message {
        val msgCode = String(data, 0, 4)
        return when (msgCode) {
            "Barr" -> Message.ServerHello(data.readShort(7), data.readShort(9))
            "QINF" -> Message.QueryInfo
            "CALV" -> Message.KeepAlive
            "DMMV" -> Message.MouseMove(data.readShort(4), data.readShort(6))
            "DMUP" -> Message.MouseUp(data[4])
            "DMDN" -> Message.MouseDown(data[4])
            "COUT" -> Message.ExitScreen
            "CIAK" -> Message.InfoAcknowledge
            "CROP" -> Message.ResetOptions
            "CINN" -> Message.EnterScreen(
                data.readShort(4),
                data.readShort(6),
                0,
                data.readShort(12)
            )
            else -> {
                Message.Unknown(msgCode)
            }
        }
    }

    private fun ByteArray.readInt(offset: Int): Int {
        var result = 0
        repeat(4) {
            result = result or (this[it + offset].toInt() shl 8 * it)
        }
        return result
    }

    /**
     * Stolen from internal ktor api :)
     * Read [Short] in network order(BE) with specified [offset] from [ByteArray].
     */
    private fun ByteArray.readShort(offset: Int): Short {
        val result = ((this[offset].toInt() and 0xFF) shl 8) or (this[offset + 1].toInt() and 0xFF)
        return result.toShort()
    }
}