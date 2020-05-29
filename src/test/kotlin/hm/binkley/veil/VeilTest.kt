package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VeilTest {
    private val fakeDs = FakeBobDataSource(-1, "apple")

    @Test
    fun `should veil first time, and stay veiled`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs(fakeDs).pierceable

        assertEquals(veiledRowOneA, bobs.first().a)

        fakeDs.rowOneA = piercedRowOneA

        assertEquals(veiledRowOneA, bobs.first().a)
    }

    @Test
    fun `should pierce second time`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
        fakeDs.rowOneA = veiledRowOneA

        val bobs = bobs(fakeDs).pierceable
        // Sequences are not restartable -- TODO: Use List?
        val bobOne = bobs.first()

        fakeDs.rowOneA = piercedRowOneA
        bobOne.b // Pierce the veil

        assertEquals(piercedRowOneA, bobOne.a)
    }

    @Test
    fun `should not veil`() {
        val realRowOneB = "apple"
        val bobs = bobs(fakeDs).pierceable

        assertEquals(realRowOneB, bobs.first().b)
    }

    @Test
    fun `should not pierce second time`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
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
