package com.example.myandroidapp.mealapp.core.utils.handler

import retrofit2.HttpException

class GeneralExceptionHandlerImpl : GeneralExceptionHandler {

    override fun handleException(ex: Throwable) {
        when (ex) {
            is HttpException -> {
                when (ex.code()) {
                    401 -> {
                        println("TEST TAG - Unauthorized: ${ex.message}")
                    }
                    404 -> {
                        println("TEST TAG - Not Found: ${ex.message}")
                    }
                    else -> {
                        println("TEST TAG - HTTP Error ${ex.code()}: ${ex.message}")
                    }
                }
            }
            else -> {
                println("TEST TAG - Error: ${ex.message}")
            }
        }
    }
}