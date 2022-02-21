package com.maricoolsapps.e_commerce.utils

import java.util.*

private const val SECOND = 1
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR
private const val MONTH = 30 * DAY
private const val YEAR = 12 * MONTH

private fun currentDate(): Long {
    val calendar = Calendar.getInstance()
    return calendar.timeInMillis
}

// Long: time in millisecond
fun Long.toTimeAgo(): String {
    val time = this
    val now = currentDate()

    // convert back to second
    val diff = (now - time) / 1000

    return when {
        diff < MINUTE -> "Just now"
        diff < 2 * MINUTE -> "A minute ago"
        diff < 60 * MINUTE -> "${diff / MINUTE} minutes ago"
        diff < 2 * HOUR -> "An hour ago"
        diff < 24 * HOUR -> "${diff / HOUR} hours ago"
        diff < 2 * DAY -> "Yesterday"
        diff < 30 * DAY -> "${diff / DAY} days ago"
        diff < 2 * MONTH -> "A month ago"
        diff < 12 * MONTH -> "${diff / MONTH} months ago"
        diff < 2 * YEAR -> "A year ago"
        else -> "${diff / YEAR} years ago"
    }
}