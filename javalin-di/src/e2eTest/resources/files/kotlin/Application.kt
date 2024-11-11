package io.mzlnk.javalin.di.e2e.app

import io.javalin.Javalin
import io.mzlnk.javalin.di.enableDI

fun main(args: Array<String>) {
    Javalin.create()
        .enableDI()
        .start(12000) // 0 indicates that the server should start on a random port
}