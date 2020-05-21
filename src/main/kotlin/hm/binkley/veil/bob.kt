package hm.binkley.veil

import java.util.Objects.hash

interface Bob {
    val x: Int
    val y: String?
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override val x: Int
        get() =
            ds.fetch("SELECT x WHERE ID = :id", id).first()["x"] as Int

    override val y: String?
        get() =
            ds.fetch("SELECT y WHERE ID = :id", id).first()["y"] as String?

    override fun equals(other: Any?) = this === other ||
            other is RealBob &&
            id == other.id

    override fun hashCode() = hash(this::class, id)

    override fun toString() = "RealBob($id){x=$x, y=$y}"
}
