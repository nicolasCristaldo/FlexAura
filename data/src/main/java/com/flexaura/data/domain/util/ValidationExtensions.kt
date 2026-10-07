package com.flexaura.data.domain.util

private object ValidationPatterns {
    val USERNAME_REGEX = "^[a-zA-Z0-9_]{3,15}$".toRegex()
    val PASSWORD_REGEX = "^[^\\n\\r]{6,}$".toRegex()
}

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidLength(min: Int, max: Int): Boolean =
    this.trim().length in min..max

fun String.isValidDisplayName(): Boolean =
    this.isValidLength(1, 25) && !this.contains("\n") && !this.contains("\r")

fun String.isValidUsername(): Boolean =
    this.matches(ValidationPatterns.USERNAME_REGEX)

fun String.isValidPassword(): Boolean =
    this.matches(ValidationPatterns.PASSWORD_REGEX)