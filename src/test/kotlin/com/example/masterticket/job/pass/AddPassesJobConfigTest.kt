package com.example.masterticket.job.pass

import com.example.masterticket.UserGroupMapping.UserGroupMapping
import com.example.masterticket.UserGroupMapping.UserGroupMappingRepository
import com.example.masterticket.bulkpass.BulkPass
import com.example.masterticket.bulkpass.BulkPassRepository
import com.example.masterticket.bulkpass.BulkPassStatus
import com.example.masterticket.config.QuerydslConfig
import com.example.masterticket.config.TestBatchConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBatchTest
@SpringBootTest(classes = [AddPassesJobConfig::class, AddPassesTasklet::class, TestBatchConfig::class, QuerydslConfig::class])
class AddPassesJobConfigTest @Autowired constructor(
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val bulkPassRepository: BulkPassRepository,
    private val userGroupMappingRepository: UserGroupMappingRepository
) {

    @Test
    @Throws(Exception::class)
    @DisplayName("이용권발급 job")
    fun addPassesJob() {
        // given
        addBulkPass(10)

        // when
        val jobExecution = jobLauncherTestUtils!!.launchJob()
        val jobInstance = jobExecution.jobInstance

        // then
        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        Assertions.assertEquals("addPassesJob", jobInstance.jobName)
    }

    private fun addBulkPass(size: Int) {
        val userGroupId = "GROUP"
        val userId = "A1000000"
        val packageSeq = 1L
        val count = 10

        val now = LocalDateTime.now()

        val bulkPassEntity = BulkPass(
            packageId = packageSeq,
            userGroupId = userGroupId,
            status = BulkPassStatus.READY,
            count = count,
            startedAt = now,
            endedAt = now.plusDays(60)
        )

        val userGroupMappingEntity = UserGroupMapping(userGroupId = userGroupId, userId = userId)
        bulkPassRepository.save(bulkPassEntity)
        userGroupMappingRepository.save(userGroupMappingEntity)
    }
}