package io.mzlnk.javalin.di.internal

import io.javalin.Javalin
import io.javalin.config.EventConfig
import io.javalin.config.JavalinConfig
import io.javalin.http.ExceptionHandler
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import io.javalin.http.HttpStatus
import io.javalin.http.sse.SseClient
import io.javalin.http.sse.SseHandler
import io.javalin.jetty.JettyServer
import io.javalin.router.Endpoint
import io.javalin.security.RouteRole
import io.javalin.websocket.WsConfig
import io.javalin.websocket.WsExceptionHandler
import io.javalin.websocket.WsHandlerType
import io.mzlnk.javalin.di.internal.context.JavalinContext
import jakarta.servlet.Servlet
import java.util.function.Consumer

internal class JavalinProxy(
    private val delegate: Javalin,
    val context: JavalinContext
) : Javalin(null) {

    override fun addEndpoint(endpoint: Endpoint): Javalin {
        delegate.addEndpoint(endpoint)
        return this
    }

    override fun addHttpHandler(httpMethod: HandlerType, path: String, handler: Handler): Javalin {
        delegate.addHttpHandler(httpMethod, path, handler)
        return this
    }

    override fun addHttpHandler(
        handlerType: HandlerType,
        path: String,
        handler: Handler,
        vararg roles: RouteRole
    ): Javalin {
        delegate.addHttpHandler(handlerType, path, handler, *roles)
        return this
    }

    override fun addWsHandler(
        handlerType: WsHandlerType,
        path: String,
        wsConfig: Consumer<WsConfig>,
        vararg roles: RouteRole
    ): Javalin {
        delegate.addWsHandler(handlerType, path, wsConfig, *roles)
        return this
    }

    override fun after(handler: Handler): Javalin {
        delegate.after(handler)
        return this
    }

    override fun after(path: String, handler: Handler): Javalin {
        delegate.after(path, handler)
        return this
    }

    override fun afterMatched(handler: Handler): Javalin {
        delegate.afterMatched(handler)
        return this
    }

    override fun afterMatched(path: String, handler: Handler): Javalin {
        delegate.afterMatched(path, handler)
        return this
    }

    override fun before(handler: Handler): Javalin {
        delegate.before(handler)
        return this
    }

    override fun before(path: String, handler: Handler): Javalin {
        delegate.before(path, handler)
        return this
    }

    override fun beforeMatched(handler: Handler): Javalin {
        delegate.beforeMatched(handler)
        return this
    }

    override fun beforeMatched(path: String, handler: Handler): Javalin {
        delegate.beforeMatched(path, handler)
        return this
    }

    override fun delete(path: String, handler: Handler): Javalin {
        delegate.delete(path, handler)
        return this
    }

    override fun delete(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.delete(path, handler, *roles)
        return this
    }

    override fun error(status: Int, contentType: String, handler: Handler): Javalin {
        delegate.error(status, contentType, handler)
        return this
    }

    override fun error(status: HttpStatus, handler: Handler): Javalin {
        delegate.error(status, handler)
        return this
    }

    override fun error(status: HttpStatus, contentType: String, handler: Handler): Javalin {
        delegate.error(status, contentType, handler)
        return this
    }

    override fun error(status: Int, handler: Handler): Javalin {
        delegate.error(status, handler)
        return this
    }

    override fun <E : Exception?> exception(
        exceptionClass: Class<E>,
        exceptionHandler: ExceptionHandler<in E>
    ): Javalin {
        delegate.exception(exceptionClass, exceptionHandler)
        return this
    }

    override fun get(path: String, handler: Handler): Javalin {
        delegate.get(path, handler)
        return this
    }

    override fun get(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.get(path, handler, *roles)
        return this
    }

    override fun head(path: String, handler: Handler): Javalin {
        delegate.head(path, handler)
        return this
    }

    override fun head(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.head(path, handler, *roles)
        return this
    }


    override fun options(path: String, handler: Handler): Javalin {
        delegate.options(path, handler)
        return this
    }

    override fun options(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.options(path, handler, *roles)
        return this
    }


    override fun patch(path: String, handler: Handler): Javalin {
        delegate.patch(path, handler)
        return this
    }

    override fun patch(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.patch(path, handler, *roles)
        return this
    }


    override fun post(path: String, handler: Handler): Javalin {
        delegate.post(path, handler)
        return this
    }

    override fun post(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.post(path, handler, *roles)
        return this
    }

    override fun put(path: String, handler: Handler): Javalin {
        delegate.put(path, handler)
        return this
    }

    override fun put(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        delegate.put(path, handler, *roles)
        return this
    }


    override fun sse(path: String, handler: SseHandler): Javalin {
        delegate.sse(path, handler)
        return this
    }

    override fun sse(path: String, client: Consumer<SseClient>): Javalin {
        delegate.sse(path, client)
        return this
    }

    override fun sse(path: String, client: Consumer<SseClient>, vararg roles: RouteRole): Javalin {
        delegate.sse(path, client, *roles)
        return this
    }

    override fun ws(path: String, ws: Consumer<WsConfig>): Javalin {
        delegate.ws(path, ws)
        return this
    }

    override fun ws(path: String, ws: Consumer<WsConfig>, vararg roles: RouteRole): Javalin {
        delegate.ws(path, ws, *roles)
        return this
    }

    override fun wsAfter(wsConfig: Consumer<WsConfig>): Javalin {
        delegate.wsAfter(wsConfig)
        return this
    }

    override fun wsAfter(path: String, wsConfig: Consumer<WsConfig>): Javalin {
        delegate.wsAfter(path, wsConfig)
        return this
    }

    override fun wsAfterUpgrade(handler: Handler): Javalin {
        delegate.wsAfterUpgrade(handler)
        return this
    }

    override fun wsAfterUpgrade(path: String, handler: Handler): Javalin {
        delegate.wsAfterUpgrade(path, handler)
        return this
    }

    override fun wsBefore(wsConfig: Consumer<WsConfig>): Javalin {
        delegate.wsBefore(wsConfig)
        return this
    }

    override fun wsBefore(path: String, wsConfig: Consumer<WsConfig>): Javalin {
        delegate.wsBefore(path, wsConfig)
        return this
    }

    override fun wsBeforeUpgrade(handler: Handler): Javalin {
        delegate.wsBeforeUpgrade(handler)
        return this
    }

    override fun wsBeforeUpgrade(path: String, handler: Handler): Javalin {
        delegate.wsBeforeUpgrade(path, handler)
        return this
    }

    override fun <E : Exception?> wsException(
        exceptionClass: Class<E>,
        exceptionHandler: WsExceptionHandler<in E>
    ): Javalin {
        delegate.wsException(exceptionClass, exceptionHandler)
        return this
    }

    override fun equals(other: Any?): Boolean = delegate.equals(other)

    override fun hashCode(): Int = delegate.hashCode()

    override fun toString(): String = delegate.toString()

    override fun unsafeConfig(): JavalinConfig = delegate.unsafeConfig()

    override fun jettyServer(): JettyServer = delegate.jettyServer()

    override fun javalinServlet(): Servlet = delegate.javalinServlet()

    override fun start(host: String?, port: Int): Javalin {
        delegate.start(host, port)
        return this
    }

    override fun start(port: Int): Javalin {
        delegate.start(port)
        return this
    }

    override fun start(): Javalin {
        delegate.start()
        return this
    }

    override fun stop(): Javalin {
        delegate.stop()
        return this
    }

    override fun events(listener: Consumer<EventConfig>?): Javalin {
        delegate.events(listener)
        return this
    }

    override fun port(): Int = delegate.port()

}