package hm.binkley.veil

import java.util.Objects.hash

/**
 * Ideally this would be an abstract class with `equals` and `hashCode`
 * defined based on values to treat implementations as value objects.  Using
 * JDK proxies, an interface is needed rather than an abstract class.
 *
 * Note that `id` is not a property.  This respects that some implementations
 * may be database rows with an ID, but others could be memory-only test
 * objects.
 *
 * Some interesting use cases might arise when mixing objects read from a
 * database with objects from other sources.
 */
interface Bob {
    /** Meant to be veiled */
    val a: Int

    /** Meant to not be veiled */
    val b: String?
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override val a: Int get() = ds.fetchProperty("Bob", id, "a")
    override val b: String? get() = ds.fetchProperty("Bob", id, "b")

    override fun equals(other: Any?) = this === other ||
            other is RealBob &&
            id == other.id

    override fun hashCode() = hash(this::class, id)
    override fun toString() = "RealBob($id){a=$a, b=$b}"
}

internal const val SELECT_ALL_BOBS = "SELECT * FROM Bob"
internal const val SELECT_BOB_A = "SELECT a FROM Bob WHERE ID = :id"
internal const val SELECT_BOB_B = "SELECT b FROM Bob WHERE ID = :id"

class FakeBobDataSource(
    var rowOneA: Int,
    private val rowOneB: String?
) : DataSource {
    override fun fetch(
        query: String,
        vararg args: Any?
    ): Sequence<Map<String, Any?>> {
        println("FETCHING${args.contentToString()} -> $query")
        return when (query) {
            SELECT_ALL_BOBS -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "a" to rowOneA,
                    "b" to rowOneB
                )
            )
            SELECT_BOB_A -> when (args[0]) {
                1 -> sequenceOf(mapOf("a" to rowOneA))
                else -> sequenceOf(mapOf())
            }
            SELECT_BOB_B -> when (args[0]) {
                1 -> sequenceOf(mapOf("b" to rowOneB))
                else -> sequenceOf(mapOf())
            }
            else -> error("Unknown: $query")
        }
    }
}
