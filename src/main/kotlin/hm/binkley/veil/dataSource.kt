package hm.binkley.veil

interface DataSource {
    fun fetch(q: String, vararg a: Any?): Sequence<Map<String, Any?>>
}

class FakeDataSource(
    var rowOneX: Int,
    private val rowOneY: String?
) : DataSource {
    override fun fetch(
        q: String,
        vararg a: Any?
    ): Sequence<Map<String, Any?>> {
        println("FETCHING${a.contentToString()} -> $q")
        return when (q) {
            "SELECT *" -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "x" to rowOneX,
                    "y" to rowOneY
                )
            )
            "SELECT x WHERE ID = :id" -> when (a[0]) {
                1 -> sequenceOf(mapOf("x" to rowOneX))
                else -> sequenceOf(mapOf())
            }
            "SELECT y WHERE ID = :id" -> when (a[0]) {
                1 -> sequenceOf(mapOf("y" to rowOneY))
                else -> sequenceOf(mapOf())
            }
            else -> error("Unknown: $q")
        }
    }
}
