package com.example.masterticket.booking

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface BookingRepositoryCustom {
    fun findBookingByStatusAndEndedAt(status: BookingStatus, endedAt: LocalDateTime, pageable: Pageable): Page<Booking>
}