package io.mzlnk.javalin.di.internal.definition

internal data class Annotation(
    val type: Type,
    val arguments: List<Argument> = emptyList()
) {

    override fun toString(): String = "@${type.qualifiedName}"

    internal data class Argument(
        val name: String,
        val value: Any?
    )

}
