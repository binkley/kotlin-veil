<a href="LICENSE.md">
<img src="https://unlicense.org/pd-icon.png" alt="Public Domain" align="right"/>
</a>

# Kotlin Veil

[![build](https://github.com/binkley/kotlin-veil/workflows/build/badge.svg)](https://github.com/binkley/kotlin-veil/actions)
[![issues](https://img.shields.io/github/issues/binkley/kotlin-veil.svg)](https://github.com/binkley/kotlin-veil/issues)
[![Public Domain](https://img.shields.io/badge/license-Public%20Domain-blue.svg)](http://unlicense.org)
[![made with kotlin](https://img.shields.io/badge/made%20with-Kotlin-1f425f.svg)](https://kotlinlang.org)

A Kotlin demonstration of Yegor's "veiled objects"

See [_Veil Objects to Replace DTOs_](https://www.yegor256.com/2020/05/19/veil-objects.html).

## Notes

As this code is a demonstration, it comes with caveats:

- The code does not use an actual database, rather a "fake" data source
- Implementation uses JDK proxies
- Data rows representing objects each has a unique "id" key

## Features

- Demonstrating "piercing the veil" and "unpierceable veils"
- Primitive means for reflecting on veiledness by casting to a `Veiled<T>`,
and using the `pierced` property, or `veiled(prop-ref)` function

One might say, "Use an ORM"!  The point of veiled objects is lighter-weight
code without the complexity of an ORM.

## Demonstration

Output of `main` demonstrating veiled, unveiled, and pierced:
```
NOTE: Bob has props: a, b, veiled.
NOTE: Prop veiled is to show that the data value is not masked by Veilable.

PIERCED
-------

== Read veiled, then pierced if pierceable (true), then underlying real object
VEILING -> getA=2
PIERCING VEIL
CALLING public abstract java.lang.String hm.binkley.veil.Bob.getB()
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
CALLING public abstract int hm.binkley.veil.Bob.getVeiled()
VEILED: Bob{a=2, b=apple, veiled=17}

CALLING public abstract int hm.binkley.veil.Bob.getA()
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
CALLING public abstract java.lang.String hm.binkley.veil.Bob.getB()
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
CALLING public abstract int hm.binkley.veil.Bob.getVeiled()
MAYBE-PIERCED: Bob{a=222, b=apple, veiled=17}

CALLING public java.lang.String java.lang.Object.toString()
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
REAL: RealBob(1){a=222, b=apple, veiled=17}

PIERCED? true
VEILED-A? false
VEILED-B? false
IS REFLECTIVE VEILED MASKED? false

UNPIERCED
---------

== Read veiled, then pierced if pierceable (false), then underlying real object
VEILING -> getA=2
CALLING public abstract java.lang.String hm.binkley.veil.Bob.getB()
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
CALLING public abstract int hm.binkley.veil.Bob.getVeiled()
VEILED: Bob{a=2, b=apple, veiled=17}

VEILING -> getA=2
CALLING public abstract java.lang.String hm.binkley.veil.Bob.getB()
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
CALLING public abstract int hm.binkley.veil.Bob.getVeiled()
MAYBE-PIERCED: Bob{a=2, b=apple, veiled=17}

CALLING public java.lang.String java.lang.Object.toString()
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
REAL: RealBob(1){a=222, b=apple, veiled=17}

PIERCED? false
VEILED-A? true
VEILED-B? false
IS REFLECTIVE VEILED MASKED? false
```
