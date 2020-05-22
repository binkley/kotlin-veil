package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VeilTest {
    @Test
    fun `should veil first time`() {
        val veiledRowOneX = 2
        val fakeDs = FakeDataSource(veiledRowOneX, "apple")
        val bobs = bobs(fakeDs)

        assertEquals(veiledRowOneX, bobs.first().x)
    }

    @Test
    fun `should pierce second time`() {
        val veiledRowOneX = 2
        val piercedRowOneX = 222
        val fakeDs = FakeDataSource(veiledRowOneX, "apple")
        val bobs = bobs(fakeDs)
        val bobOne = bobs.first()

        bobOne.y // Pierce the veil
        fakeDs.rowOneX = piercedRowOneX

        assertEquals(piercedRowOneX, bobOne.x)
    }

    @Test
    fun `should not veil`() {
        val realRowOneY = "apple"
        val fakeDs = FakeDataSource(2, realRowOneY)
        val bobs = bobs(fakeDs)

        assertEquals(realRowOneY, bobs.first().y)
    }
}

fun bobs(fakeDs: DataSource) = veil<Bob, Int>(
    ds = fakeDs,
    initialData = fakeDs.fetch("SELECT *"),
    idProp = "id",
    "x"
) { ds, id ->
    RealBob(ds, id)
}
