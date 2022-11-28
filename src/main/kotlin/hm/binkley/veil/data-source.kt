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
