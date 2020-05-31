package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private const val veiledRowOneA = 2
private const val piercedRowOneA = 222
private const val realRowOneB = "apple"

private val fakeDs = FakeBobDataSource(-1, realRowOneB)

internal class VeilTest {
    @Test
    fun `should veil first time, and stay veiled`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs().pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        assertEquals(veiledRowOneA, bobOne.a)

        fakeDs.rowOneA = piercedRowOneA // No effect

        assertEquals(veiledRowOneA, bobOne.a)
    }

    @Test
    fun `should pierce second time when pierceable`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs().pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        fakeDs.rowOneA = piercedRowOneA
        bobOne.b // Pierce the veil with an unveiled property

        assertEquals(piercedRowOneA, bobOne.a)
    }

    @Test
    fun `should not veil when not veilable`() {
        val bobs = bobs().pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        assertEquals(realRowOneB, bobOne.b)
    }

    @Test
    fun `should not pierce second time when unpierceable`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs().unpierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        bobOne.b // Pierce the veil
        fakeDs.rowOneA = piercedRowOneA

        assertEquals(veiledRowOneA, bobOne.a)
    }

    @Test
    fun `should reflect`() {
        val bobs = bobs().pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        @Suppress("UNCHECKED_CAST")
        bobOne as Veilable<Bob>

        assertEquals(false, bobOne.pierced)
        assertEquals(true, bobOne.veiled(Bob::a))
        assertEquals(false, bobOne.veiled(Bob::b))
        assertEquals(false, bobOne.veiled(Bob::veiled))
    }

    @Test
    fun `should not hide props because of reflection`() {
        val bobs = bobs().unpierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        assertEquals(17, bobOne.veiled)
    }
}

private fun bobs() = object {
    /** NB &mdash; IntelliJ incorrectly believes this prop is unused. */
    @Suppress("unused")
    val pierceable: Sequence<Bob>
        get() = bobs(true)

    /** NB &mdash; IntelliJ incorrectly believes this prop is unused. */
    @Suppress("unused")
    val unpierceable: Sequence<Bob>
        get() = bobs(false)

    private fun bobs(pierceable: Boolean) =
        veil<Bob, Int>(
            pierceable = pierceable,
            ds = fakeDs,
            initialData = fakeDs.fetch(SELECT_ALL_BOBS),
            idProp = "id",
            "a"
        ) { ds, id ->
            RealBob(ds, id)
        }
}
