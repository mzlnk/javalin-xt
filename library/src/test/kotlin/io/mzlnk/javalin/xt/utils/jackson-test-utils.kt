package io.mzlnk.javalin.xt.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*

private val objectMapper = ObjectMapper()

fun intNode(value: Int): IntNode = IntNode.valueOf(value)

fun longNode(value: Long): LongNode = LongNode.valueOf(value)

fun booleanNode(value: Boolean): BooleanNode = BooleanNode.valueOf(value)

fun textNode(value: String): TextNode = TextNode.valueOf(value)

fun arrayNode(vararg elements: JsonNode): ArrayNode =
    objectMapper.createArrayNode().apply {
        elements.forEach { add(it) }
    }

fun objectNode(vararg children: Pair<String, JsonNode>): ObjectNode =
    objectMapper.createObjectNode().apply {
        children.forEach { (key, value) -> set<JsonNode>(key, value) }
    }