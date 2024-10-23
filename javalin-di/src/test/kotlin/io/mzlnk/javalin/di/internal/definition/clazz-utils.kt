package io.mzlnk.javalin.di.internal.definition

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure


internal fun clazz(clazz: kotlin.reflect.KClass<*>): Clazz = Clazz(
    type = type(clazz),
    methods = clazz.members
        .filterIsInstance<kotlin.reflect.KFunction<*>>()
        .map { method(it) },
    annotations = clazz.annotations.map { annotation(it) }
)

internal fun method(method: kotlin.reflect.KFunction<*>): Method = Method(
    name = method.name,
    parameters = method.valueParameters.map { parameter ->
        Method.Parameter(
            type = type(parameter.type.jvmErasure),
            name = parameter.name!!,
            annotations = parameter.annotations.map { annotation(it) }
        )
    },
    returnType = type(method.returnType.jvmErasure),
    annotations = method.annotations.map { annotation(it) }
)

internal fun annotation(annotation: kotlin.Annotation): Annotation = Annotation(
    type = Type(
        packageName = annotation.annotationClass.java.packageName,
        name = annotation.annotationClass.java.simpleName
    ),
    arguments = annotation.annotationClass.memberProperties
        .map { property ->
            Annotation.Argument(
                name = property.name,
                value = property.call(annotation)
            )
        }
)

internal fun type(clazz: kotlin.reflect.KClass<*>): Type = Type(
    packageName = clazz.java.packageName,
    name = clazz.java.simpleName
)


