package io.mzlnk.javalin.di.internal.definition

import kotlin.reflect.KClass

internal data class Annotation(
    val type: Type,
    val arguments: List<Argument> = emptyList()
) {

    override fun toString(): String = "@${type.qualifiedName}"

    fun isTypeOf(clazz: KClass<*>): Boolean {
        return clazz.qualifiedName == type.qualifiedName
    }

    internal data class Argument(
        val name: String,
        val value: Any?
    )

}
