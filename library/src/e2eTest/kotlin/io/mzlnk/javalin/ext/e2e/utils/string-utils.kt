package io.mzlnk.javalin.ext.e2e.utils


fun String.prependLineNumbers(): String {
    val lines = this.split("\n")
    val maxDigits = lines.size.toString().length

    return lines
        .mapIndexed { index, line -> "${(index + 1).toString().padStart(maxDigits, ' ')} |$line" }
        .joinToString("\n")
}