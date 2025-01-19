package io.mzlnk.javalin.xt.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Returns a logger for the given class.
 *
 * @return logger for the given class
 */
internal inline fun <reified T : Any> T.logger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger(T::class.java)
}