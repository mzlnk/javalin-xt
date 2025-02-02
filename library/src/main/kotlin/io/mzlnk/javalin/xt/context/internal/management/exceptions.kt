package io.mzlnk.javalin.xt.context.internal.management

import io.mzlnk.javalin.xt.context.ApplicationContextException
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.context.generated.SingletonDefinition.DependencyIdentifier
import io.mzlnk.javalin.xt.context.internal.utils.graph.Cycle

/**
 * Exception thrown when there are multiple candidates found for a given singleton definition.
 */
internal fun multipleCandidatesFoundException(
    singletonToMatch: SingletonToMatch<*>
): ApplicationContextException {
    return ApplicationContextException("Multiple candidates found for $singletonToMatch")
}

/**
 * Exception thrown when there are no candidates found for a given singleton definition.
 */
internal fun noCandidatesFoundException(
    singletonToMatch: SingletonToMatch<*>
): ApplicationContextException {
    return ApplicationContextException("No candidates found for $singletonToMatch")
}

/**
 * Exception thrown when a dependency cycle is found in the dependency graph.
 */
internal fun dependencyCycleFoundException(
    cycles: List<Cycle<SingletonDefinition<*>>>
): ApplicationContextException {
    val message = StringBuilder()
        .append("Failed to create context due to dependency cycle(s):")
        .apply {
            cycles.forEachIndexed { idx, cycle ->
                append("\nCycle #${idx + 1}:\n")
                append(cycle.toString())
                if (idx < cycles.size - 1) append("\n")
            }
        }
        .toString()

    return ApplicationContextException(message)
}

internal fun propertyNotFound(
    singletonIdentifier: SingletonDefinition.Identifier<*>,
    dependencyIdentifier: DependencyIdentifier.Property<*>
): ApplicationContextException {
    return ApplicationContextException("Failed to create singleton `$singletonIdentifier`. Property `${dependencyIdentifier.key}` not found.")
}

internal fun invalidPropertyType(
    singletonIdentifier: SingletonDefinition.Identifier<*>,
    dependencyIdentifier: DependencyIdentifier.Property<*>,
): ApplicationContextException {
    return ApplicationContextException("Failed to create singleton `$singletonIdentifier`. Property `${dependencyIdentifier.key}` has invalid type.")
}