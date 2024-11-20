package io.mzlnk.javalin.xt.internal.context

import io.mzlnk.javalin.xt.JavalinDIException
import io.mzlnk.javalin.xt.definition.SingletonDefinition
import io.mzlnk.javalin.xt.internal.utils.graph.Cycle

/**
 * Exception thrown when there are multiple candidates found for a given singleton definition.
 */
internal fun multipleCandidatesFoundException(
    identifier: SingletonDefinition.Identifier<*>
): JavalinDIException {
    return JavalinDIException("Multiple candidates found for $identifier")
}

/**
 * Exception thrown when there are no candidates found for a given singleton definition.
 */
internal fun noCandidatesFoundException(
    identifier: SingletonDefinition.Identifier<*>
): JavalinDIException {
    return JavalinDIException("No candidates found for $identifier")
}

/**
 * Exception thrown when a dependency cycle is found in the dependency graph.
 */
internal fun dependencyCycleFoundException(
    cycles: List<Cycle<SingletonDefinition<*>>>
): JavalinDIException {
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

    return JavalinDIException(message)
}