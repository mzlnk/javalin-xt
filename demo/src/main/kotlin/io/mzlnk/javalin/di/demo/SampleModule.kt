package io.mzlnk.javalin.di.demo

import io.mzlnk.javalin.di.core.Singleton

class SampleModule {

    @Singleton
    fun sampleComponentA(): SampleComponentA = SampleComponentA()

    fun sampleComponentB(): SampleComponentB = SampleComponentB()



}