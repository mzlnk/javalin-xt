Feature: Javalin Xt - Dependency Injection
  Scenario: DI with simple types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 2 components registered in DI context


  Scenario: DI with super types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 2 components registered in DI context


  Scenario: DI with interface types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 2 components registered in DI context


  Scenario: DI with generic types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 2 components registered in DI context


  Scenario: DI with list types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 3 components registered in DI context


  Scenario: DI with list types using super types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 3 components registered in DI context


  Scenario: DI with list types using interface types
    Given project is set up

    And application with Xt enabled is created

    And file src/main/kotlin/io/mzlnk/javalin/xt/e2e/app/AppModule.kt is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.mzlnk.javalin.xt.Module
      import io.mzlnk.javalin.xt.Singleton

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

    When run the application

    Then application starts successfully
    Then there are 3 components registered in DI context
