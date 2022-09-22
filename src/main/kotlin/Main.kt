import barrier.*
import barrier.network.Message
import barrier.network.MessageByteReader
import barrier.network.MessageHandler
import barrier.network.MessagePacketCreator
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    BarrierClient("ANTON")
        .connect("localhost", 24800)
        .addEventListener {

        }
}