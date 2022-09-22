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
            else -> {
                Message.Unknown(msgCode)
            }
        }
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