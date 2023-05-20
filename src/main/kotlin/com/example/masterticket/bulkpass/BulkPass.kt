package com.example.masterticket.bulkpass

import com.example.masterticket.BaseEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class BulkPass(

    val packageSeq: Integer,
    val userGroupId: String,
    @Enumerated(EnumType.STRING)
    val status: BulkPassStatus,
    val count: Integer,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,

    @Id @GeneratedValue
    val id: Long? = null,
) : BaseEntity() {
}