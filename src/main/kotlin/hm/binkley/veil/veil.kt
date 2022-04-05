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
    crossinline ctorOfReal: (DataSource, ID) -> T,
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
 * *NB* &mdash; Access scope is not optimal:
 * - JDK proxies require class tokens
 * - [veil] is `inline` so `reified` works, and caller does not need to pass
 * in a class token for the JDK proxy: the code does it for you
 * - [veil] requires `crossinline` for the lambda constructor
 * - [Veiler] is `public` because `veil(...)` is inlined (a Kotlin requirement)
 */
class Veiler(
    private val pierceable: Boolean = false,
    private val real: Any,
    private val data: Map<String, Any?>,
    private vararg val veiledProps: String,
) : InvocationHandler {
    private var pierced = false

    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any?>?,
    ): Any? {
        val prop = prop(method.name)

        if (method.`belongs to Veilable`) return when (prop) {
            "pierced" -> pierced
            "veiled" -> !pierced && argsVeiled(args)
            else -> error(
                "Invocation handler out of sync with Veilable: $method"
            )
        }

        if (prop.veiled) {
            println("VEILING -> ${method.name}=${data[prop]}")
            return data[prop]
        }

        if (piercing) {
            println("PIERCING VEIL")
            pierced = true
        }

        println("CALLING $method")
        return method(real, *(args ?: arrayOf())) // TODO: Not nice syntax
    }

    @Suppress("PrivatePropertyName")
    private val Method.`belongs to Veilable`
        get() = Veilable::class.java == declaringClass

    private fun argsVeiled(args: Array<out Any?>?) =
        (args?.get(0) as KProperty1<*, *>).name in veiledProps

    /**
     * @todo How to cleanly combine check for args veiled and the prop itself?
     */
    private val String.veiled get() = !pierced && this in veiledProps
    private val piercing get() = pierceable && !pierced
}

private fun prop(methodName: String) =
    if (methodName.startsWith("get")) methodName
        .removePrefix("get")
        .replaceFirstChar { it.lowercase() }
    else methodName
