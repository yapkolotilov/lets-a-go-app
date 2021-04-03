package me.kolotilov.lets_a_go.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Screens
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class MainActivity : AppCompatActivity(), DIAware {

    override val di: DI by closestDI()
    private val navigator = AppNavigator(this, R.id.fragment_container)
    private val router: Router by instance()
    private val navigatorHolder: NavigatorHolder by instance()
    private val repository: Repository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootScreen =
            if (repository.token.isNotEmpty()) Screens.MapScreen else Screens.LoginScreen
        router.newRootScreen(rootScreen)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}