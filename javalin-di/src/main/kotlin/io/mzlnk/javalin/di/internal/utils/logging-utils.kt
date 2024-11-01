package io.mzlnk.javalin.di.internal.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal inline fun <reified T : Any> T.logger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger(T::class.java)
}