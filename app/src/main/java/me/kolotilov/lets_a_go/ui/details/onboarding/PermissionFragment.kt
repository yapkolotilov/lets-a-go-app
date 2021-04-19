package me.kolotilov.lets_a_go.ui.details.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.details.onboarding.PermissionViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class PermissionFragment : BaseFragment(R.layout.fragment_permission) {

    override val viewModel: PermissionViewModel by instance()

    private val permissionButton: Button by lazyView(R.id.permission_button)

    override fun bind() {
        permissionButton.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                viewModel.go()
            } else
                showDialog(
                    title = getString(R.string.give_permission_title),
                    message = getString(R.string.give_permission_message),
                    positiveButton = ButtonData(getString(R.string.ok_button))
                )
        }
    }
}