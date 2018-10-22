package gui

import design.GlfWindow
import java.awt.Font

class Gui {

    private val renderer: Renderer = Renderer()

    private val meshs = mutableListOf<Mesh>()

    @Throws(Exception::class)
    fun init() {
        renderer.init()
        var positions = floatArrayOf(
                -0.1f, 0.4f, 0.0f,
                -0.1f, 0.2f, 0.0f,
                0.1f, 0.2f, 0.0f,
                0.1f, 0.4f, 0.0f)
        val colours = floatArrayOf(
                0.5f, 0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.0f, 0.5f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.0f, 0.5f, 0.0f, 0.5f)
        val indices = intArrayOf(
                0, 1, 3,
                3, 1, 2)
        val textCoords = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f)

        val texture = Texture("/icons/icons8-drag-48.png")
        meshs.add(Mesh(positions, /*colours,*/ textCoords, indices, texture))

    }

    fun render(window: GlfWindow) {
        renderer.render(window, meshs)
    }

    fun cleanup() {
        renderer.cleanup()
        for (mesh in meshs) {
            mesh.cleanUp()
        }
    }

}