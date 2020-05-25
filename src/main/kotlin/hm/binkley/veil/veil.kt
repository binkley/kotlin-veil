package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance

inline fun <reified T, ID> veil(
    ds: DataSource,
    initialData: Sequence<Map<String, Any?>>,
    idProp: String,
    vararg veiledProps: String,
    crossinline ctorOfReal: (DataSource, ID) -> T
) = initialData.map {
    @Suppress("UNCHECKED_CAST")
    newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java),
        veiler(ctorOfReal(ds, it[idProp] as ID)!!, it, *veiledProps)
    ) as T
}

fun veiler(
    real: Any,
    data: Map<String, Any?>,
    vararg veiledProps: String
): InvocationHandler = Veiler(real, data, *veiledProps)

private class Veiler(
    private val real: Any,
    private val data: Map<String, Any?>,
    private vararg val veiledProps: String
) : InvocationHandler {
    var pierced = false

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val prop = prop(method.name)
        if (!pierced && prop in veiledProps) {
            println("VEILING -> ${method.name}=${data[prop]}")
            return data[prop]
        }

        if (!pierced) {
            println("PIERCING VEIL")
            pierced = true
        }

        println("CALLING ${real::class.simpleName}.${method.name}")
        return method(real, *(args ?: arrayOf())) // Not nice syntax
    }
}

private fun prop(methodName: String) =
    if (methodName.startsWith("get"))
        methodName.removePrefix("get").decapitalize()
    else methodName
