package com.example.masterticket.job

import com.example.masterticket.config.QuerydslConfig
import com.example.masterticket.config.TestBatchConfig
import com.example.masterticket.job.pass.ExpirePassesJobConfig
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassRepository
import com.example.masterticket.pass.PassStatus
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.util.*

@SpringBatchTest
@SpringBootTest(classes = [ExpirePassesJobConfig::class, TestBatchConfig::class, QuerydslConfig::class])
class ExpirePassesJobConfigTest @Autowired constructor(
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val passRepository: PassRepository
) {

    @Test
    @Throws(Exception::class)
    @DisplayName("이용권 만료 job")
    fun ExpirePassesJob() {
        // given
        addPass(10)

        // when
        val jobExecution = jobLauncherTestUtils!!.launchJob()
        val jobInstance = jobExecution.jobInstance

        // then
        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        Assertions.assertEquals("expirePassesJob", jobInstance.jobName)
    }

    private fun addPass(size: Int) {
        val now = LocalDateTime.now()
        val random = Random()
        val passEntities: MutableList<Pass> = ArrayList<Pass>()
        for (i in 0 until size) {
            val passEntity = Pass(
                userId = "A1000000$i",
                status = PassStatus.PROGRESSED,
                remainingCount = random.nextInt(11),
                startedAt = now.minusDays(60),
                endedAt = now.minusDays(1)
            )
            passEntities.add(passEntity)
        }
        passRepository!!.saveAll(passEntities)
    }
}