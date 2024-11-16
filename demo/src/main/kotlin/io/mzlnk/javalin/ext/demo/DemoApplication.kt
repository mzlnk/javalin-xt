package io.mzlnk.javalin.ext.demo

import io.javalin.Javalin
import io.mzlnk.javalin.ext.enableEXT


fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableEXT()
        .start(8080)

    val a = 10
}
