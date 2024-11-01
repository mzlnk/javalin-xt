package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.internal.context.JavalinContext

internal interface Condition {

    fun matches(context: JavalinContext): Boolean

}