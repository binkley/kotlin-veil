package hm.binkley.veil

fun main() {
    NOISY = true

    val fakeDs = FakeBobDataSource(2, "apple")
    val initialData = fakeDs.fetch(SELECT_ALL_BOBS)
    val pierceableBobs = pierceableBobs(fakeDs, initialData)
    val unpierceableBobs = unpierceableBobs(fakeDs, initialData)

    // Cannot inline `pierceableBobs` or `unpierceableBobs` before updating
    // the underlying fake data
    fakeDs.rowOneA = 222

    println()
    println("PIERCED")
    println("-------")
    pierceableBobs.forEach {
        dumpVeiled(it, true)
    }
    println()
    println("UNPIERCED")
    println("---------")
    unpierceableBobs.forEach {
        dumpVeiled(it, false)
    }
}

private fun pierceableBobs(
    fakeDs: FakeBobDataSource,
    initialData: Sequence<Map<String, Any?>>
) = bobs(true, fakeDs, initialData)

private fun unpierceableBobs(
    fakeDs: FakeBobDataSource,
    initialData: Sequence<Map<String, Any?>>
) = bobs(false, fakeDs, initialData)

private fun bobs(
    pierceable: Boolean,
    fakeDs: FakeBobDataSource,
    initialData: Sequence<Map<String, Any?>>
): Sequence<Bob> {
    val pierceableBobs = veil<Bob, Int>(
        pierceable = pierceable,
        ds = fakeDs,
        initialData = initialData,
        idProp = "id",
        "a"
    ) { ds, id ->
        RealBob(ds, id)
    }
    return pierceableBobs
}

private fun dumpVeiled(it: Bob, pierceable: Boolean) {
    println()
    println(
        "== Read veiled, then pierced if $pierceable, then underlying real object"
    )
    println("VEILED: Bob{a=${it.a}, b=${it.b}}")
    println()
    println("MAYBE-PIERCED: Bob{a=${it.a}, b=${it.b}}")
    println()
    println("REAL: $it") // Relies on "toString" forwarding to real obj
}
