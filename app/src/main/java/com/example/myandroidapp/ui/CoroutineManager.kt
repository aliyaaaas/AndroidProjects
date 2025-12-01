package com.example.myandroidapp.ui

import android.content.Context
import com.example.myandroidapp.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.random.Random

class CoroutineManager(
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + mainDispatcher)
) {

    private var activeJobs = mutableListOf<Job>()
    private var lastCancelledSettings: CoroutineSettings? = null
    private var lastCancelledCount = 0

    val uiActions = MutableSharedFlow<CoroutineUiAction>(replay = 0, extraBufferCapacity = 50)

    private companion object {
        const val MIN_DELAY = 1000L
        const val MAX_DELAY = 10000L
        const val THRESHOLD = 7000L
        const val EXCEPTION_CHANCE = 0.3
    }

    fun launchCoroutines(settings: CoroutineSettings) {
        activeJobs.clear()

        scope.launch {
            try {
                if (settings.isSequential) {
                    repeat(settings.count) {
                        val job = scope.launch(
                            context = settings.dispatcher,
                            start = if (settings.isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
                        ) {
                            simulateHeavyTaskAndHandle()
                        }
                        if (settings.isLazy) job.start()
                        activeJobs.add(job)
                        job.join()
                    }
                } else {
                    repeat(settings.count) {
                        val job = scope.launch(
                            context = settings.dispatcher,
                            start = if (settings.isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
                        ) {
                            simulateHeavyTaskAndHandle()
                        }
                        if (settings.isLazy) job.start()
                        activeJobs.add(job)
                    }
                    activeJobs.forEach { it.join() }
                }

                uiActions.emit(
                    CoroutineUiAction.ShowToast(
                        messageResId = if (settings.isSequential)
                            R.string.toast_sequential_completed
                        else
                            R.string.toast_parallel_completed
                    )
                )
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                uiActions.emit(CoroutineUiAction.ShowToast(R.string.error_unknown))
            }
        }
    }

    fun cancelActiveCoroutines() {
        var cancelled = 0
        activeJobs.forEach {
            if (it.isActive) {
                it.cancel()
                cancelled++
            }
        }
        activeJobs.clear()
        uiActions.tryEmit(CoroutineUiAction.ShowCancelledToast(cancelled))
    }

    fun onAppPaused(runsInBackground: Boolean, currentSettings: CoroutineSettings) {
        if (!runsInBackground) {
            var cancelled = 0
            activeJobs.forEach {
                if (it.isActive) {
                    it.cancel()
                    cancelled++
                }
            }
            activeJobs.clear()
            if (cancelled > 0) {
                lastCancelledSettings = currentSettings
                lastCancelledCount = cancelled
                uiActions.tryEmit(CoroutineUiAction.ShowCancelledToast(cancelled))
            }
        }
    }

    fun onAppResumed() {
        val settings = lastCancelledSettings
        val count = lastCancelledCount
        if (settings != null && count > 0) {
            lastCancelledSettings = null
            lastCancelledCount = 0
            uiActions.tryEmit(CoroutineUiAction.ReLaunchInBackground(settings, count))
        }
    }

    private suspend fun simulateHeavyTaskAndHandle() {
        try {
            simulateHeavyTask()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun simulateHeavyTask() {
        val delayTime = Random.nextLong(MIN_DELAY, MAX_DELAY + 1)
        delay(delayTime)

        if (delayTime >= THRESHOLD && Random.nextDouble() < EXCEPTION_CHANCE) {
            val exceptions = listOf(ExceptionA(), ExceptionB(), ExceptionC())
            throw exceptions.random()
        }
    }

    private fun handleException(e: Exception) {
        when (e) {
            is ExceptionA -> uiActions.tryEmit(CoroutineUiAction.ShowToast(R.string.error_message_toast))
            is ExceptionB -> uiActions.tryEmit(CoroutineUiAction.ShowSnackbar(R.string.snackbar_error))
            is ExceptionC -> uiActions.tryEmit(CoroutineUiAction.ResetSettings)
            else -> uiActions.tryEmit(CoroutineUiAction.ShowToast(R.string.error_unknown))
        }
    }

    fun cleanup() {
        scope.cancel()
    }
}

class ExceptionA : Exception()
class ExceptionB : Exception()
class ExceptionC : Exception()