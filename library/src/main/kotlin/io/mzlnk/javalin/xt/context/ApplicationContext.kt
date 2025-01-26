package io.mzlnk.javalin.xt.context

/**
 * Represents a context built based on provided singleton definitions.
 */
interface ApplicationContext {

    /**
     * Returns the number of singletons registered in the context.
     *
     * @return number of singletons
     */
    fun size(): Int

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @param name the name of the instance to get
     *
     * @return instance of the singleton if found, null otherwise
     */
    fun <T : Any> findInstance(type: TypeReference<T>, name: String? = null): T?

    /**
     * Gets an instance of the specified list type from the context.
     *
     * @param type the list type of the instance to get
     * @param name the name of the instance to get
     * @param elementName the name of the element to get
     *
     * @return instance of the singleton if found, null otherwise
     */
    fun <T : Any> findInstance(
        type: TypeReference<List<T>>,
        name: String? = null,
        elementName: String? = null
    ): List<T>?

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @param name the name of the instance to get
     *
     * @return instance of the singleton if found, null otherwise
     */
    fun <T : Any> findInstance(type: Class<T>, name: String? = null): T? =
        findInstance(type = object : TypeReference<T>(type) {}, name = name)

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @param name the name of the instance to get
     *
     * @return the instance if exists
     *
     * @throws IllegalStateException if no instance found for the specified type
     */
    fun <T : Any> getInstance(type: TypeReference<T>, name: String? = null): T =
        findInstance(type = type, name = name)
            ?: throw IllegalStateException("No instance found for ${type.type.typeName}${name?.let { "($name)" }}")

    /**
     * Gets an instance of the specified list type from the context.
     *
     * @param type the list type of the instance to get
     * @param name the name of the instance to get
     * @param elementName the name of the element to get
     *
     * @return the instance if exists or an empty list otherwise
     */
    fun <T : Any> getInstance(
        type: TypeReference<List<T>>,
        name: String? = null,
        elementName: String? = null
    ): List<T> =
        findInstance(type = type, name = name, elementName = elementName) ?: emptyList()

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @param name the name of the instance to get
     *
     * @return the instance if exists
     *
     * @throws IllegalStateException if no instance found for the specified type
     */
    fun <T : Any> getInstance(type: Class<T>, name: String? = null): T =
        getInstance(type = object : TypeReference<T>(clazz = type) {}, name = name)

}