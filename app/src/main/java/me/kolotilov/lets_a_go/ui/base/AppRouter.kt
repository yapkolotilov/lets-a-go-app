package me.kolotilov.lets_a_go.ui.base

import me.kolotilov.lets_a_go.utils.castTo
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

class AppRouter(
    private val router: Router
) : Router() {

    private val listeners: MutableMap<String, (Any) -> Unit> = mutableMapOf()

    fun <T> setResultListener(key: String, listener: (result: T) -> Unit) {
        listeners[key] = { listener(it.castTo()) }
    }

    fun <T> sendResult(key: String, value: T) {
        listeners[key]?.invoke(value as Any)
    }

    //region Router

    override fun finishChain() {
        router.finishChain()
    }

    override fun navigateTo(screen: Screen) {
        router.navigateTo(screen)
    }

    override fun newChain(vararg screens: Screen?) {
        router.newChain(*screens)
    }

    override fun newRootChain(vararg screens: Screen?) {
        router.newRootChain(*screens)
    }

    override fun newRootScreen(screen: Screen) {
        router.newRootScreen(screen)
    }

    override fun replaceScreen(screen: Screen) {
        router.replaceScreen(screen)
    }

    override fun backTo(screen: Screen?) {
        router.backTo(screen)
    }

    override fun exit() {
        router.exit()
    }

    //endregion
}

fun <T> Router.setResultListener(key: String, listener: (result: T) -> Unit) {
    (this as AppRouter).setResultListener(key, listener)
}

fun <T> Router.sendResult(key: String, value: T) {
    (this as AppRouter).sendResult(key, value)
}