package com.example.masterticket.job.statistics

import com.example.masterticket.statistics.AggregatedStatistics
import com.example.masterticket.statistics.StatisticsRepository
import com.example.masterticket.util.CustomCSVWriter
import com.example.masterticket.util.LocalDateTimeUtils
import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@StepScope
@RequiredArgsConstructor
class MakeDailyStatisticsTasklet(
    @Value("#{jobParameters[from]}")
    var fromString: String,
    @Value("#{jobParameters[to]}")
    var toString: String,
    val statisticsRepository: StatisticsRepository
) : Tasklet {
    private val logger: Logger = LoggerFactory.getLogger(MakeDailyStatisticsTasklet::class.java)

    @Throws(Exception::class)
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val from: LocalDateTime = LocalDateTimeUtils.parse(fromString)
        val to: LocalDateTime = LocalDateTimeUtils.parse(toString)
        val statisticsList: List<AggregatedStatistics> = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to)
        val data: MutableList<Array<String>> = ArrayList()
        data.add(arrayOf("statisticsAt", "allCount", "attendedCount", "cancelledCount"))
        for (statistics in statisticsList) {
            data.add(
                arrayOf(
                    LocalDateTimeUtils.format(statistics.statisticsAt),
                    java.lang.String.valueOf(statistics.allCount),
                    java.lang.String.valueOf(statistics.attendedCount),
                    java.lang.String.valueOf(statistics.cancelledCount)
                )
            )
        }
        CustomCSVWriter.write(
            "daily_statistics_" + LocalDateTimeUtils.format(
                from,
                LocalDateTimeUtils.YYYY_MM_DD
            ) + ".csv", data
        )
        return RepeatStatus.FINISHED
    }
}