package com.example.masterticket.bulkpass

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BulkPassRepository: JpaRepository<BulkPass, Long> {
    fun findByStatusAndStartedAtGreaterThan(status: BulkPassStatus?, startedAt: LocalDateTime?): List<BulkPass>
}