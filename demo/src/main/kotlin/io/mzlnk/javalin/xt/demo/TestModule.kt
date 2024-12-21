package io.mzlnk.javalin.xt.demo

import io.mzlnk.javalin.xt.context.Conditional
import io.mzlnk.javalin.xt.context.Module
import io.mzlnk.javalin.xt.context.Property
import io.mzlnk.javalin.xt.context.Singleton


@Module
class TestModule {

    @Singleton
    fun componentA(): ComponentA = ComponentA()

    @Singleton
    fun componentB(componentA: ComponentA): ComponentB = ComponentB()

    @Singleton
    fun componentC(componentA: ComponentA, componentB: ComponentB): ComponentC = ComponentC()

    @Singleton
    fun componentD(
        componentA: ComponentA,
        componentB: ComponentB,
        componentC: ComponentC
    ): ComponentD = ComponentD()

    @Singleton
    @Conditional.OnProperty(property = "property10", havingValue = "value10")
    fun componentE(@Property("property10") value: String): ComponentE = ComponentE(value)

}

// @formatter:off
class ComponentA { override fun toString(): String  = "A" }

class ComponentB { override fun toString(): String  = "B" }

class ComponentC { override fun toString(): String  = "C" }

class ComponentD { override fun toString(): String  = "D" }

class ComponentE(val value: String) { override fun toString(): String  = "E[value=$value]" }
// @formatter:on

