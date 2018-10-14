package design

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import design.Enums.*
import org.joml.Quaternionf
import org.joml.Vector3f

class Render {

    private val transformation = Transform()

    private lateinit var shaderProgram: Shader

    private lateinit var hudShaderProgram: Shader

    private val Z_NEAR = 0.01f

    private val Z_FAR = 1000f

    val FOV = Math.toRadians(60.0).toFloat()

    var projMat4 = Matrix4f()

    var viewMat4 = Matrix4f()

    val quaternionf: Quaternionf = Quaternionf(0f, 0f, 0f, 1f)

    @Throws(Exception::class)
    fun init() {
        // Create shader
        shaderProgram = Shader()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex0.glsl"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment0.glsl"))
        shaderProgram.link()

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("worldMatrix")
        //shaderProgram.createUniform("texture_sampler")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(window: GlfWindow, mode: Mode, camera: Camera, designItems: List<DesignItem>) {
        clear()

        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        // Bind(finalize) the data
        shaderProgram.bind()

        // Update projection matrix
        val projectionMatrix = transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        //val projectionMatrix = transformation.getOrthographicMatrix(window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)
        projMat4 = transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), 1f, 2f)

        // Set world matrix for this item
        val worldMatrix = when(mode.b) {
            true -> transformation.getWorldMatrix(Vector3f(camera.position).negate(), quaternionf)
            false -> transformation.getWorldMatrix(camera)
        }
        shaderProgram.setUniform("worldMatrix", worldMatrix)
        viewMat4 = Matrix4f(worldMatrix)

        // Update world matrix & Render each gameItem
        for (item in designItems) {
            // Render
            item.mesh.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}
