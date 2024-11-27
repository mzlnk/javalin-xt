package io.mzlnk.javalin.xt.internal.utils.jackson

data class PathReference(val parts: List<Part>) {

    operator fun plus(part: Part): PathReference = PathReference(parts + part)

    override fun toString(): String {
        if(parts.isEmpty()) return "<empty>"

        return parts.drop(1).fold(parts[0].toString()) { acc, part ->
            when (part) {
                is Part.Property -> "$acc.$part"
                is Part.Index -> "$acc$part"
            }
        }
    }

    sealed interface Part {

        data class Property(val name: String) : Part {
            override fun toString(): String = name
        }

        data class Index(val index: Int) : Part {
            override fun toString(): String = "[$index]"
        }

    }

    companion object {

        private val PATH_REFERENCE_REGEX = Regex("([a-zA-Z0-9_]+(\\[[0-9]+])?(\\.[a-zA-Z0-9_]+(\\[[0-9]+])?)*)?")

        fun empty(): PathReference = PathReference(emptyList())

        fun create(path: String): PathReference {
            if (!PATH_REFERENCE_REGEX.matches(path)) {
                throw IllegalArgumentException("Invalid path reference: `$path`")
            }

            val parts = path.split(".")
                .flatMap { part ->
                    if (part.contains("[")) {
                        val name = part.substringBefore("[")
                        val index = part.substringAfter("[").substringBefore("]").toInt()
                        listOf(Part.Property(name), Part.Index(index))
                    } else {
                        listOf(Part.Property(part))
                    }
                }

            return PathReference(parts)
        }

    }
}