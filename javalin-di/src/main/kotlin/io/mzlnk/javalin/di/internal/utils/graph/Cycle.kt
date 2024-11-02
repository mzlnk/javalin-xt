package io.mzlnk.javalin.di.internal.utils.graph

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
