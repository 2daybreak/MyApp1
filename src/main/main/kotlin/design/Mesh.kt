package design

import java.lang.Float
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class Mesh(positions: FloatArray, colours: FloatArray) {

    private val vao: Int

    private var vbo: Int

    private val size = positions.size / 3

    init {

        lateinit var posBuffer: FloatBuffer
        lateinit var colourBuffer: FloatBuffer

        try {

            vao = glGenVertexArrays()
            glBindVertexArray(vao)

            // Position VBO
            vbo = glGenBuffers()
            posBuffer = MemoryUtil.memAllocFloat(positions.size)
            posBuffer.put(positions).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0)

            // Colour VBO
            vbo = glGenBuffers()
            colourBuffer = MemoryUtil.memAllocFloat(colours.size)
            colourBuffer.put(colours).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0)

            // Bind buffer (Finalize data)
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)

        } finally {
            MemoryUtil.memFree(posBuffer)
            MemoryUtil.memFree(colourBuffer)
        }
    }

    fun render(glEnum: Int) {
        // Draw the mesh
        glBindVertexArray(vao)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawArrays(glEnum, 0, size)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)
    }

    fun cleanUp() {
        glDisableVertexAttribArray(0)

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vbo)

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vao)
    }
}
