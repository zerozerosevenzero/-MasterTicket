package com.example.masterticket.job

import com.example.masterticket.Notification.Notification
import com.example.masterticket.Notification.NotificationEvent
import com.example.masterticket.booking.Booking
import com.example.masterticket.booking.BookingStatus
import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.batch.item.support.SynchronizedItemStreamReader
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import java.time.LocalDateTime
import java.util.Map
import javax.persistence.EntityManagerFactory

@RequiredArgsConstructor
@Configuration
class SendNotificationBeforeClassJobConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
    val sendNotificationItemWriter: SendNotificationItemWriter,
) {

    private val CHUNK_SIZE = 10

    @Bean
    fun sendNotificationBeforeClassJob(): Job? {
        return jobBuilderFactory!!["sendNotificationBeforeClassJob"]
            .start(addNotificationStep()!!)
            .next(sendNotificationStep()!!)
            .build()
    }

    @Bean
    fun addNotificationStep(): Step? {
        return stepBuilderFactory!!["addNotificationStep"]
            .chunk<Booking, Notification>(CHUNK_SIZE)
            .reader(addNotificationItemReader())
            .processor(addNotificationItemProcessor())
            .writer(addNotificationItemWriter())
            .build()
    }

    @Bean
    fun addNotificationItemReader(): JpaPagingItemReader<Booking> {
        return JpaPagingItemReaderBuilder<Booking>()
            .name("addNotificationItemReader")
            .entityManagerFactory(entityManagerFactory) // pageSize: 한 번에 조회할 row 수
            .pageSize(CHUNK_SIZE) // 상태(status)가 준비중이며, 시작일시(startedAt)이 10분 후 시작하는 예약이 알람 대상이 됩니다.
            .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
            .parameterValues(
                Map.of<String, Any>(
                    "status",
                    BookingStatus.READY,
                    "startedAt",
                    LocalDateTime.now().plusMinutes(10)
                )
            )
            .build()
    }

    @Bean
    fun addNotificationItemProcessor(): ItemProcessor<Booking, Notification> {
        return ItemProcessor<Booking, Notification> { booking: Booking ->
            Notification.toNotificationEntity(booking, NotificationEvent.BEFORE_CLASS)
        }
    }

    @Bean
    fun addNotificationItemWriter(): JpaItemWriter<Notification> {
        return JpaItemWriterBuilder<Notification>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }

    @Bean
    fun sendNotificationStep(): Step? {
        return stepBuilderFactory!!["sendNotificationStep"]
            .chunk<Notification, Notification>(CHUNK_SIZE)
            .reader(sendNotificationItemReader())
            .writer(sendNotificationItemWriter)
            .taskExecutor(SimpleAsyncTaskExecutor()) // 가장 간단한 멀티쓰레드 TaskExecutor를 선언하였습니다.
            .build()
    }


    @Bean
    fun sendNotificationItemReader(): SynchronizedItemStreamReader<Notification> {
        val itemReader: JpaCursorItemReader<Notification> = JpaCursorItemReaderBuilder<Notification>()
            .name("sendNotificationItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
            .parameterValues(Map.of<String, Any>("event", NotificationEvent.BEFORE_CLASS, "sent", false))
            .build()
        return SynchronizedItemStreamReaderBuilder<Notification>()
            .delegate(itemReader)
            .build()
    }
}