package hm.binkley.veil

import java.util.Objects.hash

/**
 * Ideally this would be an abstract class with `equals` and `hashCode`
 * defined based on values to treat implementations as value objects.  Using
 * JDK proxies, an interface is needed rather than an abstract class.
 *
 * Note that `id` is not a property.  This respects that some implementations
 * may be database rows with an ID, but others could be memory-only test
 * objects.
 *
 * Some interesting use cases might arise when mixing objects read from a
 * database with objects from other sources.
 */
interface Bob {
    /** Meant to be veiled */
    val a: Int

    /** Meant to not be veiled */
    val b: String?

    val veiled: Int // TODO: Please break!
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override val a: Int get() = ds.fetchProperty("Bob", id, "a")
    override val b: String? get() = ds.fetchProperty("Bob", id, "b")
    override val veiled = 17

    override fun equals(other: Any?) = this === other ||
        other is RealBob &&
        id == other.id

    override fun hashCode() = hash(this::class, id)
    override fun toString() = "RealBob($id){a=$a, b=$b, veiled=$veiled}"
}
