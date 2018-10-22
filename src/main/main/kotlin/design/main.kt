package design

import org.joml.Vector3f

fun main(args: Array<String>) {
    try {
        val vSync = true
        val design = DummyDesign()
        val engine = DesignEngine("BetterWay", 800, 480, vSync, design)
        engine.start()
    } catch (excp: Exception) {
        excp.printStackTrace()
        System.exit(-1)
    }
}