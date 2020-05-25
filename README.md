<a href="LICENSE.md">
<img src="https://unlicense.org/pd-icon.png" alt="Public Domain" align="right"/>
</a>

# Kotlin Veil

[![build](https://github.com/binkley/kotlin-veil/workflows/build/badge.svg)](https://github.com/binkley/kotlin-veil/actions)
[![issues](https://img.shields.io/github/issues/binkley/kotlin-veil.svg)](https://github.com/binkley/kotlin-veil/issues/)
[![Public Domain](https://img.shields.io/badge/license-Public%20Domain-blue.svg)](http://unlicense.org/)
[![made with kotlin](https://img.shields.io/badge/made%20with-Kotlin-1f425f.svg)](https://kotlinlang.org/)

A Kotlin demonstration of Yegor's "veiled objects"

See [_Veil Objects to Replace DTOs_](https://www.yegor256.com/2020/05/19/veil-objects.html).

## Notes

As this code is a demonstration, it comes with caveats:

- The code does not use an actual database, rather a "fake" data source
- Implementation uses JDK proxies
- Data rows representing objects each has a unique "id" key
- The code demonstrates "piercing the veil" and "unpierceable veils"

One might say, "Use an ORM"!  The point of veiled objects is lighter-weight
code without the complexity of an ORM.

## Demonstration

Initial data in the fake database:
```
FETCHING[] -> SELECT * FROM Bob
```
Reading a pierceable object from the database:
```
PIERCED
-------

== Read veiled, then pierced if true, then underlying real object
VEILING -> getA=2
PIERCING VEIL
CALLING RealBob.getB
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
VEILED: Bob{a=2, b=apple}

CALLING RealBob.getA
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
CALLING RealBob.getB
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
MAYBE-PIERCED: Bob{a=222, b=apple}

CALLING RealBob.toString
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
REAL: RealBob(1){a=222, b=apple}
```
Reading an unpierceable object from the database:
```
UNPIERCED
---------

== Read veiled, then pierced if false, then underlying real object
VEILING -> getA=2
CALLING RealBob.getB
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
VEILED: Bob{a=2, b=apple}

VEILING -> getA=2
CALLING RealBob.getB
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
MAYBE-PIERCED: Bob{a=2, b=apple}

CALLING RealBob.toString
FETCHING[1] -> SELECT a FROM Bob WHERE ID = :id
FETCHING[1] -> SELECT b FROM Bob WHERE ID = :id
REAL: RealBob(1){a=222, b=apple}
```
