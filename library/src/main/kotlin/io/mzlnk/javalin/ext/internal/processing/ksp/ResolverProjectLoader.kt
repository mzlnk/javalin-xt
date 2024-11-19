package io.mzlnk.javalin.ext.internal.processing.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import io.mzlnk.javalin.ext.Module
import io.mzlnk.javalin.ext.Singleton
import io.mzlnk.javalin.ext.internal.processing.*
import io.mzlnk.javalin.ext.internal.processing.Project

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
                    name = this.simpleName.asString(),
                    typeParameters = emptyList() // not needed for now
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
                    name = this.returnType?.resolve()?.declaration?.simpleName?.asString() ?: "",
                    typeParameters = this.returnType?.resolve()?.arguments?.map { it.asType } ?: emptyList()
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
                    name = this.type.resolve().declaration.simpleName.asString(),
                    typeParameters = this.type.resolve().arguments.map { it.asType }
                ),
            )
        }

    private val KSTypeArgument.asType
        get(): Type {
            return Type(
                packageName = this.type!!.resolve().declaration.packageName.asString(),
                name = this.type!!.resolve().declaration.simpleName.asString(),
                typeParameters = this.type!!.resolve().arguments.map { it.asType }
            )
        }

}

private fun KSAnnotation.isTypeOf(type: Class<*>): Boolean {
    return this.annotationType.resolve().declaration.qualifiedName?.asString() == type.canonicalName
}