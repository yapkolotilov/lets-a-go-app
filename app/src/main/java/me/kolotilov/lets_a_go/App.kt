package me.kolotilov.lets_a_go

import android.app.Application
import me.kolotilov.lets_a_go.di.mainModule
import me.kolotilov.lets_a_go.ui.base.AppRouter
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.provider
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router


class App : Application(), DIAware {

    companion object {
        lateinit var INSTANCE: App
            private set
    }

    private lateinit var cicerone: Cicerone<Router>

    override val di: DI = DI {
        import(mainModule(this@App))

        bind<App>() with provider { this@App }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        cicerone = Cicerone.create()
    }

    fun getNavigatorHolder(): NavigatorHolder {
        return cicerone.navigatorHolder
    }

    fun getRouter(): Router {
        return AppRouter(cicerone.router)
    }
}