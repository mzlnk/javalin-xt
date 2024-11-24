package io.mzlnk.javalin.xt.internal

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
import io.mzlnk.javalin.xt.di.context.JavalinContext
import jakarta.servlet.Servlet
import java.util.function.Consumer

/**
 * Proxy class used to wrap Javalin instance and provide access to its context.
 *
 * @param _delegate actual instance of Javalin to be wrapped
 * @param context attached context provided by Javalin DI
 */
internal class JavalinXtProxy(
    javalin: Javalin,
    val context: JavalinContext
) : Javalin(null) {

    private var _delegate: Javalin = javalin

    internal val delegate: Javalin get() = _delegate

    override fun addEndpoint(endpoint: Endpoint): Javalin {
        _delegate = _delegate.addEndpoint(endpoint)
        return this
    }

    override fun addHttpHandler(httpMethod: HandlerType, path: String, handler: Handler): Javalin {
        _delegate = _delegate.addHttpHandler(httpMethod, path, handler)
        return this
    }

    override fun addHttpHandler(
        handlerType: HandlerType,
        path: String,
        handler: Handler,
        vararg roles: RouteRole
    ): Javalin {
        _delegate = _delegate.addHttpHandler(handlerType, path, handler, *roles)
        return this
    }

    override fun addWsHandler(
        handlerType: WsHandlerType,
        path: String,
        wsConfig: Consumer<WsConfig>,
        vararg roles: RouteRole
    ): Javalin {
        _delegate = _delegate.addWsHandler(handlerType, path, wsConfig, *roles)
        return this
    }

    override fun after(handler: Handler): Javalin {
        _delegate = _delegate.after(handler)
        return this
    }

    override fun after(path: String, handler: Handler): Javalin {
        _delegate = _delegate.after(path, handler)
        return this
    }

    override fun afterMatched(handler: Handler): Javalin {
        _delegate = _delegate.afterMatched(handler)
        return this
    }

    override fun afterMatched(path: String, handler: Handler): Javalin {
        _delegate = _delegate.afterMatched(path, handler)
        return this
    }

    override fun before(handler: Handler): Javalin {
        _delegate = _delegate.before(handler)
        return this
    }

    override fun before(path: String, handler: Handler): Javalin {
        _delegate = _delegate.before(path, handler)
        return this
    }

    override fun beforeMatched(handler: Handler): Javalin {
        _delegate = _delegate.beforeMatched(handler)
        return this
    }

    override fun beforeMatched(path: String, handler: Handler): Javalin {
        _delegate = _delegate.beforeMatched(path, handler)
        return this
    }

    override fun delete(path: String, handler: Handler): Javalin {
        _delegate = _delegate.delete(path, handler)
        return this
    }

    override fun delete(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.delete(path, handler, *roles)
        return this
    }

    override fun error(status: Int, contentType: String, handler: Handler): Javalin {
        _delegate = _delegate.error(status, contentType, handler)
        return this
    }

    override fun error(status: HttpStatus, handler: Handler): Javalin {
        _delegate = _delegate.error(status, handler)
        return this
    }

    override fun error(status: HttpStatus, contentType: String, handler: Handler): Javalin {
        _delegate = _delegate.error(status, contentType, handler)
        return this
    }

    override fun error(status: Int, handler: Handler): Javalin {
        _delegate = _delegate.error(status, handler)
        return this
    }

    override fun <E : Exception?> exception(
        exceptionClass: Class<E>,
        exceptionHandler: ExceptionHandler<in E>
    ): Javalin {
        _delegate = _delegate.exception(exceptionClass, exceptionHandler)
        return this
    }

    override fun get(path: String, handler: Handler): Javalin {
        _delegate = _delegate.get(path, handler)
        return this
    }

    override fun get(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.get(path, handler, *roles)
        return this
    }

    override fun head(path: String, handler: Handler): Javalin {
        _delegate = _delegate.head(path, handler)
        return this
    }

    override fun head(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.head(path, handler, *roles)
        return this
    }


    override fun options(path: String, handler: Handler): Javalin {
        _delegate = _delegate.options(path, handler)
        return this
    }

    override fun options(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.options(path, handler, *roles)
        return this
    }


    override fun patch(path: String, handler: Handler): Javalin {
        _delegate = _delegate.patch(path, handler)
        return this
    }

    override fun patch(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.patch(path, handler, *roles)
        return this
    }


    override fun post(path: String, handler: Handler): Javalin {
        _delegate = _delegate.post(path, handler)
        return this
    }

    override fun post(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.post(path, handler, *roles)
        return this
    }

    override fun put(path: String, handler: Handler): Javalin {
        _delegate = _delegate.put(path, handler)
        return this
    }

    override fun put(path: String, handler: Handler, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.put(path, handler, *roles)
        return this
    }


    override fun sse(path: String, handler: SseHandler): Javalin {
        _delegate = _delegate.sse(path, handler)
        return this
    }

    override fun sse(path: String, client: Consumer<SseClient>): Javalin {
        _delegate = _delegate.sse(path, client)
        return this
    }

    override fun sse(path: String, client: Consumer<SseClient>, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.sse(path, client, *roles)
        return this
    }

    override fun ws(path: String, ws: Consumer<WsConfig>): Javalin {
        _delegate = _delegate.ws(path, ws)
        return this
    }

    override fun ws(path: String, ws: Consumer<WsConfig>, vararg roles: RouteRole): Javalin {
        _delegate = _delegate.ws(path, ws, *roles)
        return this
    }

    override fun wsAfter(wsConfig: Consumer<WsConfig>): Javalin {
        _delegate = _delegate.wsAfter(wsConfig)
        return this
    }

    override fun wsAfter(path: String, wsConfig: Consumer<WsConfig>): Javalin {
        _delegate = _delegate.wsAfter(path, wsConfig)
        return this
    }

    override fun wsAfterUpgrade(handler: Handler): Javalin {
        _delegate = _delegate.wsAfterUpgrade(handler)
        return this
    }

    override fun wsAfterUpgrade(path: String, handler: Handler): Javalin {
        _delegate = _delegate.wsAfterUpgrade(path, handler)
        return this
    }

    override fun wsBefore(wsConfig: Consumer<WsConfig>): Javalin {
        _delegate = _delegate.wsBefore(wsConfig)
        return this
    }

    override fun wsBefore(path: String, wsConfig: Consumer<WsConfig>): Javalin {
        _delegate = _delegate.wsBefore(path, wsConfig)
        return this
    }

    override fun wsBeforeUpgrade(handler: Handler): Javalin {
        _delegate = _delegate.wsBeforeUpgrade(handler)
        return this
    }

    override fun wsBeforeUpgrade(path: String, handler: Handler): Javalin {
        _delegate = _delegate.wsBeforeUpgrade(path, handler)
        return this
    }

    override fun <E : Exception?> wsException(
        exceptionClass: Class<E>,
        exceptionHandler: WsExceptionHandler<in E>
    ): Javalin {
        _delegate = _delegate.wsException(exceptionClass, exceptionHandler)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is JavalinXtProxy -> _delegate == other._delegate
            is Javalin -> _delegate == other
            else -> false
        }
    }

    override fun hashCode(): Int = _delegate.hashCode()

    override fun toString(): String = _delegate.toString()

    override fun unsafeConfig(): JavalinConfig = _delegate.unsafeConfig()

    override fun jettyServer(): JettyServer = delegate.jettyServer()

    override fun javalinServlet(): Servlet = _delegate.javalinServlet()

    override fun start(host: String?, port: Int): Javalin {
        _delegate = _delegate.start(host, port)
        return this
    }

    override fun start(port: Int): Javalin {
        _delegate = _delegate.start(port)
        return this
    }

    override fun start(): Javalin {
        _delegate = _delegate.start()
        return this
    }

    override fun stop(): Javalin {
        _delegate = _delegate.stop()
        return this
    }

    override fun events(listener: Consumer<EventConfig>?): Javalin {
        _delegate = _delegate.events(listener)
        return this
    }

    override fun port(): Int = _delegate.port()

}