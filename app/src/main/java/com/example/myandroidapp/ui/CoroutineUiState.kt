package com.example.myandroidapp.ui

sealed interface CoroutineUiAction {
    data object ShowSequentialToast : CoroutineUiAction
    data object ShowParallelToast : CoroutineUiAction
    data object ShowExceptionAToast : CoroutineUiAction
    data object ShowExceptionBSnackbar : CoroutineUiAction
    data object ShowUnknownErrorToast : CoroutineUiAction
    data object ResetSettings : CoroutineUiAction
    data class ShowCancelledToast(val count: Int) : CoroutineUiAction

    data class ReLaunchInBackground(val settings: CoroutineSettings, val count: Int) : CoroutineUiAction
}