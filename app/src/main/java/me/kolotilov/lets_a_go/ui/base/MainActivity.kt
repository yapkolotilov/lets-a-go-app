package me.kolotilov.lets_a_go.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.presentation.base.PermissionService
import me.kolotilov.lets_a_go.ui.map.MapFragment
import me.kolotilov.lets_a_go.ui.map.Recording
import me.kolotilov.lets_a_go.ui.map.RecordingData
import me.kolotilov.lets_a_go.ui.map.RecordingParam
import me.kolotilov.lets_a_go.utils.castTo
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class MainActivity : AppCompatActivity(), DIAware {

    companion object {

        private const val DATA = "DATA"

        fun start(context: Context, data: RecordingData) {
            val intent = Intent(context, MainActivity::class.java).apply {
                action = Recording.RECORDING
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(DATA, data.toRecordingParam())
            }
            context.startActivity(intent)
        }
    }

    override val di: DI by closestDI()
    private val permissionService: PermissionService by instance()
    private val navigator = AppNavigator(this, R.id.fragment_container)
    private val router: Router by instance()
    private val navigatorHolder: NavigatorHolder by instance()
    private val repository: Repository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val rootScreen = when {
            repository.token.isNotEmpty() && !permissionService.isLocationEnabled() -> Screens.permission()
            repository.token.isNotEmpty() -> Screens.map(animate = false)
            else -> Screens.login(animate = false)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        val mapFragment = navigator.mapFragment
        mapFragment?.onActivityStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Recording.RECORDING) {
            val data = intent.getSerializableExtra(DATA)!!.castTo<RecordingParam>().toRecordingData()
            MapFragment.start(this, data)
        }
    }
}