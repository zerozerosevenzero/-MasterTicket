package com.example.masterticket.pass

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface PassRepository : JpaRepository<Pass, Long> {
    @Transactional
    @Modifying
    @Query(
        value = "UPDATE Pass p " +
                "SET p.remainingCount = :remainingCount," +
                "p.modifiedAt = CURRENT_TIMESTAMP " +
                "WHERE p.id = :id"
    )
    fun updateRemainingCount(id: Long?, remainingCount: Int?): Int
}