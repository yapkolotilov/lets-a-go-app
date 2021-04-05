package me.kolotilov.lets_a_go.presentation.base

import me.kolotilov.lets_a_go.presentation.BaseViewModel

abstract class BaseBottomSheetViewModel : BaseViewModel() {

    open fun onDismiss() = Unit
}