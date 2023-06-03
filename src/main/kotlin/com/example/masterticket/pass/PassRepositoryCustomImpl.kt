package com.example.masterticket.pass

import com.example.masterticket.pass.QPass.pass
import com.querydsl.jpa.impl.JPAQueryFactory
import com.querydsl.jpa.impl.JPAUpdateClause
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
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
            .where(pass.status.eq(status), pass.endedAt.loe(endedAt))
            .fetch()[0]

        return PageImpl(content, pageable, total)
    }

    override fun updateRemainingCount(id: Long, remainingCount: Int?): Long {
        return jpaQueryFactory.update(pass)
                .set(pass.remainingCount, remainingCount)
                .set(pass.modifiedAt, LocalDateTime.now())
                .where(pass.id.eq(id))
                .execute()
    }
}