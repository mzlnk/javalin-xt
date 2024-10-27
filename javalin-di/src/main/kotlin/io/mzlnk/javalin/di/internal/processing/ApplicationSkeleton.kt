package io.mzlnk.javalin.di.internal.processing

internal data class ApplicationSkeleton(
    val generatedFiles: List<File>
) {

    data class File(
        val name: String,
        val extension: String,
        val packageName: String? = null,
        val content: String
    )

}