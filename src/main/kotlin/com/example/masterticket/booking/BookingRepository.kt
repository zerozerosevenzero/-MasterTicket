package com.example.masterticket.booking

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface BookingRepository : JpaRepository<Booking, Long>, BookingRepositoryCustom {
    @Transactional
    @Modifying
    @Query(
        value = "UPDATE Booking b " +
                "SET b.usedPass = :usedPass, " +
                "b.modifiedAt = CURRENT_TIMESTAMP " +
                "WHERE b.id = :id"
    )
    fun updateUsedPass(id: Long?, usedPass: Boolean): Int
}