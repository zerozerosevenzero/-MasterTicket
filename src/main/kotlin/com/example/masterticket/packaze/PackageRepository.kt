package com.example.masterticket.packaze

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface PackageRepository : JpaRepository<Packaze, Long> {
    fun findByCreatedAtAfter(dateTime: LocalDateTime, pageable: Pageable): List<Packaze>
}