package io.mzlnk.javalin.xt

class JavalinXtConfiguration {

    private var _di: Di = Di()

    val di: Di get() = this._di

    fun di(init: Di.() -> Unit) {
        this._di = Di().apply(init)
    }

    class Di {

        var enabled: Boolean = true

    }

}