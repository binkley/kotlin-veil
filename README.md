<a href="./LICENSE.md">
<img src="./images/public-domain.svg" alt="Public Domain"
align="right" width="20%" height="auto"/>
</a>

# Kotlin Veil

[![build](https://github.com/binkley/kotlin-veil/workflows/build/badge.svg)](https://github.com/binkley/kotlin-veil/actions)
[![issues](https://img.shields.io/github/issues/binkley/kotlin-veil.svg)](https://github.com/binkley/kotlin-veil/issues)
[![pull requests](https://img.shields.io/github/issues-pr/binkley/kotlin-veil.svg)](https://github.com/binkley/kotlin-veil/pulls)
[![vulnerabilities](https://snyk.io/test/github/binkley/kotlin-veil/badge.svg)](https://snyk.io/test/github/binkley/kotlin-veil)
[![license](https://img.shields.io/badge/license-Public%20Domain-blue.svg)](http://unlicense.org)

A Kotlin demonstration of Yegor's "veiled objects"

See [_Veil Objects to Replace
DTOs_](https://www.yegor256.com/2020/05/19/veil-objects.html).

## Try it

```
$ ./run.sh
```

## Build locally

```
$ ./mvnw clean verify
```

### Build with CI (Docker)

```
$ ./batect build
```

## Notes

As this code is a demonstration, it comes with caveats:

- The code does not use an actual database, rather a "fake" data source
- Implementation uses JDK proxies
- Data rows representing objects each has a unique "id" key

## Features

- Demonstrates "piercing the veil" and "unpierceable veils"
- JDK reflection on veiledness by casting to a `Veiled<T>`, and using the 
  `pierced` property, or the `veiled(prop-ref)` function

One might say, "Use an ORM"!  The point of veiled objects is lighter-weight code
without the complexity of an ORM.  Contrawise, 

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
