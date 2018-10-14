package design

class Timer {

    var lastLoopTime: Double = 0.toDouble()
        private set

    val time: Double
        get() = System.nanoTime() / 1000_000_000.0

    val elapsedTime: Float
        get() {
            val time = time
            val elapsedTime = (time - lastLoopTime).toFloat()
            lastLoopTime = time
            return elapsedTime
        }

    fun init() {
        lastLoopTime = time
    }
}