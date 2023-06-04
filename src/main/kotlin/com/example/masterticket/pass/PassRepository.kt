package com.example.masterticket.pass

import org.springframework.data.jpa.repository.JpaRepository

interface PassRepository : JpaRepository<Pass, Long>, PassRepositoryCustom {
}