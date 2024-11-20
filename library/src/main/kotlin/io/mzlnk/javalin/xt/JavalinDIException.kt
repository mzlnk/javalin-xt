package io.mzlnk.javalin.xt

/**
 * Exception thrown when an error related to Javalin DI occurs.
 */
class JavalinDIException(message: String) : RuntimeException(message) {

    /*
     * Prevent the stack trace from being filled in.
     */
    override fun fillInStackTrace(): Throwable = this

}