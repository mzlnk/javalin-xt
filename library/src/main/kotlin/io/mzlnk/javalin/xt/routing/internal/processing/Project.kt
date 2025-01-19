package io.mzlnk.javalin.xt.routing.internal.processing

internal data class Project(
    val endpoints: List<Endpoint>,
) {

    internal data class Endpoint(
        val type: Type,
        val handlers: List<Handler>
    ) {

        internal data class Handler(
            val methodName: String,
            val method: Method,
            val path: String,
            val parameters: List<Parameter>
        ) {

            enum class Method {

                GET, POST, PUT, DELETE, PATCH

            }

            internal sealed interface Parameter {

                data object Context : Parameter

                data class PathVariable(val name: String) : Parameter

                data class Header(val name: String, val required: Boolean) : Parameter

                data class QueryParam(val name: String, val required: Boolean) : Parameter

                sealed interface Body : Parameter {

                    data object AsString : Body

                }

            }

        }

    }

    internal data class Type(
        val packageName: String,
        val name: String
    ) {

        val qualifiedName: String = "$packageName.$name"

    }

}

