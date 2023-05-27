package com.example.masterticket.pass

import com.example.masterticket.BaseEntity
import com.example.masterticket.bulkpass.BulkPass
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@NoArgsConstructor
class Pass(
    var packageId: Long? = null,
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

    companion object {
        fun toPassEntity(bulkPass: BulkPass, userId: String): Pass {
            return Pass(
                packageId = bulkPass.packageId,
                userId = userId,
                status = PassStatus.READY,
                remainingCount = bulkPass.count,
                startedAt = bulkPass.startedAt,
                endedAt = bulkPass.endedAt
            )
        }
    }

    fun updateExpiringPass() {
        this.status = PassStatus.EXPIRED
        this.expiredAt = LocalDateTime.now()
    }
}