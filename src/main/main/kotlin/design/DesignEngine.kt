package design

class DesignEngine @Throws(Exception::class)
constructor(windowTitle: String, width: Int, height: Int, vSync: Boolean, private val design: IDesign) : Runnable {

    private val window: GlfWindow = GlfWindow(windowTitle, width, height, vSync)

    private val gameLoopThread: Thread = Thread(this, "GAME_LOOP_THREAD")

    private val timer: Timer = Timer()

    private val mouse: Mouse = Mouse()

    fun start() {
        val osName = System.getProperty("os.name")
        if (osName.contains("Mac")) {
            gameLoopThread.run()
        } else {
            gameLoopThread.start()
        }
    }

    override fun run() {
        try {
            init()
            gameLoop()
        } catch (excp: Exception) {
            excp.printStackTrace()
        } finally {
            cleanup()
        }
    }

    @Throws(Exception::class)
    protected fun init() {
        window.init()
        timer.init()
        design.init(window)
        mouse.init(window)
    }

    protected fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / TARGET_UPS

        while (!window.windowShouldClose()) {
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime

            input()

            while (accumulator >= interval) {
                update(interval, mouse)
                accumulator -= interval
            }

            render()

            if (!window.isvSync()) {
                sync()
            }
        }
    }

    protected fun cleanup() {
        design.cleanup()
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }

        }
    }

    protected fun input() {
        mouse.input()
        design.input(window, mouse)
    }

    protected fun update(interval: Float, mouse: Mouse) {
        design.update(interval, mouse)
    }

    protected fun render() {
        design.render(window)
        window.update()
    }

    companion object {

        val TARGET_FPS = 75

        val TARGET_UPS = 30
    }
}
