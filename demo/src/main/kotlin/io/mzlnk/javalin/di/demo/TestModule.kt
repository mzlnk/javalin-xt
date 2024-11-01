package io.mzlnk.javalin.di.demo

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Singleton

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

class ComponentA

class ComponentB

class ComponentC

class ComponentD