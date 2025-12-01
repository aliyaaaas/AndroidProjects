package com.example.myandroidapp.ui

import androidx.annotation.StringRes

sealed interface CoroutineUiAction {
    data class ShowToast(
        @StringRes val messageResId: Int,
        val formatArg: Int? = null
    ): CoroutineUiAction

    data class ShowSnackbar(
        @StringRes val messageResId: Int
    ): CoroutineUiAction

    data object ResetSettings : CoroutineUiAction
    data class ShowCancelledToast(val count: Int) : CoroutineUiAction
    data class ReLaunchInBackground(val settings: CoroutineSettings, val count: Int) : CoroutineUiAction
}