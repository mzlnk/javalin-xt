package io.mzlnk.javalin.di.internal.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import io.mzlnk.javalin.di.internal.processing.*
import io.mzlnk.javalin.di.internal.processing.Annotation
import io.mzlnk.javalin.di.internal.processing.Clazz
import io.mzlnk.javalin.di.internal.processing.Method
import io.mzlnk.javalin.di.internal.processing.Project

internal object ResolverProjectLoader{

    fun load(resolver: Resolver): Project? {
        val mainFunction = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filter { it is KSFunctionDeclaration && it.isMain }
            .map { it as KSFunctionDeclaration }
            .firstOrNull() ?: return null

        val classes = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance(KSClassDeclaration::class.java)
            .map { it.asClazz }
            .toList()

        return Project(
            classes = classes,
            rootPackageName = mainFunction.packageName.asString()
        )
    }

    private val KSFunctionDeclaration.isMain: Boolean get() {
        if (this.simpleName.asString() != "main") {
            return false
        }

        if (this.parentDeclaration != null) {
            return false
        }

        if (this.returnType?.resolve()?.declaration?.qualifiedName?.asString() != "kotlin.Unit") {
            return false
        }

        if (this.parameters.size != 1) {
            return false
        }

        val parameterType = this.parameters[0].type.resolve()
        val parameterTypeDeclaration = parameterType.declaration as? KSClassDeclaration

        if (parameterTypeDeclaration?.qualifiedName?.asString() != "kotlin.Array") {
            return false
        }

        val argumentType = parameterType.arguments.firstOrNull()?.type?.resolve()
        return argumentType?.declaration?.qualifiedName?.asString() == "kotlin.String"
    }

    private val KSClassDeclaration.asClazz get(): Clazz {
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

    private val KSAnnotation.asAnnotation get(): Annotation {
        val annotationType = this.annotationType.resolve().declaration
        return Annotation(
            type = Type(
                packageName = annotationType.packageName.asString(),
                name = annotationType.simpleName.asString(),
            ),
            arguments = this.arguments.map { it.asArgument }.toList()
        )
    }

    private val KSValueArgument.asArgument get(): Annotation.Argument {
        return Annotation.Argument(
            name = this.name?.asString() ?: "",
            value = this.value
        )
    }

    private val KSFunctionDeclaration.asMethod get(): Method {
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

    private val KSValueParameter.asParameter get(): Method.Parameter {
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