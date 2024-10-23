package io.mzlnk.javalin.di.internal.definition

import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Named
import io.mzlnk.javalin.di.Singleton
import io.mzlnk.javalin.di.internal.utils.TypeA
import io.mzlnk.javalin.di.internal.utils.TypeB
import io.mzlnk.javalin.di.internal.utils.TypeC
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredFunctions

class SingletonDefinitionsLoaderTest {

    @Test
    fun `should load singleton definition source`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        assertThat(definitions)
            .hasSize(1)
            .first()
            .extracting { it.source }
            .isEqualTo(
                SingletonDefinition.Source(
                clazz = clazz(TestModule::class),
                method = method(TestModule::class.declaredFunctions.find { it.name == "typeA" }!!)
            ))
    }

    @Test
    fun `should load singleton definition with no dependencies`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        assertThat(definitions)
            .hasSize(1)
            .first()
            .extracting { it.dependencies }
            .isEqualTo(emptyList<SingletonDefinition.Key>())
    }

    @Test
    fun `should load singleton definition with single dependency`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

            @Singleton
            fun typeB(typeA: TypeA): TypeB = TypeB()
        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        val typeBDefinition = definitions.find { it.key.type == type(TypeB::class) }
            ?: fail("TypeB definition not found")

        // and:
        assertThat(typeBDefinition.dependencies)
            .hasSize(1)
            .first()
            .extracting { it.type }
            .isEqualTo(type(TypeA::class))
    }

    @Test
    fun `should load singleton definition with multiple dependencies`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

            @Singleton
            fun typeB(typeA: TypeA): TypeB = TypeB()

            @Singleton
            fun typeC(typeA: TypeA, typeB: TypeB): TypeC = TypeC()
        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        val typeCDefinition = definitions.find { it.key.type == type(TypeC::class) }
            ?: fail("TypeC definition not found")

        // and:
        assertThat(typeCDefinition.dependencies)
            .extracting<Type> { it.type }
            .containsExactlyInAnyOrder(type(TypeA::class), type(TypeB::class))
    }

    @Test
    fun `should load named singleton definition`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            @Named("namedTypeA")
            fun typeA(): TypeA = TypeA()
        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        assertThat(definitions)
            .hasSize(1)
            .first()
            .extracting { it.key }
            .isEqualTo(SingletonDefinition.Key(type(TypeA::class), "namedTypeA"))
    }

    @Test
    fun `should load singleton definition with named dependency`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            @Named("namedTypeA")
            fun typeA(): TypeA = TypeA()

            @Singleton
            fun typeB(@Named("namedTypeA") typeA: TypeA): TypeB = TypeB()
        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        val typeBDefinition = definitions.find { it.key.type == type(TypeB::class) }
            ?: fail("TypeB definition not found")

        // and:
        assertThat(typeBDefinition.dependencies)
            .hasSize(1)
            .first()
            .isEqualTo(SingletonDefinition.Key(type(TypeA::class), "namedTypeA"))
    }

    @Test
    fun `should load no singleton definitions if class is not annotated with @Module`() {
        // given:
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        assertThat(definitions).isEmpty()
    }

    @Test
    fun `should load singleton definitions for methods annotated with @Singleton`() {
        // given:
        @Module
        class TestModule {

            @Singleton
            fun typeA(): TypeA = TypeA()

            fun typeB(): TypeB = TypeB()
        }

        // and:
        val loader = SingletonDefinitionsLoader(
            clazzSource = StaticClassSource.of(
                clazz(TestModule::class)
            )
        )

        // when:
        val definitions = loader.load()

        // then:
        assertThat(definitions)
            .hasSize(1)
            .first()
            .extracting { it.key.type }
            // only typeA should be loaded as it is annotated with @Singleton
            .isEqualTo(type(TypeA::class))
    }

}