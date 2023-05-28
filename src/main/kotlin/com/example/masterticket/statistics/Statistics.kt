package com.example.masterticket.statistics

import com.example.masterticket.booking.Booking
import com.example.masterticket.booking.BookingStatus
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Statistics(
    val statisticsAt: LocalDateTime,
    var allCount: Int,
    var attendedCount: Int,
    var cancelledCount: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        fun create(booking: Booking): Statistics {
            val statistics: Statistics = Statistics(
                statisticsAt = booking.getStatisticsAt(),
                allCount = 1,
                attendedCount = if (booking.attended) 1 else 0,
                cancelledCount = if (BookingStatus.CANCELLED.equals(booking.status)) 1 else 0,
            )

            return statistics
        }
    }

    fun add(bookingEntity: Booking) {
        this.allCount++
        if (bookingEntity.attended) {
            this.attendedCount++
        }
        if (BookingStatus.CANCELLED.equals(bookingEntity.status)) {
            this.cancelledCount++
        }
    }
}