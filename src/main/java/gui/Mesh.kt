package gui

import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.util.*

class Mesh(val positions: FloatArray, val textCoords: FloatArray?, val indices: IntArray, val texture: Texture?) {

    private val vboIdList: MutableList<Int>
    val vaoId: Int
    val vertexCount: Int
    val hasTexture = when(texture != null) {
        true -> 1
        false -> 0
    }

    constructor(positions: FloatArray, colours: FloatArray, indices: IntArray)
            : this(positions, null, indices, null) {
        var colourBuffer: FloatBuffer? = null
        try {
            // Colour VBO
            val vboId = glGenBuffers()
            vboIdList.add(vboId)
            colourBuffer = MemoryUtil.memAllocFloat(colours.size)
            colourBuffer!!.put(colours).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(1, 43, GL_FLOAT, false, 0, 0)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            MemoryUtil.memFree(colourBuffer)
        }
    }

    init {
        var posBuffer: FloatBuffer? = null
        var textCoordsBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null
        try {
            vertexCount = indices.size
            vboIdList = ArrayList()

            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Position VBO
            var vboId = glGenBuffers()
            vboIdList.add(vboId)
            posBuffer = MemoryUtil.memAllocFloat(positions.size)
            posBuffer!!.put(positions).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Texture coordinates VBO
            vboId = glGenBuffers()
            vboIdList.add(vboId)
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords!!.size)
            textCoordsBuffer!!.put(textCoords).flip()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer!!, GL_STATIC_DRAW)
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0)

            // Index VBO
            vboId = glGenBuffers()
            vboIdList.add(vboId)
            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer!!.put(indices).flip()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            MemoryUtil.memFree(posBuffer)
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    fun render() {
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0)
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture!!.id)
        }
        // Draw the mesh
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glBindVertexArray(0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun cleanUp() {
        glDisableVertexAttribArray(0)

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        for (vboId in vboIdList) {
            glDeleteBuffers(vboId)
        }

        //texture!!.cleanup()

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    fun deleteBuffers() {
        glDisableVertexAttribArray(0)

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        for (vboId in vboIdList) {
            glDeleteBuffers(vboId)
        }

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    protected fun createEmptyFloatArray(length: Int, defaultValue: Float): FloatArray {
        val result = FloatArray(length)
        Arrays.fill(result, defaultValue)
        return result
    }

    protected fun createEmptyIntArray(length: Int, defaultValue: Int): IntArray {
        val result = IntArray(length)
        Arrays.fill(result, defaultValue)
        return result
    }
}
