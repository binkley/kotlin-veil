package hm.binkley.veil

interface DataSource {
    fun fetch(query: String, vararg args: Any?): Sequence<Map<String, Any?>>
}

/**
 * Fetches the value of one column from one row.  Assumptions:
 * - The query returns only one row
 * - The row is specific to an `ID` column
 * - [table] and [prop] are simple strings: no whitespace, no SQL injection
 *
 * These assumptions do not make sense for a general ORM: this is demo code.
 *
 * The query is constructed as:
 * ```
 * SELECT $prop FROM $table WHERE ID = :id
 * ```
 */
@Suppress("UNCHECKED_CAST")
internal fun <T, ID> DataSource.fetchProperty(
    table: String,
    id: ID,
    prop: String
): T =
    fetch("SELECT $prop FROM $table WHERE ID = :id", id).first()[prop] as T

class FakeDataSource(
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
