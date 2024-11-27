package io.mzlnk.javalin.xt.properties

interface Property {

    /**
     * Returns the value of this property as a string.
     *
     * @return the value as a string
     * @throws IllegalStateException if the value cannot be represented as a string
     */
    val asString: String

    /**
     * Returns the value of this property as an integer.
     *
     * @return the value as an integer
     * @throws IllegalStateException if the value cannot be represented as an integer
     */
    val asInt: Int

    /**
     * Returns the value of this property as a long.
     *
     * @return the value as a long
     * @throws IllegalStateException if the value cannot be represented as a long
     */
    val asLong: Long

    /**
     * Returns the value of this property as a float.
     *
     * @return the value as a float
     * @throws IllegalStateException if the value cannot be represented as a float
     */
    val asFloat: Float

    /**
     * Returns the value of this property as a double.
     *
     * @return the value as a double
     * @throws IllegalStateException if the value cannot be represented as a double
     */
    val asDouble: Double

    /**
     * Returns the value of this property as a boolean.
     *
     * @return the value as a boolean
     * @throws IllegalStateException if the value cannot be represented as a boolean
     */
    val asBoolean: Boolean

    /**
     * Returns the value of this property as a list of strings.
     *
     * @return the value as a list of strings
     * @throws IllegalStateException if the value cannot be represented as a list of strings
     */
    val asStringList: List<String>

    /**
     * Returns the value of this property as a list of integers.
     *
     * @return the value as a list of integers
     * @throws IllegalStateException if the value cannot be represented as a list of integers
     */
    val asIntList: List<Int>

    /**
     * Returns the value of this property as a list of longs.
     *
     * @return the value as a list of longs
     * @throws IllegalStateException if the value cannot be represented as a list of longs
     */
    val asLongList: List<Long>

    /**
     * Returns the value of this property as a list of floats.
     *
     * @return the value as a list of floats
     * @throws IllegalStateException if the value cannot be represented as a list of floats
     */
    val asFloatList: List<Float>

    /**
     * Returns the value of this property as a list of doubles.
     *
     * @return the value as a list of doubles
     * @throws IllegalStateException if the value cannot be represented as a list of doubles
     */
    val asDoubleList: List<Double>

    /**
     * Returns the value of this property as a list of booleans.
     *
     * @return the value as a list of booleans
     * @throws IllegalStateException if the value cannot be represented as a list of booleans
     */
    val asBooleanList: List<Boolean>

}