package hm.binkley.veil

private val fakeDs = FakeBobDataSource(2, "apple")
private val initialData = fakeDs.fetch(SELECT_ALL_BOBS)
private val pierceableBobs = bobs().pierceable
private val unpierceableBobs = bobs().unpierceable

fun main() {
    NOISY = true

    fakeDs.rowOneA = 222

    println()
    println("NOTE: Bob has props: a, b, veiled.")
    println(
        "NOTE: Prop veiled is to show that the data value is not masked " +
            "by Veilable."
    )

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

private fun bobs() = object {
    val pierceable: Sequence<Bob> get() = bobs(true)
    val unpierceable: Sequence<Bob> get() = bobs(false)

    private fun bobs(pierceable: Boolean) =
        veil<Bob, Int>(
            pierceable = pierceable,
            ds = fakeDs,
            initialData = initialData,
            idProp = "id",
            "a"
        ) { ds, id ->
            RealBob(ds, id)
        }
}

private fun dumpVeiled(it: Bob, pierceable: Boolean) {
    println()
    println(
        "== Read veiled, then pierced if pierceable ($pierceable), then " +
            "underlying real object"
    )
    println("VEILED: Bob{a=${it.a}, b=${it.b}, veiled=${it.veiled}}")
    println()
    println("MAYBE-PIERCED: Bob{a=${it.a}, b=${it.b}, veiled=${it.veiled}}")
    println()
    println("REAL: $it") // Relies on "toString" forwarding to real obj
    println()
    @Suppress("UNCHECKED_CAST")
    it as Veilable<Bob>
    println("PIERCED? ${it.pierced}")
    println("VEILED-A? ${it.veiled(Bob::a)}")
    println("VEILED-B? ${it.veiled(Bob::b)}")
    println("IS REFLECTIVE VEILED MASKED? ${it.veiled(Bob::veiled)}")
}
