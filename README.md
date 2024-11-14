# Javalin DI

**Javalin DI is a very lightweight dependency injection framework dedicated to [Javalin](https://javalin.io/).**

Key features includes:

- **Lightweight**: Javalin DI is designed to be as lightweight as possible. It does not introduce any additional
  dependencies to your project. Just the most basic and useful features that can enhance working with Javalin.
- **No reflection**: Javalin DI does not use reflection to inject dependencies. Everything is done at compile time.
- **Enforced framework separation**: Javalin DI by default enforces separation between business logic and framework
  code via the use of modules.
- **Invisible**: Javalin DI can be considered as an extension to Javalin. Using it, you can still use Javalin as you
  would normally do, but with the added benefit of dependency injection context acessible via Javalin `app`
- **Dedicated to Javalin**: Javalin DI is specifically designed to work with Javalin which allows for a more
  streamlined and efficient integration.

## Notes 📄

Currently, Javalin DI is still in development 🚧. Therefore:

- Kotlin support only (support for Java is planned in the future)
- limited features
- no UX-friendly configuration (dedicated Gradle/Maven plugin is planned in the future)

## Quickstart 🚀

### Installation 🔧

#### Gradle

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.mzlnk:javalin-di:0.1.0")
    ksp("io.mzlnk:javalin-di:0.0.1")
}
```

#### Maven

To be announced soon.

### Enabling Javalin DI 🔓

To enable Javalin DI, you just need to invoke `enableDI()` on your Javalin instance:

```kotlin
fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableDI()
        .start(8080)
}
```

### Defining modules and singletons 📦

To add a component to the DI context, you need to define a separate class with no-args constructor and annotate it with
`@Module`. Then inside the class you can define singleton definitions via methods annotated with `@Singleton`:

```kotlin
package com.example

import io.javalin.di.Module
import io.javalin.di.Singleton

class ComponentA
class ComponentB(val componentA: ComponentA)

@Module
class SampleModule {

    @Singleton
    fun componentA(): ComponentA = ComponentA()

    @Singleton
    fun componentB(componentA: ComponentA): ComponentB = ComponentB(componentA)

}
```

**Note:**

This is the only way of defining components in Javalin DI. You cannot define component e.g. by using `@Inject`/
`@Singleton` annotation
on the component class itself. This is to enforce separation between business logic and framework code on the framework
level itself to propagate
good design practices.

### Accessing Javalin DI context 🔑

Once you have defined your modules and singletons, you can access them via regular Javalin `app` instance using
dedicated extension functions:

```kotlin
fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableDI()
        .start(8080)

    val componentA = app.getInstance(ComponentA::class.java)
    val componentB = app.getInstance(ComponentB::class.java)
}
```

**Note:**

The `app` instance is still a regular Javalin instance, so you can use it as you would normally do (e.g. defining
routes, handlers etc.). The only difference is that now you can also access your components from DI context.

## Features 🎯

### Injecting plain components

Javalin DI provides support for injecting plain components (without generic types) by:

- given type
- super type
- interface implemented by the given component

It applied to both dependency injection and accessing components from the DI context using `app.getInstance()` method.

**Examples:**

Using dependency injection:

```kotlin
interface TypeA
open class ComponentA : TypeA
class ComponentA1 : ComponentA()

@Module
class SampleModule {

    @Singleton
    fun componentA1(): ComponentA1 = ComponentA1()

    // Injecting component A1 as defined by given type
    @Singleton
    fun componentB(componentA1: ComponentA1): ComponentB { 
        // ...
    }

    // Injecting component A1 as defined by super type
    @Singleton
    fun componentC(componentA1: ComponentA): ComponentC { 
        // ...
    }

    // Injecting component A1 as defined by implemented interface
    @Singleton
    fun componentD(typeA: TypeA): ComponentD { 
        // ...
    }

}
```

Accessing components from DI context:

```kotlin
val componentA1 = app.getInstance(ComponentA1::class.java)
val componentA = app.getInstance(ComponentA::class.java)
val typeA = app.getInstance(TypeA::class.java)
```

### Injecting components with generic types

Javalin DI also provides support for injecting components with generic types. To prevent type erasure, in some cases
you need to use dedicated `TypeReference` object to define the generic type (similarly to how it is done in Jackson
library).

It applies to both dependency injection and accessing components from the DI context using `app.getInstance()` method.

**Examples:**

Using dependency injection:

```kotlin
class ComponentA<T>

@Module
class SampleModule {

    @Singleton
    fun componentA(): ComponentA<String> = ComponentA()

    // Injecting component A
    @Singleton
    fun componentB(componentA: ComponentA<String>): ComponentB { 
        // ...
    }

}
```

Accessing components from DI context:

```kotlin
val componentA = app.getInstance(object : TypeReference<ComponentA<String>>() {})
```

### Injecting list of components of the same type

Javalin DI supports injection of list of all components of the same type. To do this, you need to define
dependency/access instance from DI context using `List` type (the only collection type supported at the moment).

**Examples:**

Using dependency injection:

```kotlin
class ComponentA

@Module
class SampleModule {

    @Singleton
    fun componentA1(): ComponentA = ComponentA()

    @Singleton
    fun componentA2(): ComponentA = ComponentA()

    // Injecting components A1 and A2:
    @Singleton
    fun componentB(components: List<ComponentA>): ComponentB { 
        // ...
    }
}
```

Accessing components from DI context:

```kotlin
val components = app.getInstance(object : TypeReference<List<ComponentA>>() {})
```

**Note:**

When injecting/accessing list of components:

- the list of components defined one by one are returned first
- if there are no such components defined, then the explictly defined list of components is returned
- if there are no matching components at all, an empty list is returned

```kotlin
class ComponentA
class ComponentB

@Module
class SampleModule {

    @Singleton
    fun componentA1(): ComponentA = ComponentA()

    @Singleton
    fun componentA2(): ComponentA = ComponentA()

    @Singleton
    fun componentsA3A4(): List<ComponentA> = listOf(ComponentA(), ComponentA())

    // Injecting components A1 and A2 as defined one by one
    @Singleton
    fun componentC(components: List<ComponentA>): ComponentC { 
        // ...
    }

    // Injecting an empty list as no components of type ComponentB are defined
    @Singleton
    fun componentD(components: List<ComponentB>): ComponentD { 
        // ...
    }

}
``` 

## Examples 📂

There is an example project available in the `demo` directory.

## License 🎫

Javalin DI is licensed under the Apache License, Version 2.0. See TBD for more information.

## Contributing 💪

Contributions are welcome! Please see [LICENSE](LICENSE) for more information.

## Authors ⚗️

Javalin DI is developed by:

- [Marcin Zielonka](https://github.com/mzlnk)

