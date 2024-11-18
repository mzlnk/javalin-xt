package io.mzlnk.javalin.ext.e2e.app

/**
 * This file consists of generic use components for E2E testing purposes.
 *
 * Each component group (A,B,C,...) has a representative class that accepts any number of dependencies
 * via dedicated constructor `constructor(vararg components: Pair<String, Any>`)`, where:
 * - first pair item is the name of the component (e.g. `D`, `E`)
 * - second pair item is the instance of the component (e.g. `ComponentD()`, `ComponentE()`)
 *
 * Additionally, components group A, B, C:
 * - each of them has interface `Type<X>`
 * - each of them has super type `Component<X>`
 * - each of them has concrete implementations `Component<1|2|...><X>` extending super type `Component<X>`
 *
 * Additionally, component group G contains ComponentG<T> that accepts generic type
 * for testing DI with generic types
 */

// @formatter:off
private interface TypeA
private open class ComponentA(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
private class ComponentA1(vararg components: Pair<String, Any>) : ComponentA(*components)
private class ComponentA2(vararg components: Pair<String, Any>) : ComponentA(*components)

private interface TypeB
private open class ComponentB(vararg components: Pair<String, Any>) : TypeB { val components = mapOf(*components) }
private class ComponentB1(vararg components: Pair<String, Any>) : ComponentB(*components)
private class ComponentB2(vararg components: Pair<String, Any>) : ComponentB(*components)

private interface TypeC
private open class ComponentC(vararg components: Pair<String, Any>) : TypeC { val components = mapOf(*components) }
private class ComponentC1(vararg components: Pair<String, Any>) : ComponentC(*components)
private class ComponentC2(vararg components: Pair<String, Any>) : ComponentC(*components)

private class ComponentD(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
private class ComponentE(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
private class ComponentF(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }

private class ComponentG<T : Any>(vararg components: Pair<String, Any>) : TypeA { val components = mapOf(*components) }
// @formatter:on