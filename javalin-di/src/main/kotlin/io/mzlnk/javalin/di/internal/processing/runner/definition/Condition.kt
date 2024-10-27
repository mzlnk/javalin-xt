package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.ApplicationContext

internal interface Condition {

    fun matches(context: ApplicationContext): Boolean

}