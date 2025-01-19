package io.mzlnk.javalin.xt.context.internal.processing.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import io.mzlnk.javalin.xt.context.Conditional
import io.mzlnk.javalin.xt.context.Named
import io.mzlnk.javalin.xt.context.internal.processing.Module
import io.mzlnk.javalin.xt.context.internal.processing.Project
import io.mzlnk.javalin.xt.context.internal.processing.Singleton
import io.mzlnk.javalin.xt.context.internal.processing.Type

/**
 * Factory component that uses KSP API to load the project based on the source code.
 */
internal object ResolverProjectLoader {

    /**
     * Loads the project using information from the KSP resolver.
     *
     * @param resolver resolver to load the project from
     *
     * @return loaded project
     */
    fun load(resolver: Resolver): Project {
        val modules = resolver.getSymbolsWithAnnotation(io.mzlnk.javalin.xt.context.Module::class.java.canonicalName)
            .map { it as KSClassDeclaration }
            .map { it.asModule }
            .toList()

        return Project(modules = modules)
    }

    private val KSClassDeclaration.asModule
        get(): Module = Module(
            type = Type(
                packageName = packageName.asString(),
                name = simpleName.asString(),
                nullable = false,
                typeParameters = emptyList() // not needed for now
            ),
            singletons = getDeclaredFunctions()
                .filter { it.annotations.any { annotation -> annotation.isTypeOf(io.mzlnk.javalin.xt.context.Singleton::class.java) } }
                .map { it.asSingleton }
                .toList()
        )

    private val KSFunctionDeclaration.asSingleton
        get(): Singleton = Singleton(
            name = annotations
                .firstOrNull { it.isTypeOf(Named::class.java) }
                ?.arguments?.first()?.value as? String,
            methodName = simpleName.asString(),
            type = Type(
                packageName = returnType?.resolve()?.declaration?.packageName?.asString() ?: "",
                name = returnType?.resolve()?.declaration?.simpleName?.asString() ?: "",
                nullable = returnType?.resolve()?.isMarkedNullable ?: false,
                typeParameters = returnType?.resolve()?.arguments?.map { it.asType } ?: emptyList()
            ),
            conditionals = annotations
                .filter {
                    it.annotationType.resolve().declaration.qualifiedName?.asString()
                        ?.startsWith(Conditional::class.java.canonicalName) == true
                }
                .map { it.asConditional }
                .toList(),
            dependencies = parameters.map { it.asDependency }.toList(),
        )

    private val KSAnnotation.asConditional
        get(): Singleton.Conditional {
            return when {
                this.isTypeOf(io.mzlnk.javalin.xt.context.Conditional.OnProperty::class.java) -> {
                    Singleton.Conditional.OnProperty(
                        key = this.arguments[0].value as String,
                        havingValue = this.arguments[1].value as String
                    )
                }

                else -> throw IllegalArgumentException("Unsupported conditional annotation: ${this.annotationType.resolve().declaration.qualifiedName?.asString()}")
            }
        }

    private val KSValueParameter.asDependency
        get(): Singleton.Dependency {
            val isPropertyAnnotated =
                this.annotations.any { it.isTypeOf(io.mzlnk.javalin.xt.context.Property::class.java) }

            return if (isPropertyAnnotated) {
                Singleton.Dependency.Property(
                    type = Type(
                        packageName = this.type.resolve().declaration.packageName.asString(),
                        name = this.type.resolve().declaration.simpleName.asString(),
                        nullable = this.type.resolve().isMarkedNullable,
                        typeParameters = this.type.resolve().arguments.map { it.asType }
                    ),
                    key = this.annotations.first { it.isTypeOf(io.mzlnk.javalin.xt.context.Property::class.java) }.arguments.first().value as String,
                    required = !this.type.resolve().isMarkedNullable,
                )
            } else {
                val type = Type(
                    packageName = this.type.resolve().declaration.packageName.asString(),
                    name = this.type.resolve().declaration.simpleName.asString(),
                    nullable = this.type.resolve().isMarkedNullable,
                    typeParameters = this.type.resolve().arguments.map { it.asType }
                )

                val name: String? = this.annotations
                    .firstOrNull { it.isTypeOf(io.mzlnk.javalin.xt.context.Named::class.java) }
                    ?.arguments?.first()
                    ?.value as? String

                if (KOTLIN_LIST_TYPE_REGEX.matches(type.qualifiedName)) {
                    Singleton.Dependency.Singleton.List(
                        type = type,
                        name = name,
                        elementName = this.type.resolve()
                            .arguments.getOrNull(0)
                            ?.annotations?.toList()
                            ?.firstOrNull { it.isTypeOf(io.mzlnk.javalin.xt.context.Named::class.java) }
                            ?.arguments?.first()
                            ?.value as? String
                    )
                } else {
                    Singleton.Dependency.Singleton.Singular(
                        type = type,
                        name = name
                    )
                }
            }

        }


    private val KSTypeArgument.asType
        get(): Type {
            return Type(
                packageName = this.type!!.resolve().declaration.packageName.asString(),
                name = this.type!!.resolve().declaration.simpleName.asString(),
                nullable = this.type!!.resolve().isMarkedNullable,
                typeParameters = this.type!!.resolve().arguments.map { it.asType }
            )
        }

}

private val KOTLIN_LIST_TYPE_REGEX = Regex("kotlin.collections.List<.*>")

private fun KSAnnotation.isTypeOf(type: Class<*>): Boolean {
    return this.annotationType.resolve().declaration.qualifiedName?.asString() == type.canonicalName
}