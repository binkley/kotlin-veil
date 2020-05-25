package hm.binkley.veil

fun main() {
    NOISY = true

    val fakeDs = FakeDataSource(2, "apple")
    val initialData = fakeDs.fetch("SELECT * FROM Bob")
    val pierceableBobs = veil<Bob, Int>(
        pierceable = true,
        ds = fakeDs,
        initialData = initialData,
        idProp = "id",
        "a"
    ) { ds, id ->
        RealBob(ds, id)
    }
    val unpierceableBobs = veil<Bob, Int>(
        pierceable = false,
        ds = fakeDs,
        initialData = initialData,
        idProp = "id",
        "a"
    ) { ds, id ->
        RealBob(ds, id)
    }

    fakeDs.rowOneA = 222 // Data change since initial read

    pierceableBobs.forEach {
        dumpVeiled(it, true)
    }
    unpierceableBobs.forEach {
        dumpVeiled(it, false)
    }
}

private fun dumpVeiled(it: Bob, pierceable: Boolean) {
    println()
    println("== Read veiled, then pierced if $pierceable, then underlying real object")
    println("VEILED: Bob{a=${it.a}, b=${it.b}}")
    println()
    println("MAYBE-PIERCED: Bob{a=${it.a}, b=${it.b}}")
    println()
    println("REAL: $it") // Relies on "toString" forwarding to real obj
}
