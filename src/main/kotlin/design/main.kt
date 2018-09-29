package design

fun main(args: Array<String>) {
    try {
        val vSync = true
        val design = DummyDesign()
        val engine = DesignEngine("BetterWay", 600, 480, vSync, design)
        engine.start()
    } catch (excp: Exception) {
        excp.printStackTrace()
        System.exit(-1)
    }

}