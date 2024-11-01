package io.mzlnk.javalin.di.internal.processing

internal data class Project(
    val modules: List<Clazz>
)

data class Type(
    val packageName: String,
    val name: String
) {

    val qualifiedName: String = "$packageName.$name"

    override fun toString(): String = qualifiedName

}

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

internal data class Clazz(
    val type: Type,
    val methods: List<Method> = emptyList(),
    val annotations: List<Annotation> = emptyList()
) {

    override fun toString(): String = type.qualifiedName

}

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