package com.example.masterticket.pass

import com.example.masterticket.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Pass(
    val userId: String,
    @Enumerated(EnumType.STRING)
    var status: PassStatus,
    val remainingCount: Int,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime? = null,
    var expiredAt: LocalDateTime? = null,

    @Id @GeneratedValue
    val id: Long? = null,
) : BaseEntity() {
    fun updateExpiringPass() {
        this.status = PassStatus.EXPIRED
        this.expiredAt = LocalDateTime.now()
    }
}