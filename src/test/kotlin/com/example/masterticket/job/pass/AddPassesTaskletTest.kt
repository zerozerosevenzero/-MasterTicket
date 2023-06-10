package com.example.masterticket.job.pass

import com.example.masterticket.UserGroupMapping.UserGroupMapping
import com.example.masterticket.UserGroupMapping.UserGroupMappingRepository
import com.example.masterticket.bulkpass.BulkPass
import com.example.masterticket.bulkpass.BulkPassRepository
import com.example.masterticket.bulkpass.BulkPassStatus
import com.example.masterticket.config.TestBatchConfig
import com.example.masterticket.pass.Pass
import com.example.masterticket.pass.PassRepository
import com.example.masterticket.pass.PassStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime

@ContextConfiguration(classes = [AddPassesTasklet::class, TestBatchConfig::class])
@ExtendWith(MockitoExtension::class)
class AddPassesTaskletTest {

    @Mock
    private lateinit var stepContribution: StepContribution

    @Mock
    private lateinit var chunkContext: ChunkContext

    @Mock
    private lateinit var passRepository: PassRepository

    @Mock
    private lateinit var bulkPassRepository: BulkPassRepository

    @Mock
    private lateinit var userGroupMappingRepository: UserGroupMappingRepository

    @InjectMocks
    private lateinit var addPassesTasklet: AddPassesTasklet

    @Test
    fun 이용권발급_AddPassTasklet() {
        // given
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

        Mockito.`when`(
            bulkPassRepository.findByStatusAndStartedAtGreaterThan(
                ArgumentMatchers.eq(BulkPassStatus.READY),
                ArgumentMatchers.any()
            )
        )
            .thenReturn(listOf(bulkPassEntity))
        Mockito.`when`(userGroupMappingRepository.findByUserGroupId("GROUP"))
            .thenReturn(listOf(userGroupMappingEntity))

        val repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext)

        // then
        Assertions.assertEquals(RepeatStatus.FINISHED, repeatStatus)

        val passEntitiesCaptor = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<Pass>>
        Mockito.verify(passRepository, Mockito.times(1)).saveAll(passEntitiesCaptor.capture())
        val passEntities = passEntitiesCaptor.value

        Assertions.assertEquals(1, passEntities.size)
        val passEntity = passEntities[0]
        Assertions.assertEquals(packageSeq, passEntity.packageId)
        Assertions.assertEquals(userId, passEntity.userId)
        Assertions.assertEquals(PassStatus.READY, passEntity.status)
        Assertions.assertEquals(count, passEntity.remainingCount)
    }
}