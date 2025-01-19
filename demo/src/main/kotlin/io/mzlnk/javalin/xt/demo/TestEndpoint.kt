package io.mzlnk.javalin.xt.demo

import io.javalin.http.Context
import io.mzlnk.javalin.xt.routing.*

@Path("/root")
class TestEndpoint : Endpoint {

    @Get
    @Path("/path1/{id}")
    fun post(
        @QueryParameter("param-1") queryParam1: String?,
        @Header("header-2") header1: String?,
        @PathVariable("id") pathVar1: String,
        @Body body: String,
        ctx: Context
    ) {
        ctx.result("Hello World from javalin-xt declarative routing!")
    }

    @Get
    @Path("/path2")
    fun get(ctx: Context) {
        ctx.result("Hello World from javalin-xt declarative routing!")
    }

}