package io.mzlnk.javalin.di.internal.processing

internal data class Project(
    val modules: List<ModuleClass>
)

internal data class ModuleClass(
    val type: Type,
    val singletons: List<SingletonMethod>
)

internal data class SingletonMethod(
    val name: String,
    val returnType: Type,
    val parameters: List<Parameter> = emptyList()
) {

    internal data class Parameter(
        val name: String,
        val type: Type
    )

}

internal data class Type(
    val packageName: String,
    val name: String
) {

    val qualifiedName: String = "$packageName.$name"

    override fun toString(): String = qualifiedName

}