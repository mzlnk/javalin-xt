package io.mzlnk.javalin.xt.demo

import io.mzlnk.javalin.xt.di.Module
import io.mzlnk.javalin.xt.di.Singleton


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

}

// @formatter:off
class ComponentA { override fun toString(): String  = "A" }

class ComponentB { override fun toString(): String  = "B" }

class ComponentC { override fun toString(): String  = "C" }

class ComponentD { override fun toString(): String  = "D" }
// @formatter:on