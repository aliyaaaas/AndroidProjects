package com.example.myandroidapp.mealapp.core.utils

import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler

inline fun <T, R> T.runCatching(
    generalExceptionHandler: GeneralExceptionHandler,
    block: T.() -> R
): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        generalExceptionHandler.handleException(ex = e)
        Result.failure(e)
    }
}