package io.mzlnk.javalin.xt.context

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class Conditional {

    /**
     * Mark singletons that should be created only if a certain property is present and has a specific value.
     *
     * @param property name of the property
     * @param havingValue value of the property
     */
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.FUNCTION)
    annotation class OnProperty(val property: String, val havingValue: String)

}