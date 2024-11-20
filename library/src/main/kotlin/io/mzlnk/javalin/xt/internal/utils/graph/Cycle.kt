package io.mzlnk.javalin.xt.internal.utils.graph

/**
 * Represents a cycle in a graph.
 *
 * @param E type of the nodes in the cycle
 * @property nodes list of nodes in the cycle
 */
data class Cycle<E>(val nodes: List<E>) {

    /**
     * Returns graphical representation of a cycle.
     * Example:
     * ┌-> A -> B -> C -> D -┐
     * └---------------------┘
     */
    override fun toString(): String {
        val chain = nodes.joinToString(prefix = "┌->", separator = " -> ", postfix = " -┐") { it.toString() }
        val border = "└" + "-".repeat(chain.length - 2) + "┘"

        return chain + "\n" + border
    }

}
