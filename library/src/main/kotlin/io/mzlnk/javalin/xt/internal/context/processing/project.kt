package io.mzlnk.javalin.xt.internal.context.processing

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
    val parameters: List<Parameter> = emptyList(),
    val annotations: List<Annotation> = emptyList(),
) {

    /**
     * Represents a parameter of a singleton method.
     *
     * @param name name of the parameter
     * @param type information about the type of the parameter
     * @param annotations list of annotations of the parameter
     */
    internal data class Parameter(
        val name: String,
        val type: Type,
        val annotations: List<Annotation>,
    )

}

/**
 * Stores information about a Java/Kotlin type.
 *
 * @param packageName package name of the type
 * @param name simple name of the type
 * @param typeParameters list of generic types of the type
 * @param nullable whether the type is nullable
 */
internal data class Type(
    val packageName: String,
    val name: String,
    val nullable: Boolean,
    val typeParameters: List<Type> = emptyList(),
) {

    /**
     * Returns the qualified name of the type.
     * Examples:
     * - `a.b.c.Type`
     * - `a.b.c.Type<e.f.GenericType>`
     *
     * @return qualified name of the type
     */
    val qualifiedName: String = run {
        val typeParameters = typeParameters
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = "<", postfix = ">") { it.toString() }
            ?: ""

        val nullableMarker = if (nullable) "?" else ""

        "$packageName.$name$typeParameters$nullableMarker"
    }

    override fun toString(): String = qualifiedName

}

/**
 * Represents an annotation.
 *
 * @param type information about the annotation type
 * @param parameters map of annotation parameters
 */
internal data class Annotation(
    val type: Type,
    val parameters: Map<String, Any?>
)