package barrier

sealed class Event {
    data class MouseMove(val x: Int, val y: Int): Event()
}