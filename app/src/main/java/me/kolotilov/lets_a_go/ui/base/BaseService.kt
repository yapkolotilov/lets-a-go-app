package me.kolotilov.lets_a_go.ui.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

abstract class BaseService : Service(), DIAware {

    abstract val viewModel: BaseViewModel
    override val di by closestDI()

    final override fun onCreate() {
        viewModel.attach()
    }

    @CallSuper
    override fun onDestroy() {
        viewModel.detach()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    abstract override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
}