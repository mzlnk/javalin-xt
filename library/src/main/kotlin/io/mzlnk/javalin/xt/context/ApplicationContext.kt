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
     * @return instance of the singleton if found, null otherwise
     */
    fun <T : Any> findInstance(type: TypeReference<T>): T?

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @return instance of the singleton if found, null otherwise
     */
    fun <T : Any> findInstance(type: Class<T>): T? =
        findInstance(object : TypeReference<T>(type) {})

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @return the instance if exists
     *
     * @throws IllegalStateException if no instance found for the specified type
     */
    fun <T : Any> getInstance(type: TypeReference<T>): T =
        findInstance(type) ?: throw IllegalStateException("No instance found for $type")

    /**
     * Gets an instance of the specified type from the context.
     *
     * @param type the type of the instance to get
     * @return the instance if exists
     *
     * @throws IllegalStateException if no instance found for the specified type
     */
    fun <T : Any> getInstance(type: Class<T>): T = getInstance(object : TypeReference<T>(clazz = type) {})

}