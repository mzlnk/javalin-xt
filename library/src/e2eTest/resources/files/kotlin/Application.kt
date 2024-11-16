package io.mzlnk.javalin.ext.e2e.app

import io.javalin.Javalin
import io.mzlnk.javalin.ext.enableEXT

fun main(args: Array<String>) {
    Javalin.create()
        .enableEXT()
        .start(12000) // 0 indicates that the server should start on a random port
}