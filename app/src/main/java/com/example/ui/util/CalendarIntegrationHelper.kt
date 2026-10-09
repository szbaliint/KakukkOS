package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class CalendarEventData(
    val title: String,
    val description: String = "",
    val location: String = "",
    val dateStr: String, // e.g. "2026-10-18" or "2026-11-15"
    val startTimeStr: String = "10:00", // e.g. "10:00"
    val endTimeStr: String = "16:00" // e.g. "16:00"
)

object CalendarIntegrationHelper {

    /**
     * Parses date (yyyy-MM-dd) and time (HH:mm) into start and end epoch milliseconds.
     */
    fun parseEventTimestamps(
        dateStr: String,
        startTimeStr: String = "10:00",
        endTimeStr: String = "16:00"
    ): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        try {
            val dateParts = dateStr.trim().split("-", ".", "/")
            if (dateParts.size >= 3) {
                val year = dateParts[0].toIntOrNull() ?: calendar.get(Calendar.YEAR)
                val month = (dateParts[1].toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)) - 1
                val day = dateParts[2].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (_: Exception) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val startCal = calendar.clone() as Calendar
        val endCal = calendar.clone() as Calendar

        try {
            val startParts = startTimeStr.trim().split(":", ".")
            val startHour = startParts.getOrNull(0)?.toIntOrNull() ?: 10
            val startMinute = startParts.getOrNull(1)?.toIntOrNull() ?: 0
            startCal.set(Calendar.HOUR_OF_DAY, startHour)
            startCal.set(Calendar.MINUTE, startMinute)
            startCal.set(Calendar.SECOND, 0)
            startCal.set(Calendar.MILLISECOND, 0)
        } catch (_: Exception) {
            startCal.set(Calendar.HOUR_OF_DAY, 10)
            startCal.set(Calendar.MINUTE, 0)
        }

        try {
            val endParts = endTimeStr.trim().split(":", ".")
            val endHour = endParts.getOrNull(0)?.toIntOrNull() ?: (startCal.get(Calendar.HOUR_OF_DAY) + 3)
            val endMinute = endParts.getOrNull(1)?.toIntOrNull() ?: 0
            endCal.set(Calendar.HOUR_OF_DAY, endHour)
            endCal.set(Calendar.MINUTE, endMinute)
            endCal.set(Calendar.SECOND, 0)
            endCal.set(Calendar.MILLISECOND, 0)
            if (endCal.before(startCal)) {
                endCal.add(Calendar.HOUR_OF_DAY, 2)
            }
        } catch (_: Exception) {
            endCal.timeInMillis = startCal.timeInMillis + (3 * 3600 * 1000)
        }

        return Pair(startCal.timeInMillis, endCal.timeInMillis)
    }

    /**
     * Formats timestamp into UTC ISO-8601 string for Google Calendar render URL: yyyyMMddTHHmmssZ
     */
    fun formatUtcDateTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }

    /**
     * Builds official Google Calendar TEMPLATE URL (same as Facebook Events and Eventbrite).
     */
    fun buildGoogleCalendarUrl(event: CalendarEventData): String {
        val (startMillis, endMillis) = parseEventTimestamps(event.dateStr, event.startTimeStr, event.endTimeStr)
        val startUtc = formatUtcDateTime(startMillis)
        val endUtc = formatUtcDateTime(endMillis)

        val encodedTitle = URLEncoder.encode(event.title, "UTF-8")
        val encodedDesc = URLEncoder.encode(event.description, "UTF-8")
        val encodedLocation = URLEncoder.encode(event.location, "UTF-8")

        return "https://calendar.google.com/calendar/render?action=TEMPLATE" +
                "&text=$encodedTitle" +
                "&dates=$startUtc/$endUtc" +
                "&details=$encodedDesc" +
                "&location=$encodedLocation"
    }

    /**
     * Directly launches Google Calendar event creation flow.
     * Tries Android Calendar Intent targeting com.google.android.calendar first;
     * falls back to the official Google Calendar render URL in browser/PWA.
     */
    fun addToGoogleCalendar(context: Context, event: CalendarEventData) {
        val (startMillis, endMillis) = parseEventTimestamps(event.dateStr, event.startTimeStr, event.endTimeStr)

        val calendarAppIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            setPackage("com.google.android.calendar")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            if (context.packageManager.resolveActivity(calendarAppIntent, 0) != null) {
                context.startActivity(calendarAppIntent)
                Toast.makeText(context, "Opening Google Calendar", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (_: Exception) {
            // Fallback to web template URL
        }

        // Universal web fallback (works for any Google account)
        try {
            val url = buildGoogleCalendarUrl(event)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            Toast.makeText(context, "Opening Google Calendar", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Google Calendar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Launches Android System Calendar Picker (Google Calendar, Outlook, Samsung Calendar, etc.)
     */
    fun addToDeviceCalendar(context: Context, event: CalendarEventData) {
        val (startMillis, endMillis) = parseEventTimestamps(event.dateStr, event.startTimeStr, event.endTimeStr)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to Google Calendar URL
            addToGoogleCalendar(context, event)
        }
    }

    /**
     * Generates standard RFC 5545 iCalendar content and shares it.
     */
    fun shareIcsEvent(context: Context, event: CalendarEventData) {
        val (startMillis, endMillis) = parseEventTimestamps(event.dateStr, event.startTimeStr, event.endTimeStr)
        val startUtc = formatUtcDateTime(startMillis)
        val endUtc = formatUtcDateTime(endMillis)
        val stampUtc = formatUtcDateTime(System.currentTimeMillis())

        val icsContent = buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//LifeOS//Calendar Integration//EN")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            appendLine("BEGIN:VEVENT")
            appendLine("UID:lifeos-${System.currentTimeMillis()}@lifeos.app")
            appendLine("DTSTAMP:$stampUtc")
            appendLine("DTSTART:$startUtc")
            appendLine("DTEND:$endUtc")
            appendLine("SUMMARY:${event.title}")
            if (event.description.isNotBlank()) {
                appendLine("DESCRIPTION:${event.description.replace("\n", "\\n")}")
            }
            if (event.location.isNotBlank()) {
                appendLine("LOCATION:${event.location}")
            }
            appendLine("STATUS:CONFIRMED")
            appendLine("END:VEVENT")
            appendLine("END:VCALENDAR")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/calendar"
            putExtra(Intent.EXTRA_SUBJECT, event.title)
            putExtra(Intent.EXTRA_TEXT, icsContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Add to Calendar (.ics)").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share calendar file", Toast.LENGTH_SHORT).show()
        }
    }
}
