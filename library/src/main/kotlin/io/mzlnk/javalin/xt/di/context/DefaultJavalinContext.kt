package io.mzlnk.javalin.xt.di.context

import io.javalin.Javalin
import io.mzlnk.javalin.xt.di.definition.SingletonDefinition
import io.mzlnk.javalin.xt.di.type.TypeReference
import io.mzlnk.javalin.xt.internal.di.context.SingletonDefinitionContext

/**
 * Represents a context built based on provided singleton definitions via SingletonDefinitionContext
 */
internal class DefaultJavalinContext private constructor(
    private val definitionsContext: SingletonDefinitionContext
) : JavalinContext {

    override fun size(): Int = definitionsContext.size()

    override fun <T : Any> Javalin.findInstance(type: TypeReference<T>): T? {
        val identifier = SingletonDefinition.Identifier(typeRef = type)
        return definitionsContext.findInstance(identifier)
    }

    internal companion object {

        fun create(context: SingletonDefinitionContext): JavalinContext {
            return DefaultJavalinContext(definitionsContext = context)
        }

    }
}