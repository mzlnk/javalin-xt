package io.mzlnk.javalin.di.internal.definition

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

internal interface ClassSource {

    fun read(): Collection<Class<*>>

}

internal object ClasspathClassSource : ClassSource {

    override fun read(): Collection<Class<*>> {
        val reflections = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(SubTypesScanner(false))
        )

        return reflections.getSubTypesOf(Any::class.java)
    }

}