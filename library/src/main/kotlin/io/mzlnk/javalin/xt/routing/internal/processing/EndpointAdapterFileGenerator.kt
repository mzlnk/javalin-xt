package io.mzlnk.javalin.xt.routing.internal.processing

import io.mzlnk.javalin.xt.routing.internal.processing.GeneratedProject.GeneratedFile
import io.mzlnk.javalin.xt.routing.internal.processing.Project.Endpoint

internal interface EndpointAdapterFileGenerator {

    fun generate(endpoint: Endpoint): GeneratedFile

}