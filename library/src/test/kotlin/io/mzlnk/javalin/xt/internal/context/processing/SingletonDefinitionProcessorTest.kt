package io.mzlnk.javalin.xt.internal.context.processing

import io.mzlnk.javalin.xt.internal.context.processing.SingletonMethod.Parameter
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
                    type = Type(packageName = "a.b", name = "TestModule1", nullable = false),
                    singletons = emptyList()
                ),
                ModuleClass(
                    type = Type(packageName = "c.d", name = "TestModule2", nullable = false),
                    singletons = emptyList()
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val serviceFile = generatedProject.definitionProviderService
            ?: fail("Definition provider service file not found")

        assertThat(serviceFile.name).isEqualTo("META-INF/services/io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider")
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
    fun `should create singleton definition provider file for singleton with no dependencies`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule", nullable = false),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType",
                            returnType = Type(packageName = "c.d", name = "Type", nullable = false),
                            parameters = emptyList()
                        ),
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile =
            generatedProject.definitionProviders.find { it.name == "TestModuleSingletonDefinitionProvider" }
                ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.xt.context.TypeReference
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          identifier = SingletonDefinition.Identifier(
            |            typeRef = object : TypeReference<c.d.Type>() {}
            |          ),
            |          conditions = emptyList(),
            |          dependencies = emptyList(),
            |          instanceProvider = {
            |            module.providesType()
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

    @Test
    fun `should create singleton definition provider file for singleton with singleton dependencies`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule", nullable = false),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType1",
                            returnType = Type(packageName = "c.d", name = "Type1", nullable = false),
                            parameters = listOf(
                                Parameter(
                                    name = "type2",
                                    type = Type(packageName = "e.f", name = "Type2", nullable = false),
                                    annotations = emptyList()
                                ),
                                Parameter(
                                    name = "type3",
                                    type = Type(packageName = "g.h", name = "Type3", nullable = false),
                                    annotations = emptyList()
                                )
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile =
            generatedProject.definitionProviders.find { it.name == "TestModuleSingletonDefinitionProvider" }
                ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.xt.context.TypeReference
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          identifier = SingletonDefinition.Identifier(
            |            typeRef = object : TypeReference<c.d.Type1>() {}
            |          ),
            |          conditions = emptyList(),
            |          dependencies = listOf(
            |            SingletonDefinition.DependencyIdentifier.Singleton(typeRef = object : TypeReference<e.f.Type2>() {}),
            |            SingletonDefinition.DependencyIdentifier.Singleton(typeRef = object : TypeReference<g.h.Type3>() {}),
            |          ),
            |          instanceProvider = {
            |            module.providesType1(
            |              it[0] as e.f.Type2,
            |              it[1] as g.h.Type3
            |            )
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

    @Test
    fun `should create singleton definition provider file for singleton with singleton dependencies with generic types`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule", nullable = false),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType1",
                            returnType = Type(
                                packageName = "c.d",
                                name = "Type1",
                                nullable = false,
                                typeParameters = listOf(
                                    Type(
                                        packageName = "e.f",
                                        name = "GenericType1",
                                        nullable = false
                                    )
                                )
                            ),
                            parameters = listOf(
                                Parameter(
                                    name = "type2",
                                    type = Type(
                                        packageName = "g.h",
                                        name = "Type2",
                                        nullable = false,
                                        typeParameters = listOf(
                                            Type(
                                                packageName = "e.f",
                                                name = "GenericType2A",
                                                nullable = false,
                                                typeParameters = listOf(
                                                    Type(packageName = "e.f", name = "GenericType2B", nullable = false)
                                                )
                                            ),
                                            Type(packageName = "e.f", name = "GenericType3", nullable = false)
                                        )
                                    ),
                                    annotations = emptyList()
                                )
                            )
                        ),
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile =
            generatedProject.definitionProviders.find { it.name == "TestModuleSingletonDefinitionProvider" }
                ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.xt.context.TypeReference
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          identifier = SingletonDefinition.Identifier(
            |            typeRef = object : TypeReference<c.d.Type1<e.f.GenericType1>>() {}
            |          ),
            |          conditions = emptyList(),
            |          dependencies = listOf(
            |            SingletonDefinition.DependencyIdentifier.Singleton(typeRef = object : TypeReference<g.h.Type2<e.f.GenericType2A<e.f.GenericType2B>, e.f.GenericType3>>() {}),
            |          ),
            |          instanceProvider = {
            |            module.providesType1(
            |              it[0] as g.h.Type2<e.f.GenericType2A<e.f.GenericType2B>, e.f.GenericType3>
            |            )
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

    @Test
    fun `should create singleton definition provider file for singleton with property dependencies`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule", nullable = false),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType1",
                            returnType = Type(packageName = "c.d", name = "Type1", nullable = false),
                            parameters = listOf(
                                Parameter(
                                    name = "propertyA",
                                    type = Type(packageName = "kotlin", name = "String", nullable = false),
                                    annotations = listOf(
                                        Annotation(
                                            type = Type(
                                                packageName = "io.mzlnk.javalin.xt.context",
                                                name = "Property",
                                                nullable = false
                                            ),
                                            parameters = mapOf(
                                                "key" to "propertyA"
                                            )
                                        )
                                    )
                                ),
                                Parameter(
                                    name = "propertyA",
                                    type = Type(packageName = "kotlin", name = "Int", nullable = true),
                                    annotations = listOf(
                                        Annotation(
                                            type = Type(
                                                packageName = "io.mzlnk.javalin.xt.context",
                                                name = "Property",
                                                nullable = false
                                            ),
                                            parameters = mapOf(
                                                "key" to "propertyB"
                                            )
                                        )
                                    )
                                ),
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile =
            generatedProject.definitionProviders.find { it.name == "TestModuleSingletonDefinitionProvider" }
                ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.xt.context.TypeReference
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
            |import io.mzlnk.javalin.xt.properties.Property
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          identifier = SingletonDefinition.Identifier(
            |            typeRef = object : TypeReference<c.d.Type1>() {}
            |          ),
            |          conditions = emptyList(),
            |          dependencies = listOf(
            |            SingletonDefinition.DependencyIdentifier.Property(key = "propertyA", valueProvider = Property::asString, required = true),
            |            SingletonDefinition.DependencyIdentifier.Property(key = "propertyB", valueProvider = Property::asInt, required = false),
            |          ),
            |          instanceProvider = {
            |            module.providesType1(
            |              it[0] as kotlin.String,
            |              it[1] as kotlin.Int?
            |            )
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

    @Test
    fun `should create singleton definition provider file for conditional singleton`() {
        // given:
        val project = Project(
            modules = listOf(
                ModuleClass(
                    type = Type(packageName = "a.b", name = "TestModule", nullable = false),
                    singletons = listOf(
                        SingletonMethod(
                            name = "providesType1",
                            returnType = Type(packageName = "c.d", name = "Type1", nullable = false),
                            parameters = emptyList(),
                            annotations = listOf(
                                Annotation(
                                    type = Type(
                                        packageName = "io.mzlnk.javalin.xt.context",
                                        name = "Conditional.OnProperty",
                                        nullable = false
                                    ),
                                    parameters = mapOf(
                                        "property" to "propertyA",
                                        "havingValue" to "valueA"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        // when:
        val generatedProject = DefaultSingletonDefinitionProcessor.process(project)

        // then:
        val providerFile =
            generatedProject.definitionProviders.find { it.name == "TestModuleSingletonDefinitionProvider" }
                ?: fail("Definition provider file not found")

        assertThat(providerFile.packageName).isEqualTo("a.b")
        assertThat(providerFile.extension).isEqualTo("kt")
        assertThat(providerFile.content).isEqualTo(
            // language=kotlin
            """
            |package a.b
            |
            |import io.mzlnk.javalin.xt.context.TypeReference
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinition
            |import io.mzlnk.javalin.xt.context.definition.SingletonDefinitionProvider
            |import kotlin.collections.List
            |
            |public class TestModuleSingletonDefinitionProvider : SingletonDefinitionProvider {
            |  private val module: TestModule = TestModule()
            |
            |  override val definitions: List<SingletonDefinition<*>> = listOf(
            |        SingletonDefinition(
            |          identifier = SingletonDefinition.Identifier(
            |            typeRef = object : TypeReference<c.d.Type1>() {}
            |          ),
            |          conditions = listOf(
            |            SingletonDefinition.Condition.OnProperty(property = "propertyA", havingValue = "valueA"),
            |          ),
            |          dependencies = emptyList(),
            |          instanceProvider = {
            |            module.providesType1()
            |          }
            |        )
            |      )
            |}
            |
            """.trimMargin()
        )
    }

}