package hm.binkley.veil

/** Yes, a global mutable.  This is a proof-of-concept project (spike). */
var beNoisy = false

fun println(msg: String) {
    if (beNoisy) kotlin.io.println(msg)
}
