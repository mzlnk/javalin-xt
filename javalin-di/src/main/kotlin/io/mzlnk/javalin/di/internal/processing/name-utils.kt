package io.mzlnk.javalin.di.internal.processing

internal fun singletonDefinitionProviderQualifiedName(module: ModuleClass): String {
    return "${module.type.qualifiedName}SingletonDefinitionProvider"
}