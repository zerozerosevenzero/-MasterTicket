package com.example.masterticket.job.pass

import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassRepository
import com.example.masterticket.pass.PassStatus
import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManagerFactory

@Configuration
@RequiredArgsConstructor
class ExpirePassesJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
    val passRepository: PassRepository
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
            .chunk<Pass, Pass>(CHUNK_SIZE)
            .reader(expirePassesItemReader(passRepository = passRepository))
            .processor(expirePassesItemProcessor())
            .writer(expirePassesItemWriter())
            .build()
    }

//    @Bean
//    @StepScope
//    fun expirePassesItemReader(): JpaCursorItemReader<Pass> {
//        return JpaCursorItemReaderBuilder<Pass>()
//            .name("expirePassesItemReader")
//            .entityManagerFactory(entityManagerFactory)
//            .queryString("select p from Pass p where p.status = :status and p.endedAt <= :endedAt")
//            .parameterValues(Map.of<String, Any>("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
//            .build()
//    }

    @Bean
    @StepScope
    fun expirePassesItemReader(passRepository: PassRepository): RepositoryItemReader<Pass> {
        return RepositoryItemReaderBuilder<Pass>()
            .name("expirePassesItemReader")
            .repository(passRepository)
            .methodName("findPassByStatusAndEndedAt")
            .pageSize(10)
            .arguments(listOf(PassStatus.PROGRESSED, LocalDateTime.now()))
            .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
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