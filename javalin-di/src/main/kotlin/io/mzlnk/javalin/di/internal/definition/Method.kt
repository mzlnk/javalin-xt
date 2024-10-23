package io.mzlnk.javalin.di.internal.definition

internal data class Method(
    val name: String,
    val returnType: Type,
    val annotations: List<Annotation> = emptyList(),
    val parameters: List<Parameter> = emptyList()
) {

    internal data class Parameter(
        val name: String,
        val type: Type,
        val annotations: List<Annotation> = emptyList()
    )

}