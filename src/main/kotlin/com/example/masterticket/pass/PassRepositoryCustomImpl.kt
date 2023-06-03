package com.example.masterticket.pass

import com.example.masterticket.pass.QPass.pass
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class PassRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : PassRepositoryCustom {

    override fun findPassByStatusAndEndedAt(
        status: PassStatus,
        endedAt: LocalDateTime,
        pageable: Pageable
    ): Page<Pass> {
        val content = jpaQueryFactory
            .selectFrom(pass)
            .where(pass.status.eq(status), pass.endedAt.loe(endedAt))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total: Long = jpaQueryFactory
            .select(pass.count())
            .from(pass)
            .where(pass.status.eq(status), pass.endedAt.loe(endedAt)).fetch().get(0)

        return PageImpl(content, pageable, total)
    }
}