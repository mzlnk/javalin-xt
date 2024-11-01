package io.mzlnk.javalin.di.definition

import java.util.UUID

data class SingletonDefinition<T>(
    val type: Class<T>,
    val dependencies: List<Class<*>>,
    val instanceProvider: (args: List<*>) -> T
) where T : Any{

    val id: UUID = UUID.randomUUID()

}
