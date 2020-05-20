package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance
import java.util.Arrays

class InvokeHandler(private val real: Any) : InvocationHandler {
    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        for (fn in real.javaClass.methods) {
            if (fn.name == method.name && Arrays.equals(
                    fn.parameterTypes,
                    method.parameterTypes
                )
            ) {
                if (args == null)
                    return fn.invoke(real)
                else
                    return fn.invoke(real, *args)
            }
        }
        throw UnsupportedOperationException("$method with $args")
    }
}

inline fun <reified T> veil(real: T): T {
    return newProxyInstance(
        real!!.javaClass.classLoader,
        arrayOf(T::class.java),
        InvokeHandler(real)
    ) as T
}

fun main() {
    val real = Bob(3)
    val bob: Bob = veil(real)

    println(bob)
}

class Bob(val x: Int) {
    fun foo() = x * 2
}
