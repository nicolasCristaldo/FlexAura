package com.flexaura.core.designsystem.util

import android.content.Context
import com.flexaura.core.designsystem.R
import kotlin.math.abs

/**
 * Formats an integer into a localized compact representation (using 'k' for thousands or 'M' for millions)
 * with support for negative signs.
 *
 * @param context The Android context used to access localized compact formatting strings.
 * @return A compact, localized string representation of the number.
 */
fun Int.toCompactString(context: Context): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""

    return when {
        absValue < 1000 -> this.toString()
        absValue < 1_000_000 -> {
            val value = absValue / 1000
            context.getString(R.string.compact_k, sign, value)
        }
        else -> {
            val value = absValue / 1_000_000.0
            context.getString(R.string.compact_m, sign, value)
        }
    }
}