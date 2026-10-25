package com.flexaura.data.domain.util

/**
 * Object holding regular expression validation patterns for user inputs.
 */
private object ValidationPatterns {
    val USERNAME_REGEX = "^[a-zA-Z0-9_]{3,15}$".toRegex()
    val PASSWORD_REGEX = "^[^\\n\\r]{6,}$".toRegex()
}

/**
 * Validates whether the string conforms to standard email address patterns.
 *
 * @return `true` if the string is a valid email address, `false` otherwise.
 */
fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * Checks whether the trimmed length of the string falls within a specified inclusive range.
 *
 * @param min The minimum allowed length.
 * @param max The maximum allowed length.
 * @return `true` if the length is within the range, `false` otherwise.
 */
fun String.isValidLength(min: Int, max: Int): Boolean =
    this.trim().length in min..max

/**
 * Validates whether the display name meets length requirements and contains no line breaks.
 *
 * @return `true` if the display name is valid, `false` otherwise.
 */
fun String.isValidDisplayName(): Boolean =
    this.isValidLength(1, 25) && !this.contains("\n") && !this.contains("\r")

/**
 * Validates whether the username matches allowed alphanumeric and underscore character rules and length bounds.
 *
 * @return `true` if the username format is valid, `false` otherwise.
 */
fun String.isValidUsername(): Boolean =
    this.matches(ValidationPatterns.USERNAME_REGEX)

/**
 * Validates whether the password meets minimum length requirements without disallowed characters.
 *
 * @return `true` if the password is valid, `false` otherwise.
 */
fun String.isValidPassword(): Boolean =
    this.matches(ValidationPatterns.PASSWORD_REGEX)