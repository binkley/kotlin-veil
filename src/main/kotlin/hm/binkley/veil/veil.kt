package hm.binkley.veil

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance
import kotlin.reflect.KProperty1

interface Veilable<T> {
    val pierced: Boolean
    fun veiled(prop: KProperty1<T, *>): Boolean
}

inline fun <reified T, ID> veil(
    pierceable: Boolean,
    ds: DataSource,
    initialData: Sequence<Map<String, Any?>>,
    idProp: String,
    vararg veiledProps: String,
    crossinline ctorOfReal: (DataSource, ID) -> T
) = initialData.map {
    @Suppress("UNCHECKED_CAST")
    newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java, Veilable::class.java),
        Veiler(
            pierceable,
            ctorOfReal(ds, it[idProp] as ID)!!,
            it,
            *veiledProps
        )
    ) as T
}

/**
 * NB &mdash; this class cannot be non-public: it is called from an inline
 * function, so has the access of caller, not the declaration site.  The
 * calling function needs to be inline to support reified generics, else the
 * caller would need to pass in a type token.  In general, JDK proxies have
 * several restrictions like this.
 */
class Veiler(
    private val pierceable: Boolean = false,
    private val real: Any,
    private val data: Map<String, Any?>,
    private vararg val veiledProps: String
) : InvocationHandler {
    private var pierced = false

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?
    ): Any? {
        val prop = prop(method.name)

        if (Veilable::class.java == method.declaringClass) return when (prop) {
            "pierced" -> pierced
            "veiled" -> !pierced &&
                    (args?.get(0) as KProperty1<*, *>).name in veiledProps
            else -> error(
                "Invocation handler out of sync with Veilable: $method"
            )
        }

        if (!pierced && prop in veiledProps) {
            println("VEILING -> ${method.name}=${data[prop]}")
            return data[prop]
        }

        if (pierceable && !pierced) {
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
