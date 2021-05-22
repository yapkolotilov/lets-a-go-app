package me.kolotilov.lets_a_go.ui.details.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.base.ButtonData
import me.kolotilov.lets_a_go.presentation.base.showDialog
import me.kolotilov.lets_a_go.presentation.details.onboarding.PermissionViewModel
import me.kolotilov.lets_a_go.ui.base.BaseFragment
import org.kodein.di.instance

class PermissionFragment : BaseFragment(R.layout.fragment_permission) {

    private companion object {

        const val FINE_LOCATION_REQUEST_CODE = 1
        const val BACKGROUND_LOCATION_REQUEST_CODE = 2
    }

    override val viewModel: PermissionViewModel by instance()

    private val permissionButton: Button by lazyView(R.id.permission_button)

    override fun bind() {
        permissionButton.setOnClickListener {
            requestLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.all { it.isGranted() }) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        showDialog(
                            title = getString(R.string.background_location_request_title),
                            message = getString(R.string.background_location_request_message),
                            positiveButton = ButtonData(getString(R.string.ok_button)) {
                                requestBackgroundLocation()
                            }
                        )
                    } else {
                        viewModel.go()
                    }
                } else {
                    showDialog(
                        title = getString(R.string.give_permission_title),
                        message = getString(R.string.give_permission_message),
                        positiveButton = ButtonData(getString(R.string.ok_button)) {
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ),
                                FINE_LOCATION_REQUEST_CODE
                            )
                        },
                        cancelable = false
                    )
                }
            }
            BACKGROUND_LOCATION_REQUEST_CODE -> {
                if (grantResults.all { it.isGranted() }) {
                    viewModel.go()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val packageManager = requireContext().packageManager
                        showDialog(
                            title = getString(R.string.give_background_permission_title),
                            message = getString(R.string.give_background_permission_message, packageManager.backgroundPermissionOptionLabel),
                            positiveButton = ButtonData(getString(R.string.ok_button)) {
                                requestBackgroundLocation()
                            },
                            cancelable = false
                        )
                    }
                }
            }
        }
    }

    private fun requestLocation() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            FINE_LOCATION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocation() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            BACKGROUND_LOCATION_REQUEST_CODE
        )
    }

    private fun Int.isGranted(): Boolean {
        return this == PackageManager.PERMISSION_GRANTED
    }
}