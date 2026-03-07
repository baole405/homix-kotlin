package com.exe202.nova.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val vndFormatter = NumberFormat.getInstance(Locale("vi", "VN")).apply {
    maximumFractionDigits = 0
}

private val displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
private val isoDateFormatter = DateTimeFormatter.ISO_DATE
private val isoDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

fun Double.toVndFormat(): String = "${vndFormatter.format(this)} VND"

fun String.toVndFormat(): String = this.toDoubleOrNull()?.toVndFormat() ?: this

fun String.toDisplayDate(): String = try {
    LocalDate.parse(this, isoDateFormatter).format(displayDateFormatter)
} catch (e: Exception) {
    try {
        LocalDateTime.parse(this, isoDateTimeFormatter).format(displayDateFormatter)
    } catch (e: Exception) {
        this
    }
}

fun String.toDisplayDateTime(): String = try {
    LocalDateTime.parse(this, isoDateTimeFormatter).format(displayDateTimeFormatter)
} catch (e: Exception) {
    this
}

fun String.toRelativeTime(): String = try {
    val dateTime = LocalDateTime.parse(this, isoDateTimeFormatter)
    val now = LocalDateTime.now()
    val minutes = ChronoUnit.MINUTES.between(dateTime, now)
    val hours = ChronoUnit.HOURS.between(dateTime, now)
    val days = ChronoUnit.DAYS.between(dateTime, now)
    when {
        minutes < 1 -> "Vua xong"
        minutes < 60 -> "$minutes phut truoc"
        hours < 24 -> "$hours gio truoc"
        days < 7 -> "$days ngay truoc"
        days < 30 -> "${days / 7} tuan truoc"
        else -> dateTime.format(displayDateFormatter)
    }
} catch (e: Exception) {
    this
}
