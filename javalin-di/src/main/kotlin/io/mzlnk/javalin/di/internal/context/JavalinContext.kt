package io.mzlnk.javalin.di.internal.context

internal class JavalinContext {

    private val singletons: MutableMap<Class<*>, Any> = mutableMapOf()

    fun size(): Int = singletons.size

    fun <T : Any> registerSingleton(type: Class<out T>, instance: T) {
        singletons[type] = instance
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> getSingleton(type: Class<T>): T {
        return singletons[type]!! as T
    }

}