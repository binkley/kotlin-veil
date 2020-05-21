package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance
import java.util.Objects

fun main() {
    val ds = DataSource()
    val bobs = veil<Bob>(::RealBob, ds, ds.fetch("SELECT *"), "x")

    bobs.forEach { println(it) }
}

class DataSource {
    fun fetch(q: String, vararg a: Any?): Sequence<Map<String, Any>> {
        println("FETCHING${a.contentToString()} -> $q")
        return when (q) {
            "SELECT *" -> sequenceOf(
                mapOf(
                    "id" to 1,
                    "x" to 2
                ),
                mapOf(
                    "id" to 2,
                    "x" to 3
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

class InvokeHandler(
    private val real: Any,
    private val data: Map<String, Any?>,
    vararg _keys: String
) : InvocationHandler {
    private val keys = _keys

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val key = method.name
        return when {
            key in keys -> {
                println("VEILING -> $key")
                data[key]
            }
            null == args -> method.invoke(real)
            else -> method.invoke(real, *args)
        }
    }
}

inline fun <reified T> veil(
    crossinline real: (DataSource, Int) -> T,
    ds: DataSource,
    data: Sequence<Map<String, Any?>>,
    vararg keys: String
): Sequence<T> {
    return data.map {
        newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            InvokeHandler(real.invoke(ds, it["id"] as Int)!!, it, *keys)
        ) as T
    }
}

interface Bob {
    fun x(): Int
    fun y(): String?
}

class RealBob(private val ds: DataSource, val id: Int) : Bob {
    override fun x(): Int =
        ds.fetch("SELECT x WHERE ID = :id", id).first()["x"] as Int

    override fun y(): String? =
        ds.fetch("SELECT y WHERE ID = :id", id).first()["y"] as String?

    override fun equals(other: Any?) = this === other ||
            other is RealBob &&
            id == other.id

    override fun hashCode() = Objects.hash(this::class, id)

    override fun toString() = "RealBob{x=${x()}, y=${y()}}"
}
