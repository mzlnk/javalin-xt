package io.mzlnk.javalin.xt.internal.properties

import java.net.URL

/**
 * Resolves application properties file by name
 */
internal fun interface ApplicationPropertiesFileResolver {

    /**
     * Resolves application properties file by name
     *
     * @param fileName name of the file to resolve
     *
     * @return URL to the resolved file or null if the file was not found
     */
    fun resolve(fileName: String): URL?

}