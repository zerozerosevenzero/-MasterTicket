package com.example.masterticket.statistics

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface StatisticsRepository : JpaRepository<Statistics, Long> {
    @Query(
        value = "SELECT new com.example.masterticket.statistics.AggregatedStatistics(s.statisticsAt, SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount)) " +
                "FROM Statistics s " +
                "WHERE s.statisticsAt BETWEEN :from AND :to " +
                "GROUP BY s.statisticsAt"
    )
    fun findByStatisticsAtBetweenAndGroupBy(@Param("from") from: LocalDateTime, @Param("to") to: LocalDateTime): List<AggregatedStatistics>
}