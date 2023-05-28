package com.example.masterticket.job.statistics

import com.example.masterticket.statistics.AggregatedStatistics
import com.example.masterticket.statistics.StatisticsRepository
import com.example.masterticket.util.CustomCSVWriter
import com.example.masterticket.util.LocalDateTimeUtils
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@StepScope
class MakeWeeklyStatisticsTasklet(
    @Value("#{jobParameters[from]}") private val fromString: String,
    @Value("#{jobParameters[to]}") private val toString: String,
    private val statisticsRepository: StatisticsRepository
) : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val from = LocalDateTimeUtils.parse(fromString)
        val to = LocalDateTimeUtils.parse(toString)

        val statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to)
        val weeklyStatisticsEntityMap = LinkedHashMap<Int, AggregatedStatistics>()

        for (statistics in statisticsList) {
            val week = LocalDateTimeUtils.getWeekOfYear(statistics.statisticsAt)
            val savedStatisticsEntity = weeklyStatisticsEntityMap[week]

            if (savedStatisticsEntity == null) {
                weeklyStatisticsEntityMap[week] = statistics
            } else {
                savedStatisticsEntity.merge(statistics)
            }
        }

        val data = mutableListOf<Array<String>>()
        data.add(arrayOf("week", "allCount", "attendedCount", "cancelledCount"))
        weeklyStatisticsEntityMap.forEach { (week, statistics) ->
            data.add(
                arrayOf(
                    "Week $week",
                    statistics.allCount.toString(),
                    statistics.attendedCount.toString(),
                    statistics.cancelledCount.toString()
                )
            )
        }
        CustomCSVWriter.write("weekly_statistics_${LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD)}.csv", data)
        return RepeatStatus.FINISHED
    }
}