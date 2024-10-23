package io.mzlnk.javalin.di.internal.definition

internal data class Clazz(
    val type: Type,
    val methods: List<Method> = emptyList(),
    val annotations: List<Annotation> = emptyList()
) {

    override fun toString(): String = type.qualifiedName

}