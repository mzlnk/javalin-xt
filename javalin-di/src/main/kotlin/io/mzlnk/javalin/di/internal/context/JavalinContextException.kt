package io.mzlnk.javalin.di.internal.context

abstract class JavalinContextException : RuntimeException() {

    /*
     * Prevent the stack trace from being filled in.
     */
    override fun fillInStackTrace(): Throwable = this

}