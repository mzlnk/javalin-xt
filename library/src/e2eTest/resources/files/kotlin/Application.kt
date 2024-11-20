package io.mzlnk.javalin.xt.e2e.app

import io.javalin.Javalin
import io.mzlnk.javalin.xt.enableXt

fun main(args: Array<String>) {
    Javalin.create()
        .enableXt()
        .start(0) // 0 indicates that the server should start on a random port
}