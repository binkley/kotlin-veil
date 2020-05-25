package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VeilTest {
    @Test
    fun `should veil first time`() {
        val veiledRowOneA = 2
        val fakeDs = FakeBobDataSource(veiledRowOneA, "apple")
        val bobs = pierceableBobs(fakeDs)

        assertEquals(veiledRowOneA, bobs.first().a)
    }

    @Test
    fun `should pierce second time`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
        val fakeDs = FakeBobDataSource(veiledRowOneA, "apple")
        val bobs = pierceableBobs(fakeDs)
        val bobOne = bobs.first()

        bobOne.b // Pierce the veil
        fakeDs.rowOneA = piercedRowOneA

        assertEquals(piercedRowOneA, bobOne.a)
    }

    @Test
    fun `should not veil`() {
        val realRowOneB = "apple"
        val fakeDs = FakeBobDataSource(2, realRowOneB)
        val bobs = pierceableBobs(fakeDs)

        assertEquals(realRowOneB, bobs.first().b)
    }

    @Test
    fun `should not pierce second time`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
        val fakeDs = FakeBobDataSource(veiledRowOneA, "apple")
        val bobs = unpierceableBobs(fakeDs)
        val bobOne = bobs.first()

        bobOne.b // Pierce the veil
        fakeDs.rowOneA = piercedRowOneA

        assertEquals(veiledRowOneA, bobOne.a)
    }
}

private fun pierceableBobs(fakeDs: DataSource) = bobs(true, fakeDs)
private fun unpierceableBobs(fakeDs: DataSource) = bobs(false, fakeDs)

private fun bobs(pierceable: Boolean, fakeDs: DataSource) = veil<Bob, Int>(
    pierceable = pierceable,
    ds = fakeDs,
    initialData = fakeDs.fetch(SELECT_ALL_BOBS),
    idProp = "id",
    "a"
) { ds, id ->
    RealBob(ds, id)
}
