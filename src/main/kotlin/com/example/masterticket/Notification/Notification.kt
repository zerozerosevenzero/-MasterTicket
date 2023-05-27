package com.example.masterticket.Notification

import com.example.masterticket.BaseEntity
import com.example.masterticket.booking.Booking
import com.example.masterticket.util.LocalDateTimeUtils
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Notification(
    val uuid: String,
    val event: NotificationEvent,
    val text: String,
    var sent: Boolean = false,
    var sentAt: LocalDateTime? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseEntity() {

    companion object {
        fun toNotificationEntity(booking: Booking, event: NotificationEvent): Notification? {
            if (booking != null) {
                return Notification(
                    uuid = bookingEntityUserEntityUuid(booking),
                    text = text(booking.startedAt),
                    event = event
                )
            }
            return null
        }

        private fun bookingEntityUserEntityUuid(booking: Booking): String {
            if (booking == null) {
                return ""
            }
            return booking.user?.let { it.uuid }?: ""
        }

        private fun text(startedAt: LocalDateTime): String {
            return String.format("안녕하세요. ${LocalDateTimeUtils.format(startedAt)} 수업 시작합니다. 수업 전 출석 체크 부탁드립니다.")
        }
    }

}