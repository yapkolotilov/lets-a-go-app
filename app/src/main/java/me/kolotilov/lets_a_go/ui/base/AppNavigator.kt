package me.kolotilov.lets_a_go.ui.base

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.kolotilov.lets_a_go.presentation.LetsScreen
import me.kolotilov.lets_a_go.utils.castToOrNull
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

open class AppNavigator(
    activity: FragmentActivity,
    containerId: Int
) : SupportAppNavigator(activity, containerId) {

    private val currentFragment: Fragment? get() = fragmentManager.fragments.lastOrNull()

    override fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment?,
        fragmentTransaction: FragmentTransaction
    ) {
        val animation = when (command) {
            is Replace -> command.screen
            is BackTo -> command.screen
            is Forward -> command.screen
            else -> null
        }?.castToOrNull<LetsScreen>()?.animation
        if (animation != null)
            fragmentTransaction.setCustomAnimations(
                animation.enter,
                animation.exit,
                animation.popEnter,
                animation.popExit
            )
        super.setupFragmentTransaction(command, currentFragment, nextFragment, fragmentTransaction)
    }

    override fun fragmentForward(command: Forward) {
        val screen = command.screen as SupportAppScreen

        val fragmentParams = screen.fragmentParams
        val fragment = if (fragmentParams == null) createFragment(screen) else null

        if (fragment is DialogFragment)
            fragment.show(fragmentManager, screen.screenKey)
        else
            super.fragmentForward(command)
    }

    override fun fragmentReplace(command: Replace) {
        if (currentFragment != null)
            fragmentBack()
        fragmentForward(Forward(command.screen))
    }

    override fun fragmentBack() {
        val lastFragment = currentFragment
        if (lastFragment is BottomSheetDialogFragment)
            lastFragment.dismiss()
        else
            super.fragmentBack()
    }
}