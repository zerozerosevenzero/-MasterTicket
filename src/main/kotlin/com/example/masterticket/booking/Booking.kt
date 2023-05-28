package com.example.masterticket.booking

import com.example.masterticket.BaseEntity
import com.example.masterticket.pass.Pass
import com.example.masterticket.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Booking(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_id", insertable = false, updatable = false)
    val pass: Pass,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    val user: User,
    val status: BookingStatus,
    var usedPass: Boolean = false,
    val attended: Boolean,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,
    val canceledAt: LocalDateTime,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    fun updateUsedPass() {
        this.usedPass = true
        this.pass.remainingCount -= 1
    }
}