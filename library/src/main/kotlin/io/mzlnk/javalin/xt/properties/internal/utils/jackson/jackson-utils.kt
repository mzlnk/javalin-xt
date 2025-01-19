package io.mzlnk.javalin.xt.properties.internal.utils.jackson

import com.fasterxml.jackson.databind.JsonNode

internal fun JsonNode.find(path: PathReference): JsonNode? = path.parts.fold<PathReference.Part, JsonNode?>(this) { node, part ->
    when (part) {
        is PathReference.Part.Property -> node?.takeIf { it.has(part.name) }?.get(part.name)
        is PathReference.Part.Index -> node?.takeIf { it.isArray && it.has(part.index) }?.get(part.index)
    }
}

internal fun JsonNode.traverse(func: (path: PathReference, node: JsonNode) -> Unit) {
    fun traverse(node: JsonNode, path: PathReference) {
        func(path, node)
        when {
            node.isObject -> node.fields().forEach { (name, child) ->
                traverse(child, path + PathReference.Part.Property(name))
            }
            node.isArray -> node.elements().asSequence().toList().forEachIndexed{ index, child ->
                traverse(child, path + PathReference.Part.Index(index))
            }
        }
    }

    traverse(this, PathReference.empty())
}