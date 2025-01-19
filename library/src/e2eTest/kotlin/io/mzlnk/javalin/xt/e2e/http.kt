package io.mzlnk.javalin.xt.e2e

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody

object HttpFacade {

    private val client: OkHttpClient = OkHttpClient()

    fun send(request: HttpRequest): HttpResponse {
        client.newCall(
            okhttp3.Request.Builder()
                .url(
                    HttpUrl.Builder()
                        .scheme("http")
                        .host(request.host)
                        .port(request.port)
                        .addPathSegments(request.path)
                        .apply {
                            request.queryParams.forEach { (key, value) -> addQueryParameter(key, value) }
                        }
                        .build()
                )
                .apply {
                    when (request.method) {
                        HttpRequest.Method.GET -> get()
                        HttpRequest.Method.POST -> post((request.body ?: "").toRequestBody())
                        HttpRequest.Method.PUT -> put((request.body ?: "").toRequestBody())
                        HttpRequest.Method.DELETE -> delete()
                        HttpRequest.Method.PATCH -> patch((request.body ?: "").toRequestBody())
                    }
                }
                .apply {
                    request.headers.forEach { (key, value) -> addHeader(key, value) }
                }
                .build()
        ).execute().use { response ->
            return HttpResponse(
                status = response.code,
                body = response.body?.string()
            )
        }
    }

}

data class HttpRequest(
    val host: String,
    val port: Int,
    val method: Method,
    val path: String,
    val headers: List<Pair<String, String>> = emptyList(),
    val queryParams: List<Pair<String, String>> = emptyList(),
    val body: String? = null
) {

    companion object {

        fun builder(): Builder = Builder()

    }

    enum class Method {

        GET, POST, PUT, DELETE, PATCH

    }

    class Builder {

        var host: String? = null
        var port: Int? = null
        var method: Method? = null
        var path: String? = null
        var headers: List<Pair<String, String>> = emptyList()
        var queryParams: List<Pair<String, String>> = emptyList()
        var body: String? = null

        fun build(): HttpRequest {
            return HttpRequest(
                host = host ?: throw IllegalArgumentException("Host must be provided"),
                port = port ?: throw IllegalArgumentException("Port must be provided"),
                method = method ?: throw IllegalArgumentException("Method must be provided"),
                path = path ?: throw IllegalArgumentException("Path must be provided"),
                headers = headers,
                queryParams = queryParams,
                body = body
            )
        }
    }

}

data class HttpResponse(
    val status: Int,
    val body: String?
)