package barrier

sealed class Event {
    class MouseMove(x: Int, y: Int): Event()
}