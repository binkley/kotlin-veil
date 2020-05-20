package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance

class InvokeHandler(private val real: Any) : InvocationHandler {
    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        return if (null == args) method.invoke(real)
        else method.invoke(real, *args)
    }
}

inline fun <reified T, D> veil(
    crossinline real: (D) -> T,
    data: Sequence<D>
): Sequence<T> {
    return data.map {
        newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            InvokeHandler(real.invoke(it)!!)
        ) as T
    }
}

fun main() {
    val data = sequenceOf(
        object : DataForBob {
            override val x = 2
        },
        object : DataForBob {
            override val x = 3
        },
    )
    val bobs = veil<Bob, DataForBob>(::RealBob, data)

    bobs.forEach { println(it) }
}

interface DataForBob {
    val x: Int
}

interface Bob {
    fun foo(): Int
}

class RealBob(private val data: DataForBob) : Bob {
    override fun foo() = data.x * 2

    override fun toString() = "RealBob{x=${data.x}}"
}
