package io.mzlnk.javalin.di.internal.processing.runner.definition

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Named
import io.mzlnk.javalin.di.Singleton
import io.mzlnk.javalin.di.internal.processing.*
import io.mzlnk.javalin.di.internal.processing.Annotation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import kotlin.String

class ModuleDefinitionsLoaderTest {

    @Test
    fun `should load module definition source`() {
        // given:
        /*
         * @Module
         * class TestModule
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf()
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        assertThat(module.source).isEqualTo(clazz)
    }

    @Test
    fun `should load module definition singletons`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         *   @Singleton
         *   fun typeB(): TypeB = TypeB()
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                ),
                Method(
                    name = "typeB",
                    returnType = typeTypeB(),
                    annotations = listOf(annotationSingleton())
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        assertThat(module.singletons).hasSize(2)
    }

    @Test
    fun `should load singleton definition source`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         *}
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" } ?: fail("Module not found")
        val singleton = module.singletons.find { it.source.name == "typeA" } ?: fail("Singleton not found")

        assertThat(singleton.source).isEqualTo(clazz.methods[0]) // fun typeA()
    }

    @Test
    fun `should load singleton definition with no dependencies`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" } ?: fail("Module not found")
        val singleton = module.singletons.find { it.source.name == "typeA" } ?: fail("Singleton not found")

        assertThat(singleton.dependencies).isEmpty()
    }

    @Test
    fun `should load singleton definition with single dependency`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         *   @Singleton
         *   fun typeB(typeA: TypeA): TypeB = TypeB()
         *
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                ),
                Method(
                    name = "typeB",
                    returnType = typeTypeB(),
                    annotations = listOf(annotationSingleton()),
                    parameters = listOf(
                        Method.Parameter(name = "typeA", type = typeTypeA())
                    )
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        val typeBDefinition = module.singletons.find { it.key.type == typeTypeB() }
            ?: fail("TypeB definition not found")

        // and:
        assertThat(typeBDefinition.dependencies)
            .hasSize(1)
            .first()
            .extracting { it.type }
            .isEqualTo(typeTypeA())
    }

    @Test
    fun `should load singleton definition with multiple dependencies`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *     @Singleton
         *     fun typeA(): TypeA = TypeA()
         *
         *     @Singleton
         *     fun typeB(typeA: TypeA): TypeB = TypeB()
         *
         *     @Singleton
         *     fun typeC(typeA: TypeA, typeB: TypeB): TypeC = TypeC()
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                ),
                Method(
                    name = "typeB",
                    returnType = typeTypeB(),
                    annotations = listOf(annotationSingleton()),
                    parameters = listOf(
                        Method.Parameter(name = "typeA", type = typeTypeA())
                    )
                ),
                Method(
                    name = "typeC",
                    returnType = typeTypeC(),
                    annotations = listOf(annotationSingleton()),
                    parameters = listOf(
                        Method.Parameter(name = "typeA", type = typeTypeA()),
                        Method.Parameter(name = "typeB", type = typeTypeB())
                    )
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        val typeCDefinition = module.singletons.find { it.key.type == typeTypeC() }
            ?: fail("TypeC definition not found")

        // and:
        assertThat(typeCDefinition.dependencies)
            .extracting<Type> { it.type }
            .containsExactlyInAnyOrder(typeTypeA(), typeTypeB())
    }

    @Test
    fun `should load named singleton definition`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *    @Singleton
         *    @Named("namedTypeA")
         *    fun typeA(): TypeA = TypeA()
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(
                        annotationSingleton(),
                        annotationNamed("namedTypeA")
                    )
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        assertThat(module.singletons)
            .hasSize(1)
            .first()
            .extracting { it.key }
            .isEqualTo(SingletonDefinition.Key(typeTypeA(), "namedTypeA"))
    }

    @Test
    fun `should load singleton definition with named dependency`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   @Named("namedTypeA")
         *   fun typeA(): TypeA = TypeA()
         *
         *   @Singleton
         *   fun typeB(@Named("namedTypeA") typeA: TypeA): TypeB = TypeB()
         *
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(
                        annotationSingleton(),
                        annotationNamed("namedTypeA")
                    )
                ),
                Method(
                    name = "typeB",
                    returnType = typeTypeB(),
                    annotations = listOf(annotationSingleton()),
                    parameters = listOf(
                        Method.Parameter(
                            name = "typeA",
                            type = typeTypeA(),
                            annotations = listOf(annotationNamed("namedTypeA"))
                        )
                    )
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module notn found")

        val typeBDefinition = module.singletons.find { it.key.type == typeTypeB() }
            ?: fail("TypeB definition not found")

        // and:
        assertThat(typeBDefinition.dependencies)
            .hasSize(1)
            .first()
            .isEqualTo(SingletonDefinition.Key(typeTypeA(), "namedTypeA"))
    }

    @Test
    fun `should load no singleton definitions if class is not annotated with @Module`() {
        // given:
        /*
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        assertThat(definitions).isEmpty()
    }

    @Test
    fun `should load singleton definitions for methods annotated with @Singleton`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   @Singleton
         *   fun typeA(): TypeA = TypeA()
         *
         *   fun typeB(): TypeB = TypeB()
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),
                    annotations = listOf(annotationSingleton())
                ),
                Method(
                    name = "typeB",
                    returnType = typeTypeB()
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        assertThat(module.singletons)
            .hasSize(1)
            .first()
            .extracting { it.key.type }
            // only typeA should be loaded as it is annotated with @Singleton
            .isEqualTo(typeTypeA())
    }

    @Test
    fun `should load module definition with no singletons`() {
        // given:
        /*
         * @Module
         * class TestModule {
         *
         *   fun typeA(): TypeA = TypeA()
         *
         *   fun typeB(): TypeB = TypeB()
         * }
         */
        val clazz = Clazz(
            type = typeTestModule(),
            annotations = listOf(annotationModule()),
            methods = listOf(
                Method(
                    name = "typeA",
                    returnType = typeTypeA(),),
                Method(
                    name = "typeB",
                    returnType = typeTypeB()
                )
            )
        )

        // and:
        val project = Project(classes = listOf(clazz), rootPackageName = "test")

        // when:
        val definitions = ModuleDefinitionsLoader.load(project)

        // then:
        val module = definitions.find { it.source.type.name == "TestModule" }
            ?: fail("Module not found")

        assertThat(module.singletons).isEmpty()
    }

    private companion object {

        private fun typeTestModule() = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TestModule"
        )

        private fun typeTypeA() = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TypeA"
        )

        private fun typeTypeB() = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TypeB"
        )

        private fun typeTypeC() = Type(
            packageName = "io.mzlnk.javalin.di.test",
            name = "TypeC"
        )


        private fun annotationModule() = Annotation(
            type = Type(
                packageName = Module::class.java.packageName,
                name = Module::class.java.simpleName
            )
        )

        private fun annotationSingleton() = Annotation(
            type = Type(
                packageName = Singleton::class.java.packageName,
                name = Singleton::class.java.simpleName
            )
        )

        private fun annotationNamed(value: String) = Annotation(
            type = Type(
                packageName = Named::class.java.packageName,
                name = Named::class.java.simpleName
            ),
            arguments = listOf(
                Annotation.Argument(
                    name = "value",
                    value = value
                )
            )
        )

    }

}