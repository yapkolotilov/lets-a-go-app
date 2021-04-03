package me.kolotilov.lets_a_go.ui.base

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Forward

open class AppNavigator(
    activity: FragmentActivity,
    containerId: Int
) : SupportAppNavigator(activity, containerId) {

    override fun fragmentForward(command: Forward) {
        val screen = command.screen as SupportAppScreen

        val fragmentParams = screen.fragmentParams
        val fragment = if (fragmentParams == null) createFragment(screen) else null

        if (fragment is DialogFragment)
            fragment.show(fragmentManager, screen.screenKey)
        else
            super.fragmentForward(command)
    }

    override fun fragmentBack() {
        val lastFragment = fragmentManager.fragments.lastOrNull()
        if (lastFragment is BottomSheetDialogFragment)
            lastFragment.dismiss()
        else
            super.fragmentBack()
    }
}