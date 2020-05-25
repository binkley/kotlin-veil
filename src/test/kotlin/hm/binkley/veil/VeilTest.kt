package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VeilTest {
    @Test
    fun `should veil first time`() {
        val veiledRowOneA = 2
        val fakeDs = FakeDataSource(veiledRowOneA, "apple")
        val bobs = bobs(fakeDs)

        assertEquals(veiledRowOneA, bobs.first().a)
    }

    @Test
    fun `should pierce second time`() {
        val veiledRowOneA = 2
        val piercedRowOneA = 222
        val fakeDs = FakeDataSource(veiledRowOneA, "apple")
        val bobs = bobs(fakeDs)
        val bobOne = bobs.first()

        bobOne.b // Pierce the veil
        fakeDs.rowOneA = piercedRowOneA

        assertEquals(piercedRowOneA, bobOne.a)
    }

    @Test
    fun `should not veil`() {
        val realRowOneB = "apple"
        val fakeDs = FakeDataSource(2, realRowOneB)
        val bobs = bobs(fakeDs)

        assertEquals(realRowOneB, bobs.first().b)
    }
}

fun bobs(fakeDs: DataSource) = veil<Bob, Int>(
    ds = fakeDs,
    initialData = fakeDs.fetch("SELECT * FROM Bob"),
    idProp = "id",
    "a"
) { ds, id ->
    RealBob(ds, id)
}
