package hm.binkley.veil

import java.util.Objects.hash

interface Bob {
    val a: Int
    val b: String?
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override val a: Int get() = ds.fetchProperty(id, "a")
    override val b: String? get() = ds.fetchProperty(id, "b")

    override fun equals(other: Any?) = this === other ||
            other is RealBob &&
            id == other.id

    override fun hashCode() = hash(this::class, id)
    override fun toString() = "RealBob($id){a=$a, b=$b}"
}

@Suppress("UNCHECKED_CAST")
private fun <T, ID> DataSource.fetchProperty(id: ID, prop: String): T =
    fetch("SELECT $prop WHERE ID = :id", id).first()[prop] as T
