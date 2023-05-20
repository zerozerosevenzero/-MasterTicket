package com.example.masterticket.statistics

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Statistics (
    val statisticsAt: LocalDateTime,
    val allCount: Integer,
    val attendedCount: Integer,
    val cancelledCount: Integer,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

}