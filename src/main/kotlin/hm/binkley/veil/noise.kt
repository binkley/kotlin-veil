package hm.binkley.veil

/** Yes, a global mutable.  This is a proof-of-concept project (spike). */
var NOISY = false

fun println(msg: String) {
    if (NOISY) kotlin.io.println(msg)
}
