package io.mzlnk.javalin.xt.internal.context.processing

/**
 * Returns the qualified name of the singleton definition provider for the given module.
 * Example: `a.b.c.TestModuleSingletonDefinitionProvider`
 *
 * @param module module to create the definition provider for
 *
 * @return qualified name of the singleton definition provider
 */
internal fun singletonDefinitionProviderQualifiedName(module: Module): String {
    return "${module.type.qualifiedName}SingletonDefinitionProvider"
}

/**
 * Returns the simple name of the singleton definition provider for the given module.
 * Example: `TestModuleSingletonDefinitionProvider`
 *
 * @param module module to create the definition provider for
 *
 * @return simple name of the singleton definition provider
 */
internal fun singletonDefinitionProviderSimpleName(module: Module): String {
    return "${module.type.name}SingletonDefinitionProvider"
}