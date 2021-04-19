package me.kolotilov.lets_a_go.ui.base

import me.kolotilov.lets_a_go.utils.castTo
import ru.terrakok.cicerone.Router

class AppRouter : Router() {

    private val listeners: MutableMap<String, (Any) -> Unit> = mutableMapOf()

    fun <T> setResultListener(key: String, listener: (result: T) -> Unit) {
        listeners[key] = { listener(it.castTo()) }
    }

    fun <T> sendResult(key: String, value: T) {
        listeners[key]?.invoke(value as Any)
    }
}

fun <T> Router.setResultListener(key: String, listener: (result: T) -> Unit) {
    (this as AppRouter).setResultListener(key, listener)
}

fun <T> Router.sendResult(key: String, value: T) {
    (this as AppRouter).sendResult(key, value)
}