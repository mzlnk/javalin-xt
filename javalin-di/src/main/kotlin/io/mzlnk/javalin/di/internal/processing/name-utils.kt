package io.mzlnk.javalin.di.internal.processing

internal fun singletonDefinitionProviderQualifiedName(module: ModuleClass): String {
    return "${module.type.qualifiedName}SingletonDefinitionProvider"
}

internal fun singletonDefinitionProviderSimpleName(module: ModuleClass): String {
    return "${module.type.name}SingletonDefinitionProvider"
}