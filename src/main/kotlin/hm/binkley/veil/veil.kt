package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance

inline fun <reified T> veil(
    crossinline real: (DataSource, Int) -> T,
    ds: DataSource,
    data: Sequence<Map<String, Any?>>,
    vararg keys: String
) = data.map {
    newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java),
        Veiler(real(ds, it["id"] as Int)!!, it, *keys)
    ) as T
}

class Veiler(
    private val real: Any,
    private val data: Map<String, Any?>,
    vararg _keys: String
) : InvocationHandler {
    private val keys = _keys
    private var pierced = false

    init {
        println("HANDLER -> ${keys.contentToString()}$data")
    }

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val key = prop(method.name)
        if (!pierced && key in keys) {
            println("VEILING -> ${method.name}=${data[key]}")
            return data[key]
        }

        pierced = true

        println("CALLING ON ${real::class.simpleName} -> ${method.name}")
        return if (args == null) method(real)
        else method(real, *args)
    }
}

private fun prop(methodName: String) =
    if (methodName.startsWith("get"))
        methodName.removePrefix("get").decapitalize()
    else methodName
