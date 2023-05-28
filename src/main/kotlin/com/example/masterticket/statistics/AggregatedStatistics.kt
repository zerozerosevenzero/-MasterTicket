package com.example.masterticket.statistics

import lombok.AllArgsConstructor
import lombok.ToString
import java.time.LocalDateTime

class AggregatedStatistics(
    val statisticsAt: LocalDateTime,
    var allCount: Long = 0,
    var attendedCount: Long = 0,
    var cancelledCount: Long = 0
) {
    fun merge(statistics: AggregatedStatistics) {
        this.allCount += statistics.allCount
        this.attendedCount += statistics.attendedCount
        this.cancelledCount += statistics.cancelledCount
    }
}
