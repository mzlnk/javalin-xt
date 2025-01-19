package io.mzlnk.javalin.xt.properties.internal.management

import io.mzlnk.javalin.xt.properties.ApplicationProperties

/**
 * Factory for creating application properties
 *
 * @property fileResolver resolves application properties files by name. By default, it looks for files in the resources directory.
 * @property environmentVariablesProvider provides access to environment variables. By default, it uses system environment variables.
 */
internal class ApplicationPropertiesFactory(
    private val fileResolver: ApplicationPropertiesFileResolver = ApplicationPropertiesFileResolver { fileName ->
        { }.javaClass.classLoader.getResource(fileName)
    },
    private val environmentVariablesProvider: EnvironmentVariablesProvider = EnvironmentVariablesProvider { key ->
        System.getenv(key)
    }
) {

    /**
     * Creates application properties based on provided configuration
     */
    fun create(config: ApplicationProperties.Configuration = ApplicationProperties.Configuration()): ApplicationProperties {
        val baseProperties = fileResolver.resolve("application.yml")
            ?.let { path ->
                FilePropertySource.create(
                    path = path,
                    resolveEnvironmentVariables = config.resolveEnvironmentVariables,
                    environmentVariablesProvider = environmentVariablesProvider
                )
            }
            ?: EmptyPropertySource

        val profileProperties = config.profile
            ?.let { profile ->
                fileResolver.resolve("application-$profile.yml")
                    ?.let { path ->
                        FilePropertySource.create(
                            path = path,
                            resolveEnvironmentVariables = config.resolveEnvironmentVariables,
                            environmentVariablesProvider = environmentVariablesProvider
                        )
                    }
                    ?: throw IllegalArgumentException("Application properties file for profile `$profile` not found.")
            }
            ?: EmptyPropertySource

        return DefaultApplicationProperties(
            baseProperties = baseProperties,
            profileProperties = profileProperties
        )
    }

}