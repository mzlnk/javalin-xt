package io.mzlnk.javalin.di.core.internal.definition

class StaticClassSource(vararg classes: Class<*>) : ClassSource {

    private val classes: Collection<Class<*>> = classes.toList()

    override fun read(): Collection<Class<*>> {
        return classes
    }
}