package com.example.masterticket.job.pass

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@RequiredArgsConstructor
class AddPassesJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val addPassesTasklet: AddPassesTasklet,
) {

    @Bean
    fun addPassesJob(): Job? {
        return jobBuilderFactory["addPassesJob"]
            .start(addPassesStep())
            .build()
    }


    @Bean
    fun addPassesStep(): Step {
        return stepBuilderFactory["addPassesStep"]
            .tasklet(addPassesTasklet)
            .build()
    }
}