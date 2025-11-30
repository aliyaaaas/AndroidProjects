package com.example.myandroidapp.ui

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

    fun launchCoroutines(settings: CoroutineSettings) {
        activeJobs.clear()
        val jobs = mutableListOf<Job>()

        repeat(settings.count) {
            val job = scope.launch(
                context = settings.dispatcher,
                start = if (settings.isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
            ) {
                val result = simulateHeavyTask()
                if (result is HeavyTaskResult.Failure) {
                    handleException(result.exception)
                }
            }
            jobs.add(job)
        }

        activeJobs.addAll(jobs)

        scope.launch {
            if (settings.isLazy) {
                jobs.forEach { it.start() }
            }

            if (settings.isSequential) {
                for (job in jobs) job.join()
            } else {
                jobs.forEach { it.join() }
            }

            val action = if (settings.isSequential)
                CoroutineUiAction.ShowSequentialToast
            else
                CoroutineUiAction.ShowParallelToast

            uiActions.emit(action)
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
        scope.launch {
            uiActions.emit(CoroutineUiAction.ShowCancelledToast(cancelled))
        }
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
                scope.launch {
                    uiActions.emit(CoroutineUiAction.ShowCancelledToast(cancelled))
                }
            }
        }
    }

    fun onAppResumed() {
        val settings = lastCancelledSettings
        val count = lastCancelledCount
        if (settings != null && count > 0) {
            lastCancelledSettings = null
            lastCancelledCount = 0
            scope.launch {
                uiActions.emit(CoroutineUiAction.ReLaunchInBackground(settings, count))
            }
        }
    }

    private suspend fun handleException(e: Exception) {
        val action = when (e) {
            is ExceptionA -> CoroutineUiAction.ShowExceptionAToast
            is ExceptionB -> CoroutineUiAction.ShowExceptionBSnackbar
            is ExceptionC -> {
                uiActions.emit(CoroutineUiAction.ResetSettings)
                return
            }
            else -> CoroutineUiAction.ShowUnknownErrorToast
        }
        uiActions.emit(action)
    }

    fun cleanup() {
        scope.cancel()
    }
}


sealed interface HeavyTaskResult {
    data object Success : HeavyTaskResult
    data class Failure(val exception: Exception) : HeavyTaskResult
}

suspend fun simulateHeavyTask(): HeavyTaskResult {
    val delayTime = (1000L..10000L).random()
    delay(delayTime)

    if (delayTime >= 7000L && Random.nextDouble() < 0.3) {
        val exceptions = listOf(ExceptionA(), ExceptionB(), ExceptionC())
        return HeavyTaskResult.Failure(exceptions.random())
    }

    return HeavyTaskResult.Success
}

class ExceptionA : Exception()
class ExceptionB : Exception()
class ExceptionC : Exception()