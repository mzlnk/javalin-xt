package io.mzlnk.javalin.xt.context

import io.mzlnk.javalin.xt.context.generated.SingletonDefinition
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import io.mzlnk.javalin.xt.properties.Property

inline fun <reified T : Any> identifier(
    type: Class<T>,
    name: String? = null
): SingletonDefinition.Identifier<T> =
    SingletonDefinition.Identifier(typeRef = object : TypeReference<T>() {}, name = name)

inline fun <reified T : Any> identifier(
    typeRef: TypeReference<T>,
    name: String? = null
): SingletonDefinition.Identifier<T> =
    SingletonDefinition.Identifier(typeRef = typeRef, name = name)

inline fun <reified T : Any> singletonSingularDependency(
    type: Class<T>,
    name: String? = null
): SingletonDefinition.DependencyIdentifier<T> =
    SingletonDefinition.DependencyIdentifier.Singleton.Singular(
        typeRef = object : TypeReference<T>() {},
        name = name
    )

inline fun <reified T : Any> singletonSingularDependency(
    typeRef: TypeReference<T>,
    name: String? = null
): SingletonDefinition.DependencyIdentifier<T> =
    SingletonDefinition.DependencyIdentifier.Singleton.Singular(typeRef = typeRef, name = name)

inline fun <reified T : Any> singletonListDependency(
    type: TypeReference<List<T>>,
    name: String? = null,
    elementName: String? = null
): SingletonDefinition.DependencyIdentifier<List<T>> =
    SingletonDefinition.DependencyIdentifier.Singleton.List(
        typeRef = type,
        name = name,
        elementName = elementName
    )

inline fun <reified T : Any> propertyDependency(
    key: String,
    noinline valueProvider: (Property) -> T,
    required: Boolean
): SingletonDefinition.DependencyIdentifier<T> =
    SingletonDefinition.DependencyIdentifier.Property(
        key = key,
        valueProvider = valueProvider,
        required = required
    )

// general purpose types for testing
internal interface TypeA
internal interface TypeB

// general purpose classes for testing
// @formatter:off
internal open class ComponentA(vararg components: Pair<String, Any?>) : TypeA { val components = mapOf(*components) }
internal open class ComponentB(vararg components: Pair<String, Any?>) : TypeB { val components = mapOf(*components) }
internal open class ComponentC(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
internal open class ComponentD(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
internal open class ComponentE(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
internal open class ComponentG<T>(vararg components: Pair<String, Any?>) { val components = mapOf(*components) }
// @formatter:on

internal class ComponentA1(vararg components: Pair<String, Any?>) : ComponentA(*components)
internal class ComponentA2(vararg components: Pair<String, Any?>) : ComponentA(*components)

internal class ComponentB1(vararg components: Pair<String, Any?>) : ComponentB(*components)
internal class ComponentB2(vararg components: Pair<String, Any?>) : ComponentB(*components)

internal class TestApplicationProperties(vararg properties: Pair<String, Property>) : ApplicationProperties {

    private val properties = mapOf(*properties)

    override fun get(key: String): Property = properties[key] ?: throw NoSuchElementException("Property not found")

    override fun getOrNull(key: String): Property? = properties[key]
}