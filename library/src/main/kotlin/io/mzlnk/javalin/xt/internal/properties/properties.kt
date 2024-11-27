package io.mzlnk.javalin.xt.internal.properties

import io.mzlnk.javalin.xt.properties.Property

internal abstract class AbstractProperty : Property {

    override val asString: String get() = throw IllegalStateException("Property is not a string")
    override val asInt: Int get() = throw IllegalStateException("Property is not an int")
    override val asLong: Long get() = throw IllegalStateException("Property is not a long")
    override val asFloat: Float get() = throw IllegalStateException("Property is not a float")
    override val asDouble: Double get() = throw IllegalStateException("Property is not a double")
    override val asBoolean: Boolean get() = throw IllegalStateException("Property is not a boolean")

    override val asStringList: List<String> get() = throw IllegalStateException("Property is not a string list")
    override val asIntList: List<Int> get() = throw IllegalStateException("Property is not a number list")
    override val asLongList: List<Long> get() = throw IllegalStateException("Property is not a number list")
    override val asFloatList: List<Float> get() = throw IllegalStateException("Property is not a number list")
    override val asDoubleList: List<Double> get() = throw IllegalStateException("Property is not a number list")
    override val asBooleanList: List<Boolean> get() = throw IllegalStateException("Property is not a boolean list")

}

/**
 * Represents application property that is an object
 *
 * Note: Currently not fully supported
 */
internal data object ObjectProperty : AbstractProperty()

/**
 * Represents application property that is a number.
 *
 * Supports the following number types:
 * - Int
 * - Long
 * - Float
 * - Double
 *
 * Note: When getting Float/Double value as Int/Long/Double - the value is rounded down.
 *
 * @property value the value of the property
 */
internal data class NumberProperty(private val value: Number) : AbstractProperty() {

    override val asInt: Int get() = value.toInt()
    override val asLong: Long get() = value.toLong()
    override val asFloat: Float get() = value.toFloat()
    override val asDouble: Double get() = value.toDouble()
}

/**
 * Represents application property that is a string
 *
 * @property value the value of the property
 */
internal data class StringProperty(private val value: String) : AbstractProperty() {

    override val asString: String get() = value
}

/**
 * Represents application property that is a boolean
 *
 * @property value the value of the property
 */
internal data class BooleanProperty(private val value: Boolean) : AbstractProperty() {

    override val asBoolean: Boolean get() = value
}

/**
 * Represents application property that is a list of numbers
 *
 * Supports the following number types:
 * - Int
 * - Long
 * - Float
 * - Double
 *
 * Note: When getting Float/Double values as Int/Long/Double - the values are rounded down.
 *
 * @property value the value of the property
 */
internal data class NumberListProperty(private val value: List<Number>) : AbstractProperty() {

    override val asIntList: List<Int> get() = value.map { it.toInt() }
    override val asLongList: List<Long> get() = value.map { it.toLong() }
    override val asFloatList: List<Float> get() = value.map { it.toFloat() }
    override val asDoubleList: List<Double> get() = value.map { it.toDouble() }
}

/**
 * Represents application property that is a list of strings
 *
 * @property value the value of the property
 */
internal data class StringListProperty(private val value: List<String>) : AbstractProperty() {

    override val asStringList: List<String> get() = value
}

/**
 * Represents application property that is a list of booleans
 *
 * @property value the value of the property
 */
internal data class BooleanListProperty(private val value: List<Boolean>) : AbstractProperty() {

    override val asBooleanList: List<Boolean> get() = value
}

/**
 * Represents application property that is a list of objects
 *
 * Note: Currently not fully supported
 */
internal data object ObjectListProperty : AbstractProperty()