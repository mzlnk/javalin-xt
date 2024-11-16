package io.mzlnk.javalin.ext.internal.processing

/**
 * Represents an actual project structure of components related to Javalin DI (e.g. modules, singletons).
 *
 * @param modules list of classes annotated with [io.mzlnk.javalin.di.Module] in the project
 */
internal data class Project(
    val modules: List<ModuleClass>
)

/**
 * Represents a module class annotated with [io.mzlnk.javalin.di.Module].
 *
 * @param type information about the module class
 * @param singletons list of singletons defined in the module
 */
internal data class ModuleClass(
    val type: Type,
    val singletons: List<SingletonMethod>
)

/**
 * Represents a singleton method defined in a module class.
 *
 * @param name name of the method
 * @param returnType information about return type of the method
 * @param parameters list of parameters of the method
 */
internal data class SingletonMethod(
    val name: String,
    val returnType: Type,
    val parameters: List<Parameter> = emptyList()
) {

    /**
     * Represents a parameter of a singleton method.
     *
     * @param name name of the parameter
     * @param type information about the type of the parameter
     */
    internal data class Parameter(
        val name: String,
        val type: Type
    )

}

/**
 * Stores information about a Java/Kotlin type.
 *
 * @param packageName package name of the type
 * @param name simple name of the type
 */
internal data class Type(
    val packageName: String,
    val name: String
) {

    /**
     * Returns the qualified name of the type.
     * Example: `a.b.c.Type`
     *
     * @return qualified name of the type
     */
    val qualifiedName: String = "$packageName.$name"

    override fun toString(): String = qualifiedName

}