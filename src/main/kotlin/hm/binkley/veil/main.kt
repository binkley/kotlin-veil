package hm.binkley.veil

fun main() {
    val ds = FakeDataSource()
    val bobs = veil<Bob>(::RealBob, ds, ds.fetch("SELECT *"), "x")

    bobs.forEach {
        println()
        println("==")
        println("VEILED: Bob{x=${it.x}, y=${it.y}}")
        println()
        println("PIERCED: Bob{x=${it.x}, y=${it.y}}")
        println()
        println("REAL: $it")
    }
}
