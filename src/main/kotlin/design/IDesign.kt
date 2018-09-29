package design

interface IDesign {

    @Throws(Exception::class)
    fun init(window: GlfWindow)

    fun input(window: GlfWindow, mouse: Mouse)

    fun update(interval: Float, mouse: Mouse)

    fun render(window: GlfWindow)

    fun cleanup()
}