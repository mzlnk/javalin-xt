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
import io.mzlnk.javalin.xt.properties.ApplicationProperties
import jakarta.servlet.Servlet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.function.Consumer

class JavalinXtProxyTest {

    private val javalin: Javalin = mock()
    private val context: JavalinContext = mock()
    private val properties: ApplicationProperties = mock()
    private val proxy = JavalinXtProxy(javalin, context, properties)

    private val updatedJavalin: Javalin = mock()

    @Test
    fun `should delegate to Javalin instance when call proxy method start()`() {
        // given:
        whenever(javalin.start()).thenReturn(updatedJavalin)

        // when:
        val result = proxy.start()

        // then:
        verify(javalin).start()
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method start(int)`() {
        // given:
        val arg0 = 0
        whenever(javalin.start(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.start(arg0)

        // then:
        verify(javalin).start(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method start(String,int)`() {
        // given:
        val arg0 = "host"
        val arg1 = 0
        whenever(javalin.start(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.start(arg0, arg1)

        // then:
        verify(javalin).start(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method exception(Class,ExceptionHandler)`() {
        // given:
        val arg0 = Exception::class.java
        val arg1 = mock<ExceptionHandler<Exception>>()
        whenever(javalin.exception(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.exception(arg0, arg1)

        // then:
        verify(javalin).exception(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method port()`() {
        // given:
        val expectedResult = 0
        whenever(javalin.port()).thenReturn(expectedResult)

        // when:
        val result = proxy.port()

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method stop()`() {
        // given:
        whenever(javalin.stop()).thenReturn(updatedJavalin)

        // when:
        val result = proxy.stop()

        // then:
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method events(Consumer)`() {
        // given:
        val arg0 = mock<Consumer<EventConfig>>()
        whenever(javalin.events(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.events(arg0)

        // then:
        verify(javalin).events(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method javalinServlet()`() {
        // given:
        val expectedResult = mock<Servlet>()
        whenever(javalin.javalinServlet()).thenReturn(expectedResult)

        // when:
        val result = proxy.javalinServlet()

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method jettyServer()`() {
        // given:
        val expectedResult = mock<JettyServer>()
        whenever(javalin.jettyServer()).thenReturn(expectedResult)

        // when:
        val result = proxy.jettyServer()

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method unsafeConfig()`() {
        // given:
        val expectedResult = mock<JavalinConfig>()
        whenever(javalin.unsafeConfig()).thenReturn(expectedResult)

        // when:
        val result = proxy.unsafeConfig()

        // then:
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsException(Class,WsExceptionHandler)`() {
        // given:
        val arg0 = Exception::class.java
        val arg1 = mock<WsExceptionHandler<Exception>>()
        whenever(javalin.wsException(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsException(arg0, arg1)

        // then:
        verify(javalin).wsException(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method addWsHandler(WsHandlerType,String,Consumer,arr(RouteRole))`() {
        // given:
        val arg0 = WsHandlerType.WEBSOCKET
        val arg1 = "path"
        val arg2 = mock<Consumer<WsConfig>>()
        val arg3a = mock<RouteRole>()
        val arg3b = mock<RouteRole>()
        whenever(javalin.addWsHandler(arg0, arg1, arg2, arg3a, arg3b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.addWsHandler(arg0, arg1, arg2, arg3a, arg3b)

        // then:
        verify(javalin).addWsHandler(arg0, arg1, arg2, arg3a, arg3b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method addEndpoint(Endpoint)`() {
        // given:
        val arg0 = mock<Endpoint>()
        whenever(javalin.addEndpoint(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.addEndpoint(arg0)

        // then:
        verify(javalin).addEndpoint(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method get(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.get(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.get(arg0, arg1)

        // then:
        verify(javalin).get(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method get(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.get(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.get(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).get(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method put(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.put(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.put(arg0, arg1)

        // then:
        verify(javalin).put(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method put(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.put(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.put(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).put(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method delete(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.delete(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.delete(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).delete(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method delete(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.delete(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.delete(arg0, arg1)

        // then:
        verify(javalin).delete(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method options(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.options(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.options(arg0, arg1)

        // then:
        verify(javalin).options(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method options(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.options(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.options(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).options(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method before(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.before(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.before(arg0, arg1)

        // then:
        verify(javalin).before(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method before(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.before(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.before(arg0)

        // then:
        verify(javalin).before(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method after(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.after(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.after(arg0)

        // then:
        verify(javalin).after(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method after(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.after(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.after(arg0, arg1)

        // then:
        verify(javalin).after(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method head(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.head(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.head(arg0, arg1)

        // then:
        verify(javalin).head(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method head(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.head(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.head(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).head(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method error(int,Handler)`() {
        // given:
        val arg0 = 0
        val arg1 = mock<Handler>()
        whenever(javalin.error(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.error(arg0, arg1)

        // then:
        verify(javalin).error(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method error(int,String,Handler)`() {
        // given:
        val arg0 = 0
        val arg1 = "application/json"
        val arg2 = mock<Handler>()
        whenever(javalin.error(arg0, arg1, arg2)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.error(arg0, arg1, arg2)

        // then:
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method error(HttpStatus,Handler)`() {
        // given:
        val arg0 = HttpStatus.OK
        val arg1 = mock<Handler>()
        whenever(javalin.error(arg0, arg1)).thenReturn(updatedJavalin)


        // when:
        val result = proxy.error(arg0, arg1)

        // then:
        verify(javalin).error(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method error(HttpStatus,String,Handler)`() {
        // given:
        val arg0 = HttpStatus.OK
        val arg1 = "application/json"
        val arg2 = mock<Handler>()
        whenever(javalin.error(arg0, arg1, arg2)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.error(arg0, arg1, arg2)

        // then:
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method patch(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.patch(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.patch(arg0, arg1)

        // then:
        verify(javalin).patch(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method patch(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.patch(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.patch(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).patch(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method ws(String,Consumer)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<WsConfig>>()
        whenever(javalin.ws(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.ws(arg0, arg1)

        // then:
        verify(javalin).ws(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method ws(String,Consumer,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<WsConfig>>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.ws(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.ws(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).ws(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method post(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.post(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.post(arg0, arg1)

        // then:
        verify(javalin).post(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method post(String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.post(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.post(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).post(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method sse(String,Consumer)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<SseClient>>()
        whenever(javalin.sse(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.sse(arg0, arg1)

        // then:
        verify(javalin).sse(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method sse(String,SseHandler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<SseHandler>()
        whenever(javalin.sse(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.sse(arg0, arg1)

        // then:
        verify(javalin).sse(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method sse(String,Consumer,arr(RouteRole))`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<SseClient>>()
        val arg2a = mock<RouteRole>()
        val arg2b = mock<RouteRole>()
        whenever(javalin.sse(arg0, arg1, arg2a, arg2b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.sse(arg0, arg1, arg2a, arg2b)

        // then:
        verify(javalin).sse(arg0, arg1, arg2a, arg2b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsAfter(Consumer)`() {
        // given:
        val arg0 = mock<Consumer<WsConfig>>()
        whenever(javalin.wsAfter(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsAfter(arg0)

        // then:
        verify(javalin).wsAfter(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsAfter(String,Consumer)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<WsConfig>>()
        whenever(javalin.wsAfter(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsAfter(arg0, arg1)

        // then:
        verify(javalin).wsAfter(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsBefore(String,Consumer)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Consumer<WsConfig>>()
        whenever(javalin.wsBefore(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsBefore(arg0, arg1)

        // then:
        verify(javalin).wsBefore(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsBefore(Consumer)`() {
        // given:
        val arg0 = mock<Consumer<WsConfig>>()
        whenever(javalin.wsBefore(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsBefore(arg0)

        // then:
        verify(javalin).wsBefore(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsBeforeUpgrade(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.wsBeforeUpgrade(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsBeforeUpgrade(arg0, arg1)

        // then:
        verify(javalin).wsBeforeUpgrade(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsBeforeUpgrade(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.wsBeforeUpgrade(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsBeforeUpgrade(arg0)

        // then:
        verify(javalin).wsBeforeUpgrade(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsAfterUpgrade(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.wsAfterUpgrade(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsAfterUpgrade(arg0, arg1)

        // then:
        verify(javalin).wsAfterUpgrade(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method wsAfterUpgrade(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.wsAfterUpgrade(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.wsAfterUpgrade(arg0)

        // then:
        verify(javalin).wsAfterUpgrade(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method beforeMatched(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.beforeMatched(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.beforeMatched(arg0, arg1)

        // then:
        verify(javalin).beforeMatched(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method beforeMatched(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.beforeMatched(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.beforeMatched(arg0)

        // then:
        verify(javalin).beforeMatched(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method afterMatched(Handler)`() {
        // given:
        val arg0 = mock<Handler>()
        whenever(javalin.afterMatched(arg0)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.afterMatched(arg0)

        // then:
        verify(javalin).afterMatched(arg0)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method afterMatched(String,Handler)`() {
        // given:
        val arg0 = "path"
        val arg1 = mock<Handler>()
        whenever(javalin.afterMatched(arg0, arg1)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.afterMatched(arg0, arg1)

        // then:
        verify(javalin).afterMatched(arg0, arg1)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method addHttpHandler(HandlerType,String,Handler)`() {
        // given:
        val arg0 = HandlerType.GET
        val arg1 = "path"
        val arg2 = mock<Handler>()
        whenever(javalin.addHttpHandler(arg0, arg1, arg2)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.addHttpHandler(arg0, arg1, arg2)

        // then:
        verify(javalin).addHttpHandler(arg0, arg1, arg2)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should delegate to Javalin instance when call proxy method addHttpHandler(HandlerType,String,Handler,arr(RouteRole))`() {
        // given:
        val arg0 = HandlerType.GET
        val arg1 = "path"
        val arg2 = mock<Handler>()
        val arg3a = mock<RouteRole>()
        val arg3b = mock<RouteRole>()
        whenever(javalin.addHttpHandler(arg0, arg1, arg2, arg3a, arg3b)).thenReturn(updatedJavalin)

        // when:
        val result = proxy.addHttpHandler(arg0, arg1, arg2, arg3a, arg3b)

        // then:
        verify(javalin).addHttpHandler(arg0, arg1, arg2, arg3a, arg3b)
        assertThat(result as JavalinXtProxy).extracting { it.delegate }.isEqualTo(updatedJavalin)
    }

    @Test
    fun `should consider equality when comparing two proxies backed by the same Javalin instance`() {
        // given:
        val javalin = mock<Javalin>()
        val context1 = mock<JavalinContext>()
        val context2 = mock<JavalinContext>()
        val properties1 = mock<ApplicationProperties>()
        val properties2 = mock<ApplicationProperties>()

        // and:
        val proxy1 = JavalinXtProxy(javalin, context1, properties1)
        val proxy2 = JavalinXtProxy(javalin, context2, properties2)

        // expect:
        assertThat(proxy1 == proxy2).isTrue()
    }

    @Test
    fun `should consider equality when comparing proxy backed by the same Javalin instance with the instance itself`() {
        // given:
        val javalin = mock<Javalin>()
        val context = mock<JavalinContext>()
        val properties = mock<ApplicationProperties>()

        // and:
        val proxy = JavalinXtProxy(javalin, context, properties)

        // expect:
        assertThat(proxy == javalin).isTrue()
    }

    @Test
    fun `should consider no equality when comparing two proxies backed by different Javalin instances`() {
        // given:
        val javalin1 = mock<Javalin>()
        val javalin2 = mock<Javalin>()
        val context1 = mock<JavalinContext>()
        val context2 = mock<JavalinContext>()
        val properties1 = mock<ApplicationProperties>()
        val properties2 = mock<ApplicationProperties>()

        // and:
        val proxy1 = JavalinXtProxy(javalin1, context1, properties1)
        val proxy2 = JavalinXtProxy(javalin2, context2, properties2)

        // expect:
        assertThat(proxy1 == proxy2).isFalse()
    }

    @Test
    fun `should return Javalin instance hash code when call proxy method hashCode()`() {
        // given:
        val javalin = mock<Javalin>()
        val context = mock<JavalinContext>()
        val properties = mock<ApplicationProperties>()
        val proxy = JavalinXtProxy(javalin, context, properties)

        // expect:
        assertThat(proxy.hashCode()).isEqualTo(javalin.hashCode())
    }

    @Test
    fun `should return Javalin instance string representation when call proxy method toString()`() {
        // given:
        val javalin = mock<Javalin> { on(it.toString()) doReturn "javalin" }
        val context = mock<JavalinContext>()
        val properties = mock<ApplicationProperties>()
        val proxy = JavalinXtProxy(javalin, context, properties)

        // expect:
        assertThat(proxy.toString()).isEqualTo(javalin.toString())
    }

}
