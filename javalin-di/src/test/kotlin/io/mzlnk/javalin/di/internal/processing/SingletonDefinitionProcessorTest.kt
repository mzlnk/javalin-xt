package io.mzlnk.javalin.di.internal.processing

import io.mzlnk.javalin.di.internal.processing.SingletonMethod.Parameter
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test

class SingletonDefinitionProcessorTest {

    @Test
    fun `should create definition provider service file`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule1"),
                    singletons = emptyList()
                ),
                ModuleClass(
                    type = Type(packageName = "c.d", name = "TestModule2"),
                    singletons = emptyList()
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val serviceFile = generatedProject.definitionProviderService
            ?: fail("Definition provider service file not found")

        assertThat(serviceFile.name).isEqualTo("META-INF/services/io.mzlnk.javalin.di.definition.SingletonDefinitionProvider")
        assertThat(serviceFile.extension).isEqualTo("")
        assertThat(serviceFile.packageName).isNull()
        assertThat(serviceFile.content).isEqualTo(
            """
            |a.b.TestModule1SingletonDefinitionProvider
            |c.d.TestModule2SingletonDefinitionProvider
            """.trimMargin()
        )
    }

    @Test
    fun `should create singleton definition provider file`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule"),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType1",
                            returnType = Type(packageName = "c.d", name = "Type1"),
                            parameters = emptyList()
                        ),
                        SingletonMethod(
                            name = "providesType2",
                            returnType = Type(packageName = "e.f", name = "Type2"),
                            parameters = listOf(
                                Parameter(name = "type3", type = Type(packageName = "g.h", name = "Type3")),
                                Parameter(name = "type4", type = Type(packageName = "i.j", name = "Type4"))
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile = generatedProject.definitionProviders.find { it.name == "a.b.TestModuleSingletonDefinitionProvider" }
            ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.di.definition.SingletonDefinition
            |import io.mzlnk.javalin.di.definition.SingletonDefinitionProvider
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          type = c.d.Type1::class.java,
            |          dependencies = emptyList(),
            |          instanceProvider = {
            |            module.providesType1()
            |          }
            |        ),
            |        SingletonDefinition(
            |          type = e.f.Type2::class.java,
            |          dependencies = listOf(
            |            g.h.Type3::class.java,
            |            i.j.Type4::class.java,
            |          ),
            |          instanceProvider = {
            |            module.providesType2(
            |              it[0] as g.h.Type3,
            |              it[1] as i.j.Type4
            |            )
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

}