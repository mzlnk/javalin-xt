package io.mzlnk.javalin.di.internal.context

import io.mzlnk.javalin.di.JavalinDIException
import io.mzlnk.javalin.di.definition.SingletonDefinition
import io.mzlnk.javalin.di.internal.utils.graph.Cycle

internal fun multipleCandidatesFoundException(
    identifier: SingletonDefinition.Identifier<*>
): JavalinDIException {
    return JavalinDIException("Multiple candidates found for $identifier")
}

internal fun noCandidatesFoundException(
    identifier: SingletonDefinition.Identifier<*>
): JavalinDIException {
    return JavalinDIException("No candidates found for $identifier")
}

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