package com.example.masterticket.booking

import com.example.masterticket.booking.QBooking.booking
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassStatus
import com.example.masterticket.pass.QPass
import com.example.masterticket.pass.QPass.pass
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime


class BookingRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : BookingRepositoryCustom {

    override fun findBookingByStatusAndEndedAt(
        status: BookingStatus,
        endedAt: LocalDateTime,
        pageable: Pageable
    ): Page<Booking> {
        val content = jpaQueryFactory
            .selectFrom(booking)
            .join(booking.pass, pass)
            .where(
                booking.status.eq(BookingStatus.COMPLETED),
                booking.endedAt.loe(endedAt),
                booking.usedPass.eq(false),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total: Long = jpaQueryFactory
            .select(booking.count())
            .from(booking)
            .join(booking.pass, pass)
            .where(
                booking.status.eq(BookingStatus.COMPLETED),
                booking.endedAt.loe(endedAt),
            )
            .fetch()[0]
        return PageImpl(content, pageable, total)
    }
}