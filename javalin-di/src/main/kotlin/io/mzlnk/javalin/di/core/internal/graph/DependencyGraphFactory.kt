package io.mzlnk.javalin.di.core.internal.graph

import io.mzlnk.javalin.di.core.internal.definition.SingletonDefinition
import java.util.*

internal object DependencyGraphFactory {

    fun create(definitions: List<SingletonDefinition>): DependencyGraph {
        val nodes: Array<SingletonDefinition> = definitions.toTypedArray()
        val edges: Array<Array<Boolean>> = Array(nodes.size) { Array(nodes.size) { false } }

        val nodeIdxsByIds: Map<UUID, Int> = nodes
            .mapIndexed { idx, definition -> definition.id to idx }
            .associate { it }

        nodes.forEach { node ->
            node.dependencies.forEach { dependency ->
                nodes
                    // skip the node itself
                    .filter { candidate -> candidate.id != node.id }
                    // apply filters:
                    .let { candidates -> DependenciesFilter.ByName.filter(dependency, candidates) }
                    .let { candidates -> DependenciesFilter.ByType.filter(dependency, candidates) }
                    // apply validators:
                    .also { candidates ->
                        DependenciesValidator.ExactlyOneCandidateForDependency.validate(
                            dependency,
                            candidates
                        )
                    }
                    // set corresponding edges
                    .forEach { candidate ->
                        edges[nodeIdxsByIds[candidate.id]!!][nodeIdxsByIds[node.id]!!] = true
                    }

            }
        }

        // validate against circular dependencies
        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (i == j) {
                    continue
                }

                if (edges[i][j] && edges[j][i]) {
                    throw DependencyGraphCreationException.CircularDependencyFound()
                }
            }
        }

        return DependencyGraph(
            _nodes = nodes,
            _edges = edges
        )
    }


}

private interface DependenciesFilter {

    fun filter(
        dependency: SingletonDefinition.Key,
        candidates: List<SingletonDefinition>
    ): List<SingletonDefinition>

    object ByType : DependenciesFilter {

        override fun filter(
            dependency: SingletonDefinition.Key,
            candidates: List<SingletonDefinition>
        ): List<SingletonDefinition> {
            return candidates.filter { candidate ->
                // TODO: add support for subtypes
                dependency.type == candidate.key.type
            }
        }
    }

    object ByName : DependenciesFilter {

        /*
         *  Filtering matrix:
         *
         *  node \ dep | no-name | name                   |
         *  no-name    |    1    |  0                     |
         *  name       |    1    |  node.name == dep.name |
         */
        override fun filter(
            dependency: SingletonDefinition.Key,
            candidates: List<SingletonDefinition>
        ): List<SingletonDefinition> {
            return candidates.filter { candidate ->
                dependency.name?.let { name -> name == candidate.key.name } ?: true
            }
        }

    }


}

private interface DependenciesValidator {

    fun validate(
        dependency: SingletonDefinition.Key,
        candidates: List<SingletonDefinition>
    )

    object ExactlyOneCandidateForDependency : DependenciesValidator {

        override fun validate(
            dependency: SingletonDefinition.Key,
            candidates: List<SingletonDefinition>
        ) {
            if(candidates.isEmpty()) {
                throw DependencyGraphCreationException.DependencyNotFound()
            }
            if(candidates.size > 1) {
                throw DependencyGraphCreationException.MultipleDependenciesFound()
            }
        }
    }

}

internal abstract class DependencyGraphCreationException(message: String) : RuntimeException(message) {

    class DependencyNotFound : DependencyGraphCreationException("Dependency not found")
    class MultipleDependenciesFound : DependencyGraphCreationException("Multiple dependencies found")
    class CircularDependencyFound : DependencyGraphCreationException("Circular dependency found")

}