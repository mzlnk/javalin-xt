package io.mzlnk.javalin.di.internal.processing

internal data class Clazz(
    val type: Type,
    val methods: List<Method> = emptyList(),
    val annotations: List<Annotation> = emptyList()
) {

    override fun toString(): String = type.qualifiedName

}