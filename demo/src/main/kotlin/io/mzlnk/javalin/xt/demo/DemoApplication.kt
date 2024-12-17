package io.mzlnk.javalin.xt.demo

import io.javalin.Javalin
import io.mzlnk.javalin.xt.context
import io.mzlnk.javalin.xt.properties
import io.mzlnk.javalin.xt.xt

fun main(args: Array<String>) {
    val app = Javalin.create()
        .xt {
            properties {
                profile = "dev"
            }
        }
        .start(8080)

    val a = 10

    println("Size: ${app.context.size()}")

    val componentA = app.context.findInstance(ComponentA::class.java)
    println(componentA)

    val property1: Int = app.properties["property1"].asInt
    val property9: List<String> = app.properties["property7.property9"].asStringList

    val componentE = app.context.getInstance(ComponentE::class.java)
    println(componentE)
}
