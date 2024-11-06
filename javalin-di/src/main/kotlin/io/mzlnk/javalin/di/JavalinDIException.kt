package io.mzlnk.javalin.di

class JavalinDIException(message: String) : RuntimeException(message) {

    /*
     * Prevent the stack trace from being filled in.
     */
    override fun fillInStackTrace(): Throwable = this

}