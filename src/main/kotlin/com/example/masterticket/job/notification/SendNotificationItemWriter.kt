package com.example.masterticket.job.notification

import com.example.masterticket.Notification.Notification
import com.example.masterticket.Notification.NotificationRepository
import com.example.masterticket.adapter.message.KakaoTalkMessageAdapter
import lombok.RequiredArgsConstructor
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@RequiredArgsConstructor
class SendNotificationItemWriter(
    val notificationRepository: NotificationRepository,
    val kakaoTalkMessageAdapter: KakaoTalkMessageAdapter
) : ItemWriter<Notification> {

    @Throws(java.lang.Exception::class)
    override fun write(notificationEntities: List<Notification>) {
        var count: Int = 0
        for (notification: Notification in notificationEntities) {
            val successful: Boolean =
                kakaoTalkMessageAdapter.sendKakaoTalkMessage(notification.uuid, notification.text)
            if (successful) {
                notification.sent = true
                notification.sentAt =LocalDateTime.now()
                notificationRepository.save(notification)
                count++
            }
        }
    }

}