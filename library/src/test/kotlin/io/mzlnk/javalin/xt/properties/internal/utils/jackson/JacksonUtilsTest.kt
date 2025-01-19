package io.mzlnk.javalin.xt.properties.internal.utils.jackson

import com.fasterxml.jackson.databind.JsonNode
import io.mzlnk.javalin.xt.utils.arrayNode
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.PathReference
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.find
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.traverse
import io.mzlnk.javalin.xt.utils.objectNode
import io.mzlnk.javalin.xt.utils.textNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JacksonUtilsTest {

    @Test
    fun `should find node by path reference when path contains property names`() {
        // given:
        val node = objectNode(
            "property1" to objectNode(
                "property2" to objectNode(
                    "property3" to textNode("value")
                )
            )
        )

        // and:
        val path = PathReference.create("property1.property2.property3")

        // when:
        val result = node.find(path)

        // then:
        assertThat(result).isEqualTo(textNode("value"))
    }

    @Test
    fun `should find node by path reference when path contains property names and indexes`() {
        // given:
        val node = objectNode(
            "property1" to arrayNode(
                objectNode(
                    "property2" to textNode("value1")
                ),
                objectNode(
                    "property2" to textNode("value2")
                )
            )
        )

        // and:
        val path = PathReference.create("property1[1].property2")

        // when:
        val result = node.find(path)

        // then:
        assertThat(result).isEqualTo(textNode("value2"))
    }

    @Test
    fun `should traverse all nodes in the tree`() {
        // given:
        val tree = objectNode(
            "property1" to objectNode(
                "property2" to textNode("value2"),
                "property3" to arrayNode(
                    textNode("value3a"),
                    objectNode(
                        "property4" to textNode("value4")
                    )
                )
            )
        )

        // and:
        val visitedNodes = mutableListOf<Pair<PathReference, JsonNode>>()

        // when:
        tree.traverse { path, node ->
            visitedNodes.add(path to node)
        }

        // then:
        assertThat(visitedNodes).containsExactly(
            PathReference.empty() to tree,
            PathReference.create("property1") to tree["property1"],
            PathReference.create("property1.property2") to tree["property1"]["property2"],
            PathReference.create("property1.property3") to tree["property1"]["property3"],
            PathReference.create("property1.property3[0]") to tree["property1"]["property3"][0],
            PathReference.create("property1.property3[1]") to tree["property1"]["property3"][1],
            PathReference.create("property1.property3[1].property4") to tree["property1"]["property3"][1]["property4"]
        )
    }

}
