package io.mzlnk.javalin.xt

class JavalinXtConfiguration {

    private var _context: Context = Context()
    private var _properties: Properties = Properties()

    val context: Context get() = this._context
    val properties: Properties get() = this._properties

    fun context(init: Context.() -> Unit) {
        this._context = Context().apply(init)
    }

    fun properties(init: Properties.() -> Unit) {
        this._properties = Properties().apply(init)
    }

    class Context {

        var enabled: Boolean = true

    }

    class Properties {

        var enabled: Boolean = true
        var resolveEnvironmentVariables: Boolean = true
        var profile: String? = null

    }

}