package io.mzlnk.javalin.xt.demo

import io.javalin.Javalin
import io.mzlnk.javalin.xt.context
import io.mzlnk.javalin.xt.xt

fun main(args: Array<String>) {
    val app = Javalin.create()
        .xt()
        .start(8080)

    val a = 10

    println("Size: ${app.context.size()}")

    val componentA = app.context.findInstance(ComponentA::class.java)
    println(componentA)
}
