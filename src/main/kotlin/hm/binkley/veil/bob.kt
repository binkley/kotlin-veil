package hm.binkley.veil

import java.util.Objects.hash

interface Bob {
    val x: Int
    val y: String?
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override val x: Int get() = ds.fetchProperty(id, "x")
    override val y: String? get() = ds.fetchProperty(id, "y")

    override fun equals(other: Any?) = this === other ||
            other is RealBob &&
            id == other.id

    override fun hashCode() = hash(this::class, id)
    override fun toString() = "RealBob($id){x=$x, y=$y}"
}

@Suppress("UNCHECKED_CAST")
private fun <T> DataSource.fetchProperty(id: Int, key: String): T =
    fetch("SELECT $key WHERE ID = :id", id).first()[key] as T
