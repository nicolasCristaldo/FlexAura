package com.flexaura.core.designsystem.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toRelativeTimeSpan(): String {
    if (this.isBlank()) return ""
    return try {
        val postInstant = Instant.parse(this)
        val now = Instant.now()
        val duration = Duration.between(postInstant, now)

        when {
            duration.seconds < 60 -> "just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} min"
            duration.toHours() < 24 -> "${duration.toHours()} h"
            duration.toDays() < 30 -> "${duration.toDays()} d"
            else -> {
                val formatter = DateTimeFormatter
                    .ofPattern("MMM dd", Locale.getDefault())
                    .withZone(ZoneId.systemDefault())
                formatter.format(postInstant)
            }
        }
    } catch(e: Exception) { this }
}