package com.example.masterticket.packaze

import lombok.extern.slf4j.Slf4j
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Slf4j
@Transactional
@SpringBootTest
class PackazeRepositoryTest {

    @Autowired
    private val packageRepository: PackageRepository? = null

    @Test
    fun 패키지저장() {
        // given
        val packazeEntity = Packaze(name = "바디 챌린지 PT 12주", count = 1, period = 84)

        // when
        packageRepository!!.save(packazeEntity)

        // then
        assertNotNull(packazeEntity.id)
    }

    @Test
    fun 조회_및_저장() {
        // given
        val dateTime = LocalDateTime.now().minusMinutes(1)
        val packazeEntity0 = Packaze(name = "학생 전용 3개월", period = 90)
        packageRepository!!.save(packazeEntity0)
        val packazeEntity1 = Packaze(name = "학생 전용 6개월", period = 180)
        packageRepository!!.save(packazeEntity1)

        // when
        val packazeEntities: List<Packaze> =
            packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("id").descending()))

        // then
        Assertions.assertEquals(1, packazeEntities.size)
        assertEquals(packazeEntity1.id, packazeEntities[0].id)
    }
}