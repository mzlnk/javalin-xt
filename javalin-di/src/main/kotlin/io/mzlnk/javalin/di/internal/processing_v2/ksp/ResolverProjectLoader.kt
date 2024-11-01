package io.mzlnk.javalin.di.internal.processing_v2.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.internal.processing_v2.*
import io.mzlnk.javalin.di.internal.processing_v2.Annotation
import io.mzlnk.javalin.di.internal.processing_v2.Clazz
import io.mzlnk.javalin.di.internal.processing_v2.Method
import io.mzlnk.javalin.di.internal.processing_v2.Project

internal object ResolverProjectLoader {

    fun load(resolver: Resolver): Project {
        val modules = resolver.getSymbolsWithAnnotation(Module::class.java.canonicalName)
            .map { it as KSClassDeclaration }
            .map { it.asClazz }
            .toList()

        return Project(modules = modules)
    }

    private val KSClassDeclaration.asClazz
        get(): Clazz {
            return Clazz(
                type = Type(
                    packageName = this.packageName.asString(),
                    name = this.simpleName.asString()
                ),
                annotations = this.annotations.map { it.asAnnotation }.toList(),
                methods = this.getDeclaredFunctions()
                    .map { it.asMethod }
                    // filter out constructors
                    .filter { it.name != "<init>" }
                    .toList()
            )
        }

    private val KSAnnotation.asAnnotation
        get(): Annotation {
            val annotationType = this.annotationType.resolve().declaration
            return Annotation(
                type = Type(
                    packageName = annotationType.packageName.asString(),
                    name = annotationType.simpleName.asString(),
                ),
                arguments = this.arguments.map { it.asArgument }.toList()
            )
        }

    private val KSValueArgument.asArgument
        get(): Annotation.Argument {
            return Annotation.Argument(
                name = this.name?.asString() ?: "",
                value = this.value
            )
        }

    private val KSFunctionDeclaration.asMethod
        get(): Method {
            return Method(
                name = this.simpleName.asString(),
                returnType = Type(
                    packageName = this.returnType?.resolve()?.declaration?.packageName?.asString() ?: "",
                    name = this.returnType?.resolve()?.declaration?.simpleName?.asString() ?: ""
                ),
                parameters = this.parameters.map { it.asParameter }.toList(),
                annotations = this.annotations.map { it.asAnnotation }.toList()
            )
        }

    private val KSValueParameter.asParameter
        get(): Method.Parameter {
            return Method.Parameter(
                name = this.name?.asString() ?: "",
                type = Type(
                    packageName = this.type.resolve().declaration.packageName.asString(),
                    name = this.type.resolve().declaration.simpleName.asString()
                ),
                annotations = this.annotations.map { it.asAnnotation }.toList()
            )
        }

}