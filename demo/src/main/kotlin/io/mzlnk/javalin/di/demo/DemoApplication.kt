package io.mzlnk.javalin.di.demo

import io.javalin.Javalin
import io.mzlnk.javalin.di.enableDI


fun main(args: Array<String>) {
    val app = Javalin.create()
        .enableDI()
        .start(8080)

    val a = 10
}
