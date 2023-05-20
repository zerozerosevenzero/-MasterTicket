package com.example.masterticket.pass

import com.example.masterticket.BaseEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Pass(

    val packageSeq: Integer,
    val userId: String,
    val status: PassStatus,
    val remainingCount: Integer,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    @Id @GeneratedValue
    val id: Long? = null,
) : BaseEntity() {
}