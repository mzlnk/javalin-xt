Feature: javalin-xt - application properties

  Scenario: Access application properties
    Given project is set up

    And resource application.yml is created with content
    # language=yaml
    """
    property1a: 1
    property1b: 1.5
    property2: true
    property3: value1
    property4:
      - 2
      - 3
    property5:
      - value2
      - value3
    property6:
      - true
      - false
    property7:
      property8: value4
    property9:
      - property10: value5
    """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.properties
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.properties["property1a"].asInt == 1)
          assert(app.properties["property1a"].asLong == 1L)
          assert(app.properties["property1b"].asDouble == 1.5)
          assert(app.properties["property1b"].asFloat == 1.5F)

          assert(app.properties["property2"].asBoolean == true)
          assert(app.properties["property3"].asString == "value1")

          assert(app.properties["property4"].asIntList == listOf(2, 3))
          assert(app.properties["property5"].asStringList == listOf("value2", "value3"))
          assert(app.properties["property6"].asBooleanList  == listOf(true, false))

          assert(app.properties["property7.property8"].asString == "value4")
          assert(app.properties["property9[0].property10"].asString == "value5")
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: Use application properties with profile
    Given project is set up

    And resource application.yml is created with content
    # language=yaml
    """
    property1: value1-base
    property2: value2-base
    """

    And resource application-dev.yml is created with content
    # language=yaml
    """
    property2: value2-dev
    property3: value3-dev
    """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.properties
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt {
                  properties { profile = "dev" }
              }
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.properties["property1"].asString == "value1-base")
          assert(app.properties["property2"].asString == "value2-dev")
          assert(app.properties["property3"].asString == "value3-dev")
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: Use application properties with environment variables
    Given project is set up

    And environment variable ENV_VAR_1 is set to value1-env-var-1
    And environment variable ENV_VAR_2 is set to value2-env-var-2

    And resource application.yml is created with content
    # language=yaml
    """
    property1: ${ENV_VAR_1}
    property2: ${ENV_VAR_2}
    """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.properties
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt()
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.properties["property1"].asString == "value1-env-var-1")
          assert(app.properties["property2"].asString == "value2-env-var-2")
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed


  Scenario: Disabling application properties
    Given project is set up

    And resource application.yml is created with content
    # language=yaml
    """
    property1: value1
    """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.properties
      import io.mzlnk.javalin.xt.xt

      fun main(args: Array<String>) {
          val app = Javalin.create()
              .xt {
                  properties { enabled = false }
              }
              .start(0) // 0 indicates that the server should start on a random port

          assert(app.properties.getOrNull("property1") == null)
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed