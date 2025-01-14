package io.mzlnk.javalin.xt.internal.context.processing

/**
 * Represents an actual project structure of components related to Javalin DI (e.g. modules, singletons).
 *
 * @param modules list of classes annotated with [io.mzlnk.javalin.di.Module] in the project
 */
internal data class Project(
    val modules: List<Module>
)

/**
 * Represents a module class annotated with [io.mzlnk.javalin.di.Module].
 *
 * @param type information about the module class
 * @param singletons list of singletons defined in the module
 */
internal data class Module(
    val type: Type,
    val singletons: List<Singleton>
)

/**
 * Represents a singleton method defined in a module class.
 *
 * @param methodName name of the method
 * @param type information about return type of the method
 * @param dependencies list of parameters of the method
 */
internal data class Singleton(
    val methodName: String,
    val type: Type,
    val conditionals: List<Conditional> = emptyList(),
    val dependencies: List<Dependency> = emptyList(),
) {
    
    internal interface Conditional {
        
        data class OnProperty(
            val key: String,
            val havingValue: String,
        ) : Conditional
        
    }

    /**
     * Represents a parameter of a singleton method.
     *
     * @param name name of the parameter
     * @param type information about the type of the parameter
     * @param annotations list of annotations of the parameter
     */
    internal interface Dependency {
        
        val type: Type
        
        data class Singleton(override val type: Type) : Dependency

        data class Property(
            override val type: Type,
            val key: String,
            val required: Boolean
        ) : Dependency
        
    }

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