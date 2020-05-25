package hm.binkley.veil

import java.util.Objects.hash

/**
 * Ideally this would be an abstract class with `equals` and `hashCode`
 * defined based on values to treat implementations as value objects.  Using
 * Java proxies, an interface is required rather than an abstract class.
 *
 * Note that `id` is not a property.  This respects that some implementations
 * may be database rows with an ID, but others could be memory-only test
 * objects.
 *
 * Some interesting use cases might arise when mixing objects read from a
 * database with objects from other sources.
 */
interface Bob {
    val a: Int
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

class FakeBobDataSource(
    var rowOneA: Int,
    private val rowOneY: String?
) : DataSource {
    override fun fetch(
        query: String,
        vararg args: Any?
    ): Sequence<Map<String, Any?>> {
        println("FETCHING${args.contentToString()} -> $query")
        return when (query) {
            "SELECT * FROM Bob" -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "a" to rowOneA,
                    "b" to rowOneY
                )
            )
            "SELECT a FROM Bob WHERE ID = :id" -> when (args[0]) {
                1 -> sequenceOf(mapOf("a" to rowOneA))
                else -> sequenceOf(mapOf())
            }
            "SELECT b FROM Bob WHERE ID = :id" -> when (args[0]) {
                1 -> sequenceOf(mapOf("b" to rowOneY))
                else -> sequenceOf(mapOf())
            }
            else -> error("Unknown: $query")
        }
    }
}
