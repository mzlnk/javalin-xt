# javalin-xt

[![Licence: Apache 2.0](https://img.shields.io/badge/Licence-Apache%202.0-blue.svg)](https://shields.io/)
[![Open Source](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
[![Javalin](https://img.shields.io/badge/Javalin-6.3.0-008abb.svg)](https://javalin.io/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-17-f5222f.svg)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-27c2b7.svg)](https://gradle.org/)

[![Version](https://img.shields.io/badge/version-0.4.0-397ee6)](https://central.sonatype.com/artifact/io.mzlnk/javalin-xt/overview)
[![Build](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fjavalin-xt-bot%2F896b6842fd99039b5a14141bd2e80f41%2Fraw%2Fbuild.json)](#)
[![Code coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Fjavalin-xt-bot%2F896b6842fd99039b5a14141bd2e80f41%2Fraw%2Fcode-coverage.json
)](#)

## About 🌐

**javalin-xt** is a very lightweight set of extension features dedicated to [Javalin](https://javalin.io/).

### Key concepts

Key concepts of **javalin-xt** include:

#### ▶ Lightweight

Designed to be as lightweight as possible. It does not introduce any additional dependencies to your project. Just the
most basic and useful features that can enhance working with Javalin.

#### ▶ No reflection

No use of reflection in its features. Everything is done at compile time.

#### ▶ Not over-engineered

No heavy use of proxies, annotations, or other complex mechanisms under the hood. Everything is kept simple in order to
make
any potential debugging or troubleshooting easier.

#### ▶ Invisible

Can be considered as an extension to Javalin. Using it, you can still use Javalin as you would normally do, but with the
added benefit of javalin-xt features accessible via Javalin  `app`.

#### ▶ Dedicated to Javalin

Specifically designed to work with Javalin which allows for a more streamlined and efficient integration.

### Key features

| Feature                    | Description                                                                                                                     |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Dependency injection**   | Simple and lightweight dependency injection (DI) framework that uses no reflection.                                             |
| **Application properties** | Simple engine to read and access application properties in a Spring Boot-like way via application.yml stored in your resources. |
| **Declarative routing**    | Simple way to define routes and handlers in a declarative way using annotations.                                                |

## Notes 📄

Currently, javalin-xt is still in development 🚧. Therefore:

- Kotlin support only (support for Java is planned in the future)
- Some features may have limited functionality

## Quickstart 🚀

### Installation 🔧

#### Gradle

```kotlin
plugins {
    id("io.mzlnk:javalin-xt") version "0.4.0"
}
```

#### Maven

To be announced soon.

### Enabling javalin-xt 🔓

To enable javalin-xt, you just need to invoke dedicated method on `JavalinConfig` instance during Javalin creation. For
example:

```kotlin
fun main(args: Array<String>) {
    val app = Javalin.create { config ->
        // enables IoC container and dependency injection
        config.enableIoC()
        // enables application properties engine
        config.enableProperties { propertiesConfig ->
            propertiesConfig.profile = "dev"
        }
    }

    app.start(8080)
}
```

### Explore javalin-xt features 🎯

Now you can start exploring javalin-xt features. All the features are described in details in
the [Wiki](https://github.com/mzlnk/javalin-xt/wiki) section:

- [Dependency injection](https://github.com/mzlnk/javalin-xt/wiki/Dependency-injection)
- [Application properties](https://github.com/mzlnk/javalin-xt/wiki/Application-properties)
- [Declarative routing](https://github.com/mzlnk/javalin-xt/wiki/Declarative-routing)

## Examples 📂

There is an example project available in the `demo` directory.

## License 🎫

Javalin DI is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for more information.

## Contributing 💪

Contributions are welcome! Please see [CONTRIBUTING](CONTRIBUTING.md) for more information.

## Authors ⚗️

javalin-xt is developed by:

- [Marcin Zielonka](https://github.com/mzlnk)

