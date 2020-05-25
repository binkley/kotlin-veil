package hm.binkley.veil

fun main() {
    NOISY = true

    val fakeDs = FakeDataSource(2, "apple")
    val initialData = fakeDs.fetch("SELECT *")
    val bobs = veil<Bob, Int>(
        ds = fakeDs,
        initialData = initialData,
        idProp = "id",
        "a"
    ) { ds, id ->
        RealBob(ds, id)
    }

    fakeDs.rowOneA = 222 // Data change since initial read

    bobs.forEach {
        println()
        println("== Read veiled, then pierced, then underlying real object")
        println("VEILED: Bob{a=${it.a}, b=${it.b}}")
        println()
        println("PIERCED: Bob{a=${it.a}, b=${it.b}}")
        println()
        println("REAL: $it") // Relies on "toString" forwarding to real obj
    }
}
