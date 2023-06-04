package com.example.masterticket.job.pass

import com.example.masterticket.booking.Booking
import com.example.masterticket.booking.BookingRepository
import com.example.masterticket.booking.BookingStatus
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassRepository
import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.springframework.batch.integration.async.AsyncItemWriter
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.util.*
import java.util.Map
import java.util.concurrent.Future
import javax.persistence.EntityManagerFactory

@Configuration
@RequiredArgsConstructor
class UsePassesJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
    val passRepository: PassRepository,
    val bookingRepository: BookingRepository,
) {
    private companion object {
        const val CHUNK_SIZE = 10
    }

    @Bean
    fun usePassesJob(): Job {
        return jobBuilderFactory["usePassesJob"]
            .start(usePassesStep())
            .build()
    }

    @Bean
    fun usePassesStep(): Step {
        return stepBuilderFactory["usePassesStep"]
            .chunk<Booking, Future<Booking>>(CHUNK_SIZE)
            .reader(usePassesItemReader())
            .processor(usePassesAsyncItemProcessor())
            .writer(usePassesAsyncItemWriter())
            .build()
    }

//    @Bean
//    fun usePassesItemReader(): JpaCursorItemReader<Booking> {
//        return JpaCursorItemReaderBuilder<Booking>()
//            .name("usePassesItemReader")
//            .entityManagerFactory(entityManagerFactory)
//            .queryString("select b from Booking b join fetch b.pass where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
//            .parameterValues(Map.of<String, Any>("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
//            .build()
//    }

    @Bean
    fun usePassesItemReader(): RepositoryItemReader<Booking> {
        return RepositoryItemReaderBuilder<Booking>()
            .name("usePassesItemReader")
            .repository(bookingRepository)
            .methodName("findBookingByStatusAndEndedAt")
            .arguments(listOf(BookingStatus.COMPLETED, LocalDateTime.now()))
            .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
            .build()
    }

    @Bean
    fun usePassesAsyncItemProcessor(): AsyncItemProcessor<Booking, Booking> {
        val asyncItemProcessor: AsyncItemProcessor<Booking, Booking> = AsyncItemProcessor()
        asyncItemProcessor.setDelegate(usePassesItemProcessor()) // usePassesItemProcessor로 위임하고 결과를 Future에 저장합니다.
        asyncItemProcessor.setTaskExecutor(SimpleAsyncTaskExecutor())
        return asyncItemProcessor
    }

    @Bean
    fun usePassesItemProcessor(): ItemProcessor<Booking, Booking> {
        return ItemProcessor<Booking, Booking> { booking: Booking ->
            // 이용권 잔여 횟수는 차감합니다.
            booking.updateUsedPass()
            booking
        }
    }

    @Bean
    fun usePassesAsyncItemWriter(): AsyncItemWriter<Booking> {
        val asyncItemWriter: AsyncItemWriter<Booking> = AsyncItemWriter()
        asyncItemWriter.setDelegate(usePassesItemWriter())
        return asyncItemWriter
    }

    @Bean
    fun usePassesItemWriter(): JpaItemWriter<Booking> {
        return JpaItemWriterBuilder<Booking>()
            .entityManagerFactory(entityManagerFactory)
            .build()
//        return ItemWriter<Booking> { bookings: List<Booking> ->
//            bookings.forEach { booking ->
//                // 잔여 횟수를 업데이트 합니다.
//                val updatedCount = passRepository.updateRemainingCount(booking.pass.id!!, booking.pass.remainingCount)
//                // 잔여 횟수가 업데이트 완료되면, 이용권 사용 여부를 업데이트합니다.
//                if (updatedCount > 0) {
//                    bookingRepository.updateUsedPass(booking.id, booking.usedPass)
//                }
//            }
//        }
    }

}