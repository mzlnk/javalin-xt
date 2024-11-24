Feature: javalin-xt

  Scenario: Running Javalin with javalin-xt enabled
    Given project is set up
    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
    # language=kotlin
    """
    package io.mzlnk.javalin.xt.e2e.app

    import io.javalin.Javalin
    import io.mzlnk.javalin.xt.xt

    fun main(args: Array<String>) {
        Javalin.create()
            .xt()
            .start(0) // 0 indicates that the server should start on a random port
    }
    """
    When run the application
    Then application starts successfully