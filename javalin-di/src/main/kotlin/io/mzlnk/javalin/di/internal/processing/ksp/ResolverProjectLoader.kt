package io.mzlnk.javalin.di.internal.processing.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import io.mzlnk.javalin.di.Module
import io.mzlnk.javalin.di.Singleton
import io.mzlnk.javalin.di.internal.processing.*
import io.mzlnk.javalin.di.internal.processing.Project

internal object ResolverProjectLoader {

    fun load(resolver: Resolver): Project {
        val modules = resolver.getSymbolsWithAnnotation(Module::class.java.canonicalName)
            .map { it as KSClassDeclaration }
            .map { it.asModule }
            .toList()

        return Project(modules = modules)
    }

    private val KSClassDeclaration.asModule
        get(): ModuleClass {
            return ModuleClass(
                type = Type(
                    packageName = this.packageName.asString(),
                    name = this.simpleName.asString()
                ),
                singletons = this.getDeclaredFunctions()
                    .filter { it.annotations.any { annotation -> annotation.isTypeOf(Singleton::class.java) } }
                    .map { it.asSingleton }
                    .toList()
            )
        }

    private val KSFunctionDeclaration.asSingleton
        get(): SingletonMethod {
            return SingletonMethod(
                name = this.simpleName.asString(),
                returnType = Type(
                    packageName = this.returnType?.resolve()?.declaration?.packageName?.asString() ?: "",
                    name = this.returnType?.resolve()?.declaration?.simpleName?.asString() ?: ""
                ),
                parameters = this.parameters.map { it.asParameter }.toList(),
            )
        }

    private val KSValueParameter.asParameter
        get(): SingletonMethod.Parameter {
            return SingletonMethod.Parameter(
                name = this.name?.asString() ?: "",
                type = Type(
                    packageName = this.type.resolve().declaration.packageName.asString(),
                    name = this.type.resolve().declaration.simpleName.asString()
                ),
            )
        }

}

private fun KSAnnotation.isTypeOf(type: Class<*>): Boolean {
    return this.annotationType.resolve().declaration.qualifiedName?.asString() == type.canonicalName
}