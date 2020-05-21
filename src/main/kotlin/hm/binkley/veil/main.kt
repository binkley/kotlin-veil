package hm.binkley.veil

fun main() {
    val ds = FakeDataSource()
    val bobs = veil<Bob>(::RealBob, ds, ds.fetch("SELECT *"), "x")

    bobs.forEach {
        println("VEILED: Bob{x=${it.x}, y=${it.y}}")
        println("REAL: $it")
    }
}
