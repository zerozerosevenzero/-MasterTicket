package com.example.masterticket.job.pass

import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassStatus
import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.Map
import javax.persistence.EntityManagerFactory

@Configuration
@RequiredArgsConstructor
//@Transactional
class ExpirePassesJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
) {
    val CHUNK_SIZE: Int = 5

    @Bean
    fun expirePassesJob(): Job? {
        return jobBuilderFactory["expirePassesJob"]
            .start(expirePassesStep())
            .build()
    }

    @Bean
    fun expirePassesStep(): Step {
        return stepBuilderFactory["expirePassesStep"]
            .allowStartIfComplete(true)
            .chunk<Pass, Pass>(CHUNK_SIZE)
            .reader(expirePassesItemReader())
            .processor(expirePassesItemProcessor())
            .writer(expirePassesItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun expirePassesItemReader(): JpaCursorItemReader<Pass> {
        return JpaCursorItemReaderBuilder<Pass>()
            .name("expirePassesItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select p from Pass p where p.status = :status and p.endedAt <= :endedAt")
            .parameterValues(Map.of<String, Any>("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
            .build()
    }

    @Bean
    fun expirePassesItemProcessor(): ItemProcessor<Pass, Pass> {
        return ItemProcessor<Pass, Pass> { pass: Pass ->
            pass.updateExpiringPass()
            pass
        }
    }

    @Bean
    fun expirePassesItemWriter(): JpaItemWriter<Pass> {
        return JpaItemWriterBuilder<Pass>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}