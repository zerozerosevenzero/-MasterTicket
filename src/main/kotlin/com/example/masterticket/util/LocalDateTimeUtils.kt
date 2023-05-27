package com.example.masterticket.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class LocalDateTimeUtils {
    companion object {
        val YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun format(localDateTime: LocalDateTime): String {
            return localDateTime.format(YYYY_MM_DD_HH_MM)
        }

        fun format(localDateTime: LocalDateTime, formatter: DateTimeFormatter?): String {
            return localDateTime.format(formatter)
        }

        fun parse(localDateTimeString: String?): LocalDateTime? {
            return if (localDateTimeString.isNullOrBlank()) null else LocalDateTime.parse(
                localDateTimeString,
                YYYY_MM_DD_HH_MM
            )
        }

        fun getWeekOfYear(localDateTime: LocalDateTime): Int {
            return localDateTime[WeekFields.of(Locale.KOREA).weekOfYear()]
        }
    }

}