package io.mzlnk.javalin.di.definition

interface SingletonDefinitionProvider {

    val definitions: List<SingletonDefinition<*>>

}