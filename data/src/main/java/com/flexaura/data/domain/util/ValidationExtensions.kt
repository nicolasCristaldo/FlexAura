package com.flexaura.data.domain.util

private object ValidationPatterns {
    val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val USERNAME_REGEX = "^[a-zA-Z0-9_]{3,15}$".toRegex()
    val PASSWORD_REGEX = "^[\\w!@#$%^&*()\\-+=<>?/{}\\[\\]|~]{6,}$".toRegex()
}

fun String.isValidEmail(): Boolean =
    this.matches(ValidationPatterns.EMAIL_REGEX)

fun String.isValidLength(min: Int, max: Int): Boolean =
    this.trim().length in min..max

fun String.isValidUsername(): Boolean =
    this.matches(ValidationPatterns.USERNAME_REGEX)

fun String.isValidPassword(): Boolean =
    this.matches(ValidationPatterns.PASSWORD_REGEX)