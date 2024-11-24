Feature: javalin-xt - DI
  Scenario: DI with simple types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      class ComponentA
      class ComponentB(val componentA: ComponentA)

      @Module
      class AppModule {

          @Singleton
          fun componentA(): ComponentA = ComponentA()

          @Singleton
          fun componentB(componentA: ComponentA): ComponentB = ComponentB(componentA)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 2)

          val componentA = app.context.findInstance(ComponentA::class.java)
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(componentA != null) { "componentA - expected: not null, actual: null" }
          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.componentA === componentA) { "componentB.componentA - expected: ${componentA}, actual: ${componentB?.componentA}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with super types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      open class ComponentA
      class ComponentA1 : ComponentA()
      class ComponentB(val componentA: ComponentA)

      @Module
      class AppModule {

          @Singleton
          fun componentA1(): ComponentA1 = ComponentA1()

          @Singleton
          fun componentB(componentA: ComponentA): ComponentB = ComponentB(componentA)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 2)

          val componentA = app.context.findInstance(ComponentA::class.java)
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(componentA != null) { "componentA - expected: not null, actual: null" }
          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.componentA === componentA) { "componentB.componentA - expected: ${componentA}, actual: ${componentB?.componentA}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with interface types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      interface TypeA
      class ComponentA : TypeA
      class ComponentB(val typeA: TypeA)

      @Module
      class AppModule {

          @Singleton
          fun componentA(): ComponentA = ComponentA()

          @Singleton
          fun componentB(typeA: TypeA): ComponentB = ComponentB(typeA)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 2)

          val typeA = app.context.findInstance(TypeA::class.java)
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(typeA != null) { "typeA - expected: not null, actual: null" }
          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.typeA === typeA) { "componentB.typeA - expected: ${typeA}, actual: ${componentB?.typeA}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with generic types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      class ComponentA<T>
      class ComponentB<T>(val componentA: ComponentA<T>)

      @Module
      class AppModule {

          @Singleton
          fun componentA(): ComponentA<String> = ComponentA()

          @Singleton
          fun componentB(componentA: ComponentA<String>): ComponentB<String> = ComponentB(componentA)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.di.type.TypeReference
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 2) { "size - expected: 2, actual: ${app.context.size()}" }

          val componentA = app.context.findInstance(object : TypeReference<ComponentA<String>>() {})
          val componentB = app.context.findInstance(object : TypeReference<ComponentB<String>>() {})

          assert(componentA != null) { "componentA - expected: not null, actual: null" }
          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.componentA === componentA) { "componentB.componentA - expected: ${componentA}, actual: ${componentB?.componentA}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with list types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      class ComponentA
      class ComponentB(val components: List<ComponentA>)

      @Module
      class AppModule {

          @Singleton
          fun componentA1(): ComponentA = ComponentA()

          @Singleton
          fun componentA2(): ComponentA = ComponentA()

          @Singleton
          fun componentB(components: List<ComponentA>): ComponentB = ComponentB(components)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.di.type.TypeReference
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 3) { "size - expected: 3, actual: ${app.context.size()}" }

          val componentsAs = app.context.findInstance(object : TypeReference<List<ComponentA>>() {})
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(componentsAs != null) { "componentsAs - expected: not null, actual: null" }
          assert(componentsAs?.size == 2) { "componentsAs.size - expected: 2, actual: ${componentsAs?.size}" }

          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.components == componentsAs) { "componentB.components - expected: ${componentsAs}, actual: ${componentB?.components}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with list types using super types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      open class ComponentA
      class ComponentA1 : ComponentA()
      class ComponentA2 : ComponentA()
      class ComponentB(val components: List<ComponentA>)

      @Module
      class AppModule {

          @Singleton
          fun componentA1(): ComponentA1 = ComponentA1()

          @Singleton
          fun componentA2(): ComponentA2 = ComponentA2()

          @Singleton
          fun componentB(components: List<ComponentA>): ComponentB = ComponentB(components)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.di.type.TypeReference
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 3) { "size - expected: 3, actual: ${app.context.size()}" }

          val componentsAs = app.context.findInstance(object : TypeReference<List<ComponentA>>() {})
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(componentsAs != null) { "componentsAs - expected: not null, actual: null" }
          assert(componentsAs?.size == 2) { "componentsAs.size - expected: 2, actual: ${componentsAs?.size}" }

          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.components == componentsAs) { "componentB.components - expected: ${componentsAs}, actual: ${componentB?.components}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with list types using interface types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.di.Module
      import io.mzlnk.javalin.xt.di.Singleton

      interface TypeA
      class ComponentA : TypeA
      class ComponentB(val components: List<TypeA>)

      @Module
      class AppModule {

          @Singleton
          fun componentA1(): ComponentA = ComponentA()

          @Singleton
          fun componentA2(): ComponentA = ComponentA()

          @Singleton
          fun componentB(components: List<TypeA>): ComponentB = ComponentB(components)
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.di.type.TypeReference
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.context.size() == 3) { "size - expected: 3, actual: ${app.context.size()}" }

          val typesAs = app.context.findInstance(object : TypeReference<List<TypeA>>() {})
          val componentB = app.context.findInstance(ComponentB::class.java)

          assert(typesAs != null) { "typesAs - expected: not null, actual: null" }
          assert(typesAs?.size == 2) { "typesAs.size - expected: 2, actual: ${typesAs?.size}" }

          assert(componentB != null) { "componentB - expected: not null, actual: null" }
          assert(componentB?.components == typesAs) { "componentB.components - expected: ${typesAs}, actual: ${componentB?.components}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed
