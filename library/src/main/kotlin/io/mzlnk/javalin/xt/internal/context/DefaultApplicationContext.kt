package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.context.ApplicationContext
import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
import io.mzlnk.javalin.xt.context.TypeReference

/**
 * Represents a context built based on provided singleton definitions via SingletonDefinitionContext
 */
internal class DefaultApplicationContext private constructor(
    private val definitionsContext: SingletonDefinitionContext
) : ApplicationContext {

    override fun size(): Int = definitionsContext.size()

    override fun <T : Any> findInstance(type: TypeReference<T>): T? {
        val identifier = SingletonDefinition.Identifier(typeRef = type)
        return definitionsContext.findInstance(identifier)
    }

    internal companion object {

        fun create(context: SingletonDefinitionContext): ApplicationContext {
            return DefaultApplicationContext(definitionsContext = context)
        }

    }
}