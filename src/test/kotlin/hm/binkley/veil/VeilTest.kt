package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private const val veiledRowOneA = 2
private const val piercedRowOneA = 222
private const val realRowOneB = "apple"

internal class VeilTest {
    private val fakeDs = FakeBobDataSource(-1, realRowOneB)

    @Test
    fun `should veil first time, and stay veiled`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs(fakeDs).pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        assertEquals(veiledRowOneA, bobOne.a)

        fakeDs.rowOneA = piercedRowOneA // No effect

        assertEquals(veiledRowOneA, bobOne.a)
    }

    @Test
    fun `should pierce second time when pierceable`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs(fakeDs).pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        fakeDs.rowOneA = piercedRowOneA
        bobOne.b // Pierce the veil with an unveiled property

        assertEquals(piercedRowOneA, bobOne.a)
    }

    @Test
    fun `should not veil when not veilable`() {
        val bobs = bobs(fakeDs).pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        assertEquals(realRowOneB, bobOne.b)
    }

    @Test
    fun `should not pierce second time when unpierceable`() {
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs(fakeDs).unpierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        bobOne.b // Pierce the veil
        fakeDs.rowOneA = piercedRowOneA

        assertEquals(veiledRowOneA, bobOne.a)
    }
}

private fun bobs(fakeDs: DataSource) = object {
    val pierceable: Sequence<Bob>
        get() = bobs(true, fakeDs)

    val unpierceable: Sequence<Bob>
        get() = bobs(false, fakeDs)

    private fun bobs(pierceable: Boolean, fakeDs: DataSource) =
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
