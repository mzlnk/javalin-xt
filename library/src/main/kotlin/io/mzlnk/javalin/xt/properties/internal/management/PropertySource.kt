package io.mzlnk.javalin.xt.properties.internal.management

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.PathReference
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.find
import io.mzlnk.javalin.xt.properties.internal.utils.jackson.traverse
import io.mzlnk.javalin.xt.properties.Property
import java.net.URL

/**
 * Represents a source of properties
 */
internal interface PropertySource {

    /**
     * Finds a property by its path
     *
     * @param path path to the property
     *
     * @return found property or null if the property was not found
     */
    fun find(path: PathReference): Property?

}

/**
 * Represents an empty property source
 */
internal data object EmptyPropertySource : PropertySource {

    override fun find(path: PathReference): Property? = null

}

/**
 * Represents a property source based on a YAML file
 *
 * @property tree root node of the YAML file contents
 */
internal class FilePropertySource private constructor(
    private val tree: JsonNode
) : PropertySource {

    override fun find(path: PathReference): Property? = tree.find(path)?.asProperty

    internal companion object {

        private val ENV_VAR_REGEX = Regex("\\$\\{(.+)}")
        private val YAML_MAPPER = ObjectMapper(YAMLFactory())

        /**
         * Creates a property source based on a YAML file
         *
         * @param path path to the YAML file
         * @param resolveEnvironmentVariables flag whether to resolve environment variables in the file
         * @param environmentVariablesProvider provider of environment variables
         *
         * @return created property source
         */
        fun create(
            path: URL,
            resolveEnvironmentVariables: Boolean = true,
            environmentVariablesProvider: EnvironmentVariablesProvider
        ): PropertySource {
            val tree = YAML_MAPPER.readTree(path)

            if (resolveEnvironmentVariables) tree.resolveEnvironmentVariables(environmentVariablesProvider)

            return FilePropertySource(tree)
        }

        private fun JsonNode.resolveEnvironmentVariables(
            environmentVariablesProvider: EnvironmentVariablesProvider
        ): JsonNode = this.apply {
            fun asEnvironmentVariableIfApplicable(value: String, func: (value: String) -> Unit) {
                ENV_VAR_REGEX.matchEntire(value)?.let { match ->
                    val envVarName = match.groupValues[1]
                    val envVarValue = environmentVariablesProvider.get(envVarName)
                        ?: throw IllegalArgumentException("Environment variable `$envVarName` not found")

                    func(envVarValue)
                }
            }

            this.traverse { path, node ->
                if (node is ObjectNode) {
                    node.fields().forEach { (name, child) ->
                        if (child.isTextual) {
                            asEnvironmentVariableIfApplicable(child.textValue()) { envVarValue ->
                                node.set<JsonNode>(name, YAML_MAPPER.convertValue(envVarValue, JsonNode::class.java))
                            }
                        }
                    }
                }
                if (node is ArrayNode) {
                    node.elements().asSequence().toList().forEachIndexed { index, child ->
                        if (child.isTextual) {
                            asEnvironmentVariableIfApplicable(child.textValue()) { envVarValue ->
                                node.set(index, YAML_MAPPER.convertValue(envVarValue, JsonNode::class.java))
                            }
                        }
                    }
                }

            }
        }

    }

}

private val JsonNode.asProperty: Property
    get() = when (this) {
        is NumericNode -> NumberProperty(value = this.numberValue())
        is TextNode -> StringProperty(value = this.textValue())
        is BooleanNode -> BooleanProperty(value = this.booleanValue())
        is ObjectNode -> ObjectProperty
        is ArrayNode -> this.asProperty
        else -> throw IllegalArgumentException("Unsupported property value: ${this.asText()}")
    }

private val SUPPORTED_TYPES = arrayOf(
    NumericNode::class.java,
    TextNode::class.java,
    BooleanNode::class.java,
    ObjectNode::class.java
)

private val ArrayNode.asProperty: Property
    get() {
        val children = this.elements().asSequence().toList()

        if (children.any { child -> SUPPORTED_TYPES.none { it.isAssignableFrom(child::class.java) } }) {
            throw IllegalArgumentException("Unsupported property value: ${this.asText()}")
        }

        val childrenType = when {
            this.elements().asSequence().all { it is NumericNode } -> NumberProperty::class
            this.elements().asSequence().all { it is TextNode } -> StringProperty::class
            this.elements().asSequence().all { it is BooleanNode } -> BooleanProperty::class
            else -> ObjectProperty::class
        }

        return when (childrenType) {
            NumberProperty::class -> NumberListProperty(children.map { it.numberValue() })
            StringProperty::class -> StringListProperty(children.map { it.textValue() })
            BooleanProperty::class -> BooleanListProperty(children.map { it.booleanValue() })
            else -> ObjectListProperty
        }
    }