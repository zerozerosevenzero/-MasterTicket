package com.example.masterticket.job

import com.example.masterticket.UserGroupMapping.UserGroupMappingRepository
import com.example.masterticket.bulkpass.BulkPass
import com.example.masterticket.bulkpass.BulkPassRepository
import com.example.masterticket.bulkpass.BulkPassStatus
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
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

@Slf4j
@RequiredArgsConstructor
@StepScope
@Component
class AddPassesTasklet(
    val passRepository: PassRepository,
    val bulkPassRepository: BulkPassRepository,
    val userGroupMappingRepository: UserGroupMappingRepository
) : Tasklet {

    val logger: Logger = LoggerFactory.getLogger(AddPassesTasklet::class.java)

    @Value("#{jobParameters[datetime]}")
    val datetime: String? = null

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val startedAt = LocalDateTime.now().minusDays(1)
        val bulkPasses: List<BulkPass> =
            bulkPassRepository.findByStatusAndStartedAtGreaterThan(BulkPassStatus.READY, startedAt)

        var count = 0
        for (bulkPass in bulkPasses) {
            val userIds: List<String> = userGroupMappingRepository.findByUserGroupId(bulkPass.userGroupId)
                .map { userGroupMapping -> userGroupMapping.userId!! }.toList()

            count += addPasses(bulkPass, userIds)
            bulkPass.updateStatus(BulkPassStatus.COMPLETED)
        }
        logger.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt)
        return RepeatStatus.FINISHED
    }

    // 이용권 추가 Pass엔티티 저장
    private fun addPasses(bulkPass: BulkPass, userIds: List<String>): Int {
        val passes: MutableList<Pass> = mutableListOf()
        userIds.forEach { userId -> passes.add(Pass.toPassEntity(bulkPass, userId)) }
        return passRepository.saveAll(passes).size
    }

}