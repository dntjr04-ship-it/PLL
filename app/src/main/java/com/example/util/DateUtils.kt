package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  fun todayKey(now: Date = Date()): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
  }

  fun formatKoreanDisplayDate(dateKey: String): String {
    val parsed =
      runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey) }.getOrNull()
        ?: Date()
    return SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREAN).format(parsed)
  }

  fun millisUntilNextMidnight(nowMillis: Long = System.currentTimeMillis()): Long {
    val nextMidnight =
      Calendar.getInstance().apply {
        timeInMillis = nowMillis
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 50)
      }
    return (nextMidnight.timeInMillis - nowMillis).coerceAtLeast(1_000L)
  }
}
