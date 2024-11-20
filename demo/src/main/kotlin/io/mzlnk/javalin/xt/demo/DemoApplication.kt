package io.mzlnk.javalin.xt.demo

import io.javalin.Javalin
import io.mzlnk.javalin.xt.enableXt

fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableXt()
        .start(8080)

    val a = 10
}
