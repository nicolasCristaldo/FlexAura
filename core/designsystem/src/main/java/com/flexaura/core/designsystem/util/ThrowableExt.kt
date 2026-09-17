package com.flexaura.core.designsystem.util

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserReadableMessage(): String = when(this) {
    is UnknownHostException, is ConnectException -> "No internet connection. Please check your connection and try again."
    is SocketTimeoutException -> "Request timed out. Try again."
    is IOException -> "Network error. Please check your connection and try again"
    else -> "An unexpected error occurred. Please try again later."
}