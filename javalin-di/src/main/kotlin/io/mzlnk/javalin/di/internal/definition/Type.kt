package io.mzlnk.javalin.di.internal.definition

data class Type(
    val packageName: String,
    val name: String
) {

    val qualifiedName: String = "$packageName.$name"

    override fun toString(): String = qualifiedName

}
