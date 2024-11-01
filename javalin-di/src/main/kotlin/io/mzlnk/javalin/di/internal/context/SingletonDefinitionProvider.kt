package io.mzlnk.javalin.di.internal.context

interface SingletonDefinitionProvider {

    val definitions: List<SingletonDefinition<*>>

}