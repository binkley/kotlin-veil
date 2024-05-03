@file:Suppress("RedundantInnerClassModifier") // IntelliJ

package hm.binkley.veil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val VEILED_ROW_ONE_A = 2
private const val PIERCED_ROW_ONE_A = 222
private const val REAL_ROW_ONE_B = "apple"

private val fakeDs = FakeBobDataSource(-1, REAL_ROW_ONE_B)

internal class VeilTest {
    @Nested
    inner class Veiling {
        @Test
        fun `should veil first time, and stay veiled`() {
            fakeDs.rowOneA = VEILED_ROW_ONE_A

            val bobs = bobs().pierceable
            // Sequences are not restartable -- TODO: Use List?
            val bobOne = bobs.first()

            assertEquals(VEILED_ROW_ONE_A, bobOne.a)

            fakeDs.rowOneA = PIERCED_ROW_ONE_A // No effect

            assertEquals(VEILED_ROW_ONE_A, bobOne.a)
        }

        @Test
        fun `should pierce second time when pierceable`() {
            fakeDs.rowOneA = VEILED_ROW_ONE_A

            val bobs = bobs().pierceable
            // Sequences are not restartable -- TODO: Use List?
            val bobOne = bobs.first()

            fakeDs.rowOneA = PIERCED_ROW_ONE_A
            bobOne.b // Pierce the veil with an unveiled property

            assertEquals(PIERCED_ROW_ONE_A, bobOne.a)
        }

        @Test
        fun `should not veil when not veilable`() {
            val bobs = bobs().pierceable
            // Sequences are not restartable -- TODO: Use List?
            val bobOne = bobs.first()

            assertEquals(REAL_ROW_ONE_B, bobOne.b)
        }

        @Test
        fun `should not pierce second time when unpierceable`() {
            fakeDs.rowOneA = VEILED_ROW_ONE_A

            val bobs = bobs().unpierceable
            // Sequences are not restartable -- TODO: Use List?
            val bobOne = bobs.first()

            bobOne.b // Pierce the veil
            fakeDs.rowOneA = PIERCED_ROW_ONE_A

            assertEquals(VEILED_ROW_ONE_A, bobOne.a)
        }
    }

    @Nested
    inner class Reflecting {
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

        /**
         * Key point: `veiled` is _both_ a prop on the data object, and a
         * reflective property (using JDK proxy logic).  The object prop
         * should not be hidden by the reflective prop.
         */
        @Test
        fun `should not hide props because of reflection`() {
            val bobs = bobs().unpierceable
            // Sequences are not restartable -- TODO: Use List?
            val bobOne = bobs.first()

            assertEquals(17, bobOne.veiled)
        }
    }
}

private fun bobs() = object {
    val pierceable: Sequence<Bob> get() = bobs(true)
    val unpierceable: Sequence<Bob> get() = bobs(false)

    private fun bobs(pierceable: Boolean) =
        veil<Bob, Int>(
            pierceable = pierceable,
            ds = fakeDs,
            initialData = fakeDs.fetch(SELECT_ALL_BOBS),
            idProp = "id",
            "a"
        ) { ds, id ->
            BobFromDataSource(ds, id)
        }
}
