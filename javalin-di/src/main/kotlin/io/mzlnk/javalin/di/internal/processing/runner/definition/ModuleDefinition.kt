package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.internal.processing.Clazz
import java.util.*

internal data class ModuleDefinition(
    val source: Clazz,
    val singletons: List<SingletonDefinition>,
    val conditions: List<Condition>
) {

    val id: UUID = UUID.randomUUID()

}