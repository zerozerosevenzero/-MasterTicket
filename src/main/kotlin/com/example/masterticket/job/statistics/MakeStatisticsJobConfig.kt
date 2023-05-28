package com.example.masterticket.job.statistics

import com.example.masterticket.booking.Booking
import com.example.masterticket.statistics.Statistics
import com.example.masterticket.statistics.StatisticsRepository
import com.example.masterticket.util.LocalDateTimeUtils
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.time.LocalDateTime
import java.util.Map
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

    @Bean
    fun makeStatisticsJob(): Job {
        val addStatisticsFlow: Flow = FlowBuilder<Flow>("addStatisticsFlow")
            .start(addStatisticsStep())
            .build()
        val makeDailyStatisticsFlow: Flow = FlowBuilder<Flow>("makeDailyStatisticsFlow")
            .start(makeDailyStatisticsStep())
            .build()
        val makeWeeklyStatisticsFlow: Flow = FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
            .start(makeWeeklyStatisticsStep())
            .build()
        val parallelMakeStatisticsFlow = FlowBuilder<Flow>("parallelMakeStatisticsFlow")
            .split(SimpleAsyncTaskExecutor())
            .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
            .build()
        return jobBuilderFactory["makeStatisticsJob"]
            .start(addStatisticsFlow)
            .next(parallelMakeStatisticsFlow)
            .build()
            .build()
    }

    @Bean
    fun addStatisticsStep(): Step {
        return stepBuilderFactory["addStatisticsStep"]
            .chunk<Booking, Booking>(CHUNK_SIZE)
            .reader(addStatisticsItemReader(null, null))
            .writer(addStatisticsItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun addStatisticsItemReader(
        @Value("#{jobParameters[from]}") fromString: String?,
        @Value("#{jobParameters[to]}") toString: String?
    ): JpaCursorItemReader<Booking> {
        val from: LocalDateTime = LocalDateTimeUtils.parse(fromString)
        val to: LocalDateTime = LocalDateTimeUtils.parse(toString)
        return JpaCursorItemReaderBuilder<Booking>()
            .name("usePassesItemReader")
            .entityManagerFactory(entityManagerFactory) // JobParameter를 받아 종료 일시(endedAt) 기준으로 통계 대상 예약(Booking)을 조회합니다.
            .queryString("select b from Booking b where b.endedAt between :from and :to")
            .parameterValues(Map.of<String, Any>("from", from, "to", to))
            .build()
    }

    @Bean
    fun addStatisticsItemWriter(): ItemWriter<Booking> {
        return ItemWriter<Booking> { bookings: List<Booking> ->
            val statisticsMap: MutableMap<LocalDateTime, Statistics> = LinkedHashMap()
            for (booking in bookings) {
                val statisticsAt: LocalDateTime = booking.getStatisticsAt()
                val statistics: Statistics? = statisticsMap[statisticsAt]
                if (statistics == null) {
                    statisticsMap[statisticsAt] = Statistics.create(booking)
                } else {
                    statistics.add(booking)
                }
            }
            val statistics: List<Statistics> = ArrayList(statisticsMap.values)
            statisticsRepository.saveAll(statistics)
            logger.info("### addStatisticsStep 종료")
        }
    }

    @Bean
    fun makeDailyStatisticsStep(): Step {
        return stepBuilderFactory["makeDailyStatisticsStep"]
            .tasklet(makeDailyStatisticsTasklet)
            .build()
    }

    @Bean
    fun makeWeeklyStatisticsStep(): Step {
        return stepBuilderFactory["makeWeeklyStatisticsStep"]
            .tasklet(makeWeeklyStatisticsTasklet)
            .build()
    }

}