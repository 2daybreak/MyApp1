package design

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*

class Render {

    private val transformation = Transform()

    private lateinit var shaderProgram: Shader

    private val FOV = Math.toRadians(60.0).toFloat()

    private val Z_NEAR = 0.01f

    private val Z_FAR = 1000f

    lateinit var projectMat4: Matrix4f

    val perspective = true

    @Throws(Exception::class)
    fun initShader() {
        // Create shader
        shaderProgram = Shader()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex0.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment0.fs"))
        shaderProgram.link()

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("worldMatrix")
        //shaderProgram.createUniform("texture_sampler")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(window: GlfWindow, designItems: List<DesignItem>) {
        clear()

        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        // Bind(finalize) the data
        shaderProgram.bind()

        // Update projection Matrix
        val projectionMatrix = when(perspective) {
            true -> transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
            false -> transformation.getOrthographicMatrix(window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        }
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)
        projectMat4 = projectionMatrix

        //shaderProgram.setUniform("texture_sampler", 0)
        // Render each gameItem
        for (item in designItems) {
            // Set world matrix for this item
            val worldMatrix = transformation.getWorldMatrix(
                    item.position,
                    item.quaternion)
            shaderProgram.setUniform("worldMatrix", worldMatrix)
            // Render
            item.grid.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}
