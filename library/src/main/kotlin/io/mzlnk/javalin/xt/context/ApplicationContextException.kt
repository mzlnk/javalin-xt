package io.mzlnk.javalin.xt.context

/**
 * Exception thrown when an error related to [ApplicationContext] occurs.
 */
class ApplicationContextException(message: String) : RuntimeException(message) {

    /*
     * Prevent the stack trace from being filled in.
     */
    override fun fillInStackTrace(): Throwable = this

}