package io.mzlnk.javalin.di.internal.definition

internal class StaticClassSource private constructor(private val classes: List<Clazz>) : ClazzSource {

    override fun read(): Collection<Clazz> {
        return classes
    }

    companion object {

        fun of(vararg classes: Clazz): ClazzSource {
            return StaticClassSource(classes.toList())
        }

    }

}