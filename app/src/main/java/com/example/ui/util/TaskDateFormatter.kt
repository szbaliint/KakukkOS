package com.example.ui.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utility for parsing and formatting task due dates into human-friendly,
 * unmistakable statuses (e.g., "Today", "Tomorrow", "In 3 days", "Overdue by 2 days").
 */
enum class DueUrgency {
    OVERDUE,
    TODAY,
    TOMORROW,
    THIS_WEEK,
    UPCOMING,
    NO_DATE
}

data class DueDateDisplayInfo(
    val formattedDate: String,      // e.g. "Thu, Oct 15, 2026"
    val relativeLabel: String,      // e.g. "Today", "Tomorrow", "In 3 days", "Overdue by 2 days"
    val urgency: DueUrgency,
    val daysRemaining: Int?
)

object TaskDateFormatter {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val readableFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    private val shortReadableFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    fun parseAndFormat(dateStr: String?): DueDateDisplayInfo {
        if (dateStr.isNullOrBlank()) {
            return DueDateDisplayInfo(
                formattedDate = "No due date",
                relativeLabel = "Anytime",
                urgency = DueUrgency.NO_DATE,
                daysRemaining = null
            )
        }

        val parsedDate: Date? = try {
            isoFormat.parse(dateStr.trim())
        } catch (e: ParseException) {
            null
        }

        if (parsedDate == null) {
            return DueDateDisplayInfo(
                formattedDate = dateStr,
                relativeLabel = dateStr,
                urgency = DueUrgency.UPCOMING,
                daysRemaining = null
            )
        }

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance().apply {
            time = parsedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = targetCal.timeInMillis - todayCal.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        val fullFormatted = readableFormat.format(parsedDate)

        return when {
            diffDays < 0 -> {
                val overdueDays = -diffDays
                DueDateDisplayInfo(
                    formattedDate = fullFormatted,
                    relativeLabel = if (overdueDays == 1) "Overdue by 1 day" else "Overdue by $overdueDays days",
                    urgency = DueUrgency.OVERDUE,
                    daysRemaining = diffDays
                )
            }
            diffDays == 0 -> {
                DueDateDisplayInfo(
                    formattedDate = fullFormatted,
                    relativeLabel = "Due Today",
                    urgency = DueUrgency.TODAY,
                    daysRemaining = 0
                )
            }
            diffDays == 1 -> {
                DueDateDisplayInfo(
                    formattedDate = fullFormatted,
                    relativeLabel = "Due Tomorrow",
                    urgency = DueUrgency.TOMORROW,
                    daysRemaining = 1
                )
            }
            diffDays in 2..6 -> {
                DueDateDisplayInfo(
                    formattedDate = fullFormatted,
                    relativeLabel = "In $diffDays days",
                    urgency = DueUrgency.THIS_WEEK,
                    daysRemaining = diffDays
                )
            }
            else -> {
                DueDateDisplayInfo(
                    formattedDate = fullFormatted,
                    relativeLabel = "In $diffDays days",
                    urgency = DueUrgency.UPCOMING,
                    daysRemaining = diffDays
                )
            }
        }
    }
}
