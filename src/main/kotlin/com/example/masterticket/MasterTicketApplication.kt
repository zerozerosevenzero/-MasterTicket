package com.example.masterticket

import org.springframework.batch.core.*
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.stereotype.Component

@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication
class MasterTicketApplication {
    @Autowired
    private lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    private lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun reader(): ItemReader<String> {
        val items = listOf("Hello", "World", "Batch")
        return ListItemReader(items)
    }

    @Bean
    fun processor(): ItemProcessor<String, String> {
        return ItemProcessor { item ->
            item.toUpperCase()
        }
    }

    @Bean
    fun writer(): ItemWriter<String> {
        return ItemWriter { items ->
            items.forEach { item ->
                println("Writing item: $item")
            }
        }
    }

    @Bean
    fun step(reader: ItemReader<String>, processor: ItemProcessor<String, String>, writer: ItemWriter<String>): Step {
        return stepBuilderFactory.get("step")
            .chunk<String, String>(1)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun job(step: Step): Job {
        return jobBuilderFactory.get("job")
            .listener(object : JobExecutionListenerSupport() {
                override fun afterJob(jobExecution: JobExecution) {
                    println("Job completed!")
                }
            })
            .start(step)
            .build()
    }

    @Component
    class JobRunner(private val jobLauncher: JobLauncher, private val job: Job) {
        fun runJob() {
            val jobExecution = jobLauncher.run(job, JobParameters())
            println("Job Execution Status: ${jobExecution.status}")
        }
    }
}

fun main(args: Array<String>) {
    val context = runApplication<MasterTicketApplication>(*args)
    val jobRunner = context.getBean(MasterTicketApplication.JobRunner::class.java)
    jobRunner.runJob()
}
