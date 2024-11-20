# javalin-xt

[![Licence: Apache 2.0](https://img.shields.io/badge/Licence-Apache%202.0-blue.svg)](https://shields.io/)
[![Open Source](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
[![Javalin](https://img.shields.io/badge/Javalin-6.3.0-008abb.svg)](https://javalin.io/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-17-f5222f.svg)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-27c2b7.svg)](https://gradle.org/)

**javalin-xt is a very lightweight extension framework dedicated to [Javalin](https://javalin.io/).**

Key concepts includes:

| Concept                           | Description                                                                                                                                                                                          |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Lightweight**                   | Designed to be as lightweight as possible. It does not introduce any additional dependencies to your project. Just the most basic and useful features that can enhance working with Javalin.         |
| **No reflection**                 | Does not use reflection in its features. Everything is done at compile time.                                                                                                                         |
| **Enforced framework separation** | By default enforces separation between business logic and framework code.                                                                                                                            |
| **Invisible**                     | Can be considered as an extension to Javalin. Using it, you can still use Javalin as you would normally do, but with the added benefit of dependency injection context accessible via Javalin  `app` |
| **Dedicated to Javalin**          | Specifically designed to work with Javalin which allows for a more streamlined and efficient integration.                                                                                            |

Key features includes:

| Feature                    | Description                                                                                                                                      |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| **Dependency injection**   | Simple and lightweight dependency injection (DI) framework that allows you to define modules and singletons and access them from the DI context. |
| **Application properties** | Simple engine to read and access application properties in a Spring Boot-like way via application.yml stored in your resources                   |
| **Declarative routing**    | Simple  way to define routes and handlers in a declarative way using annotations.                                                                |

## Notes 📄

Currently, javalin-xt is still in development 🚧. Therefore:

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
    implementation("io.mzlnk:javalin-xt:0.1.0")
    ksp("io.mzlnk:javalin-xt:0.0.1")
}
```

#### Maven

To be announced soon.

### Enabling Javalin Xt 🔓

To enable javalin-xt, you just need to invoke `enableXt()` on your Javalin instance:

```kotlin
fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableDI()
        .start(8080)
}
```

### Explore Javalin Xt features 🎯

Now you can start exploring javalin-xt features. All the features are described in details in
the [Wiki](https://github.com/mzlnk/javalin-xt/wiki) section:

- [Dependency injection](https://github.com/mzlnk/javalin-xt/wiki/Dependency-injection)
- Application properties (TBA)
- Declarative routing (TBA)

## Examples 📂

There is an example project available in the `demo` directory.

## License 🎫

Javalin DI is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for more information.

## Contributing 💪

Contributions are welcome! Please see [CONTRIBUTING](CONTRIBUTING.md) for more information.

## Authors ⚗️

javalin-xt is developed by:

- [Marcin Zielonka](https://github.com/mzlnk)

