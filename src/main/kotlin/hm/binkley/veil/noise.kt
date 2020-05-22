package hm.binkley.veil

var NOISY = false

fun println(msg: String) {
    if (NOISY) kotlin.io.println(msg)
}
