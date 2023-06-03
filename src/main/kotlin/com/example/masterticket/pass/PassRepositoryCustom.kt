package com.example.masterticket.pass

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface PassRepositoryCustom {
    fun findPassByStatusAndEndedAt(status: PassStatus, endedAt: LocalDateTime, pageable: Pageable): Page<Pass>
}