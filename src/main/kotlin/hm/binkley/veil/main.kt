package hm.binkley.veil

fun main() {
    val ds = FakeDataSource()
    val initialData = ds.fetch("SELECT *")
    val bobs = veil<Bob, Int>(
        realCtor = ::RealBob,
        ds = ds,
        initialData = initialData,
        idProp = "id",
        "x"
    )

    bobs.forEach {
        println()
        println("== Read veiled, then pierced, then underlying real object")
        println("VEILED: Bob{x=${it.x}, y=${it.y}}")
        println()
        println("PIERCED: Bob{x=${it.x}, y=${it.y}}")
        println()
        println("REAL: $it") // Relies on "toString" forwarding to real obj
    }
}
