Feature: javalin-xt - DI
  Scenario: DI with simple types
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
                  config.enableIoC()
              }

          app.start(0) // 0 indicates that the server should start on a random port

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

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton

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
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
                  config.enableIoC()
              }

          app.start(0) // 0 indicates that the server should start on a random port

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


  Scenario: Di with application properties support
    Given project is set up

    And resource application.yml is created with content
      # language=yaml
      """
      property1:
        property2: test-value
      """

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton
      import io.mzlnk.javalin.xt.context.Property

      class Component(val property: String)

      @Module
      class AppModule {

          @Singleton
          fun component(@Property("property1.property2") property: String): Component = Component(property)

      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC
      import io.mzlnk.javalin.xt.enableApplicationProperties

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
                  config.enableIoC()
                  config.enableApplicationProperties()
              }

          app.start(0) // 0 indicates that the server should start on a random port

          val component = app.context.getInstance(Component::class.java)
          assert(component.property == "test-value") { "component.property - expected: test-value, actual: ${component.property}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with conditional singletons
    Given project is set up

    And resource application.yml is created with content
      # language=yaml
      """
      property1:
        property2: A
      """

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton
      import io.mzlnk.javalin.xt.context.Property
      import io.mzlnk.javalin.xt.context.Conditional

      class Component(val property: String)

      @Module
      class AppModule {

          @Singleton
          @Conditional.OnProperty(property = "property1.property2", havingValue = "A")
          fun componentA(): Component = Component("A")

          @Singleton
          @Conditional.OnProperty(property = "property1.property2", havingValue = "B")
          fun componentB(): Component = Component("B")

      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableApplicationProperties
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
              config.enableApplicationProperties()
              config.enableIoC()
          }

          app.start(0) // 0 indicates that the server should start on a random port

          val component = app.context.getInstance(Component::class.java)
          assert(component.property == "A") { "component.property - expected: A, actual: ${component.property}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: DI with named singletons
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.context.Module
      import io.mzlnk.javalin.xt.context.Singleton
      import io.mzlnk.javalin.xt.context.Named

      class ComponentA(val property: String)
      class ComponentB(val component: ComponentA)

      @Module
      class AppModule {

          @Singleton
          @Named("componentA1")
          fun componentA1(): ComponentA = ComponentA("A1")

          @Singleton
          @Named("componentA2")
          fun componentA2(): ComponentA = ComponentA("A2")

          @Singleton
          fun componentB(@Named("componentA1") componentA: ComponentA): ComponentB = ComponentB(componentA)

      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.context
      import io.mzlnk.javalin.xt.context.TypeReference
      import io.mzlnk.javalin.xt.enableIoC

      fun main(args: Array<String>) {
          val app = Javalin.create { config ->
                  config.enableIoC()
              }

          app.start(0) // 0 indicates that the server should start on a random port

          val componentA = app.context.getInstance(ComponentA::class.java, name = "componentA1")
          assert(componentA.property == "A1") { "component.property - expected: A1, actual: ${componentA.property}" }

          val componentB = app.context.getInstance(ComponentB::class.java)
          assert(componentB.component.property == "A1") { "componentB.component.property - expected: A1, actual: ${componentB.component.property}" }
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed
