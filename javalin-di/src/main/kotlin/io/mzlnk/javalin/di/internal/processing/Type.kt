package io.mzlnk.javalin.di.internal.processing

data class Type(
    val packageName: String,
    val name: String
) {

    val qualifiedName: String = "$packageName.$name"

    override fun toString(): String = qualifiedName

}
