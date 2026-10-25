package com.flexaura.core.designsystem.util

import android.content.Context
import com.flexaura.core.designsystem.R
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Converts an timestamp string into a localized relative time span (e.g., "just now", "5 min", "2 h")
 * or a formatted date pattern based on the elapsed duration.
 *
 * @param context The Android context used to access localized string resources and date patterns.
 * @return A localized human-readable relative time string, or the original string if parsing fails.
 */
fun String.toRelativeTimeSpan(context: Context): String {
    if (this.isBlank()) return ""
    return try {
        val postInstant = Instant.parse(this)
        val now = Instant.now()
        val duration = Duration.between(postInstant, now)

        when {
            duration.seconds < 60 -> context.getString(R.string.time_just_now)
            duration.toMinutes() < 60 -> {
                val minutes = duration.toMinutes().toInt()
                context.getString(R.string.time_minutes, minutes)
            }
            duration.toHours() < 24 -> {
                val hours = duration.toHours().toInt()
                context.getString(R.string.time_hours, hours)
            }
            duration.toDays() < 30 -> {
                val days = duration.toDays().toInt()
                context.getString(R.string.time_days, days)
            }
            else -> {
                val pattern = context.getString(R.string.time_date_format)
                val formatter = DateTimeFormatter
                    .ofPattern(pattern, Locale.getDefault())
                    .withZone(ZoneId.systemDefault())
                formatter.format(postInstant)
            }
        }
    } catch(e: Exception) { this }
}