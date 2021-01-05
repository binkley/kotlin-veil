package hm.binkley.veil

class FakeBobDataSource(
    var rowOneA: Int,
    private val rowOneB: String?,
) : DataSource {
    override fun fetch(
        query: String,
        vararg args: Any?,
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

internal const val SELECT_ALL_BOBS = "SELECT * FROM Bob"
internal const val SELECT_BOB_A = "SELECT a FROM Bob WHERE ID = :id"
internal const val SELECT_BOB_B = "SELECT b FROM Bob WHERE ID = :id"
