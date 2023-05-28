package com.example.masterticket.job.statistics

import com.example.masterticket.statistics.StatisticsRepository
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
@RequiredArgsConstructor
class MakeStatisticsJobConfig(
    val  jobBuilderFactory: JobBuilderFactory,
    val  stepBuilderFactory: StepBuilderFactory,
    val  entityManagerFactory: EntityManagerFactory,
    val  statisticsRepository: StatisticsRepository,
    val  makeDailyStatisticsTasklet: MakeDailyStatisticsTasklet,
    val  makeWeeklyStatisticsTasklet: MakeWeeklyStatisticsTasklet,
) {

    private val logger: Logger = LoggerFactory.getLogger(MakeStatisticsJobConfig::class.java)
    private val CHUNK_SIZE = 10
}