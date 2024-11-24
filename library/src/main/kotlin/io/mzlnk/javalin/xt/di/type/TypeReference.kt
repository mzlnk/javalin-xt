package io.mzlnk.javalin.xt.di.type

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/*
 * Concept taken from com.fasterxml.jackson.core.type.TypeReference
 */
/**
 * Helper component used to represent a reference to a generic type.
 *
 * This is a workaround for the fact that JVM does not retain generic type information at runtime
 * known as type erasure.
 *
 * It works in the same way as TypeReference from Jackson library.
 *
 * @param T referenced type
 */
abstract class TypeReference<T> {

    val type: Type

    protected constructor() {
        val superClass = javaClass.genericSuperclass
        if (superClass is Class<*>) {
            throw IllegalArgumentException("Internal error: TypeReference constructed without actual type information")
        }
        this.type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    /**
     * Creates a reference to the specified class.
     *
     * For internal purposes only.
     * It is used to provide additional JavalinContext API methods for managing singletons with simple types
     * via Class<*> parameter instead of the TypeReference one because passing a generic type is not possible
     * due to type erasure.
     *
     * @param clazz the class to reference
     * @param T referenced type
     *
     * @return reference to the specified class
     */
    internal constructor(clazz: Class<T>) {
        this.type = clazz
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is TypeReference<*>) return false

        return type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    internal constructor(type: Type) {
        this.type = type
    }

    internal fun isAssignableFrom(typeRef: TypeReference<*>): Boolean {
        return if (this.type is Class<*> && typeRef.type is Class<*>) {
            this.type.isAssignableFrom(typeRef.type)
        } else {
            typeRef.type == this.type
        }
    }

}