package com.example.masterticket.pass

import com.querydsl.jpa.impl.JPAQuery
import java.time.LocalDateTime

interface PassRepositoryCustom {

    fun findPassByStatusAndEndedAt(status: PassStatus, endedAt: LocalDateTime): MutableList<Pass>

}