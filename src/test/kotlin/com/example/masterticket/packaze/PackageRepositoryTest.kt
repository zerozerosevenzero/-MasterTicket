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
class PackageRepositoryTest {

    @Autowired
    private val packageRepository: PackageRepository? = null

    @Test
    fun 패키지저장() {
        // given
        val packageEntity = Package(name = "바디 챌린지 PT 12주", count = 1, period = 84)

        // when
        packageRepository!!.save(packageEntity)

        // then
        assertNotNull(packageEntity.id)
    }

    @Test
    fun 조회_및_저장() {
        // given
        val dateTime = LocalDateTime.now().minusMinutes(1)
        val packageEntity0 = Package(name = "학생 전용 3개월", period = 90)
        packageRepository!!.save(packageEntity0)
        val packageEntity1 = Package(name = "학생 전용 6개월", period = 180)
        packageRepository!!.save(packageEntity1)

        // when
        val packageEntities: List<Package> =
            packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("id").descending()))

        // then
        Assertions.assertEquals(1, packageEntities.size)
        assertEquals(packageEntity1.id, packageEntities[0].id)
    }
}