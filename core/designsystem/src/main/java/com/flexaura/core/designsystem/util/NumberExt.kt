package com.flexaura.core.designsystem.util

import kotlin.math.abs

fun Int.toCompactString(): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""

    return when {
        absValue < 1000 -> "$this"
        absValue < 1_000_000 -> "$sign${absValue / 1000}k"
        else -> "$sign${absValue / 1_000_000.0}M"
    }
}