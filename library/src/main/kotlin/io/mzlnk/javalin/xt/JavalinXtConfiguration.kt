package io.mzlnk.javalin.xt

class JavalinXtConfiguration {

    private var _di: Di = Di()
    private var _properties: Properties = Properties()

    val di: Di get() = this._di
    val properties: Properties get() = this._properties

    fun di(init: Di.() -> Unit) {
        this._di = Di().apply(init)
    }

    fun properties(init: Properties.() -> Unit) {
        this._properties = Properties().apply(init)
    }

    class Di {

        var enabled: Boolean = true

    }

    class Properties {

        var enabled: Boolean = true
        var resolveEnvironmentVariables: Boolean = true
        var profile: String? = null

    }

}