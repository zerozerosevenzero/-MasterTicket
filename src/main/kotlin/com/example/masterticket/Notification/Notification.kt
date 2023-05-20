package com.example.masterticket.Notification

import com.example.masterticket.BaseEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Notification (
    val uuid: String,
    val event: NotificationEvent,
    val text: String,
    val sent: Boolean,
    val sentAt: LocalDateTime,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) :BaseEntity() {

}