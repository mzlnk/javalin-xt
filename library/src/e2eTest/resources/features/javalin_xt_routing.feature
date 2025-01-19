Feature: javalin-xt - declarative routing

  Scenario Outline: Declaring HTTP endpoint with different HTTP methods
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppEndpoint is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.http.Context
      import io.mzlnk.javalin.xt.routing.Endpoint
      import <annotation_import>
      import io.mzlnk.javalin.xt.routing.Path

      data class Response(val message: String)

      class AppEndpoint : Endpoint {

          <annotation>
          @Path("/test")
          fun test(ctx: Context) {
              ctx.json(Response("E2E OK"))
          }
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.registerEndpoint

      fun main(args: Array<String>) {
          val app = Javalin.create()

          app.registerEndpoint(AppEndpoint())

          app.start(0) // 0 indicates that the server should start on a random port
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed

    And HTTP request to application is created
    And HTTP request method is <method>
    And HTTP request path is /test

    And HTTP request is sent

    Then HTTP response status code is 200
    And HTTP response body is
    # language=json
    """
    {"message":"E2E OK"}
    """

    Examples:
      | method | annotation | annotation_import                  |
      | GET    | @Get       | io.mzlnk.javalin.xt.routing.Get    |
      | POST   | @Post      | io.mzlnk.javalin.xt.routing.Post   |
      | PUT    | @Put       | io.mzlnk.javalin.xt.routing.Put    |
      | DELETE | @Delete    | io.mzlnk.javalin.xt.routing.Delete |
      | PATCH  | @Patch     | io.mzlnk.javalin.xt.routing.Patch  |


  Scenario: Declaring HTTP endpoint with query parameter
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppEndpoint is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.http.Context
      import io.mzlnk.javalin.xt.routing.Endpoint
      import io.mzlnk.javalin.xt.routing.Get
      import io.mzlnk.javalin.xt.routing.Path
      import io.mzlnk.javalin.xt.routing.QueryParameter

      data class Response(val message: String, val queryParam: String)

      class AppEndpoint : Endpoint {

          @Get
          @Path("/test")
          fun test(@QueryParameter("e2e-param") param: String, ctx: Context) {
              ctx.json(Response(message = "E2E OK", queryParam = param))
          }
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.registerEndpoint

      fun main(args: Array<String>) {
          val app = Javalin.create()

          app.registerEndpoint(AppEndpoint())

          app.start(0) // 0 indicates that the server should start on a random port
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed

    And HTTP request to application is created
    And HTTP request method is GET
    And HTTP request path is /test
    And HTTP request query parameter e2e-param is e2e-value

    And HTTP request is sent

    Then HTTP response status code is 200
    And HTTP response body is
    # language=json
    """
    {"message":"E2E OK","queryParam":"e2e-value"}
    """

  Scenario: Declaring HTTP endpoint with header
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppEndpoint is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.http.Context
      import io.mzlnk.javalin.xt.routing.Endpoint
      import io.mzlnk.javalin.xt.routing.Post
      import io.mzlnk.javalin.xt.routing.Path
      import io.mzlnk.javalin.xt.routing.Header

      data class Response(val message: String, val header: String)

      class AppEndpoint : Endpoint {

          @Post
          @Path("/test")
          fun test(@Header("e2e-header") header: String, ctx: Context) {
              ctx.json(Response(message = "E2E OK", header = header))
          }
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.registerEndpoint

      fun main(args: Array<String>) {
          val app = Javalin.create()

          app.registerEndpoint(AppEndpoint())

          app.start(0) // 0 indicates that the server should start on a random port
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed

    And HTTP request to application is created
    And HTTP request method is POST
    And HTTP request path is /test
    And HTTP request header e2e-header is e2e-value

    And HTTP request is sent

    Then HTTP response status code is 200
    And HTTP response body is
    # language=json
    """
    {"message":"E2E OK","header":"e2e-value"}
    """


  Scenario: Declaring HTTP endpoint with body
    Given project is set up

    And class io.mzlnk.javalin.xt.e2e.app.AppEndpoint is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.http.Context
      import io.mzlnk.javalin.xt.routing.Endpoint
      import io.mzlnk.javalin.xt.routing.Post
      import io.mzlnk.javalin.xt.routing.Path
      import io.mzlnk.javalin.xt.routing.Body

      data class Response(val message: String, val body: String)

      class AppEndpoint : Endpoint {

          @Post
          @Path("/test")
          fun test(@Body body: String, ctx: Context) {
              ctx.json(Response(message = "E2E OK", body = body))
          }
      }
      """

    And class io.mzlnk.javalin.xt.e2e.app.Application is created with content
      # language=kotlin
      """
      package io.mzlnk.javalin.xt.e2e.app

      import io.javalin.Javalin
      import io.mzlnk.javalin.xt.registerEndpoint

      fun main(args: Array<String>) {
          val app = Javalin.create()

          app.registerEndpoint(AppEndpoint())

          app.start(0) // 0 indicates that the server should start on a random port
      }
      """

    When run the application

    Then application starts successfully
    And no assertions failed

    And HTTP request to application is created
    And HTTP request method is POST
    And HTTP request path is /test
    And HTTP request body is
    # language=text
    """
    e2e-value
    """

    And HTTP request is sent

    Then HTTP response status code is 200
    And HTTP response body is
    # language=json
    """
    {"message":"E2E OK","body":"e2e-value"}
    """

