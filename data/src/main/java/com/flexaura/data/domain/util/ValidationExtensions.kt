package com.flexaura.data.domain.util

fun String.isValidEmail(): Boolean {
    if(this.isBlank()) return false
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    return this.matches(emailRegex)
}