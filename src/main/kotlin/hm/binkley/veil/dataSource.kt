package hm.binkley.veil

interface DataSource {
    fun fetch(query: String, vararg args: Any?): Sequence<Map<String, Any?>>
}

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
            "SELECT *" -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "a" to rowOneA,
                    "b" to rowOneY
                )
            )
            "SELECT a WHERE ID = :id" -> when (args[0]) {
                1 -> sequenceOf(mapOf("a" to rowOneA))
                else -> sequenceOf(mapOf())
            }
            "SELECT b WHERE ID = :id" -> when (args[0]) {
                1 -> sequenceOf(mapOf("b" to rowOneY))
                else -> sequenceOf(mapOf())
            }
            else -> error("Unknown: $query")
        }
    }
}
