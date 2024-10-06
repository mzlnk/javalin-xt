package io.mzlnk.javalin.di.demo

import io.kontx.core.Module
import io.kontx.core.Singleton

@Module
class SampleModule {

    @Singleton
    fun sampleComponentA(): SampleComponentA = SampleComponentA()

    @Singleton
    fun sampleComponentB(): SampleComponentB = SampleComponentB()



}