package hm.binkley.veil

fun main() {
    NOISY = true

    val fakeDs = FakeDataSource()
    val initialData = fakeDs.fetch("SELECT *")
    val bobs = veil<Bob, Int>(
        ds = fakeDs,
        initialData = initialData,
        idProp = "id",
        "x"
    ) { ds, id ->
        RealBob(ds, id)
    }

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
