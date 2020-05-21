package hm.binkley.veil

interface DataSource {
    fun fetch(q: String, vararg a: Any?): Sequence<Map<String, Any?>>
}

class FakeDataSource : DataSource {
    override fun fetch(
        q: String,
        vararg a: Any?
    ): Sequence<Map<String, Any?>> {
        println("FETCHING${a.contentToString()} -> $q")
        return when (q) {
            "SELECT *" -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "x" to 2,
                    "y" to "apple"
                ),
                mapOf(
                    "id" to 2,
                    "x" to 3,
                    "y" to "banana"
                )
            )
            "SELECT x WHERE ID = :id" -> when (a[0]) {
                1 -> sequenceOf(mapOf("x" to 2))
                2 -> sequenceOf(mapOf("x" to 3))
                else -> sequenceOf(mapOf())
            }
            "SELECT y WHERE ID = :id" -> when (a[0]) {
                1 -> sequenceOf(mapOf("y" to "apple"))
                2 -> sequenceOf(mapOf("y" to "banana"))
                else -> sequenceOf(mapOf())
            }
            else -> error("Unknown: $q")
        }
    }
}
