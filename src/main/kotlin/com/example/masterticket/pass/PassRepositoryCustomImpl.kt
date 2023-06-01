package com.example.masterticket.pass

import com.example.masterticket.pass.QPass.pass
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDateTime

class PassRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : PassRepositoryCustom {

    override fun findPassByStatusAndEndedAt(status: PassStatus, endedAt: LocalDateTime): MutableList<Pass> {
        return jpaQueryFactory
            .selectFrom(pass)
            .where(pass.status.eq(status), pass.endedAt.loe(endedAt))
            .fetch()
    }
}