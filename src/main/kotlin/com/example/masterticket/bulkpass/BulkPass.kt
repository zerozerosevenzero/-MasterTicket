package com.example.masterticket.bulkpass

import com.example.masterticket.BaseEntity
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class BulkPass(

    val packageId: Long,
    val userGroupId: String,
    @Enumerated(EnumType.STRING)
    var status: BulkPassStatus,
    val count: Int = -1,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseEntity() {

    fun updateStatus(status: BulkPassStatus): BulkPass {
        this.status = status
        return this
    }

}