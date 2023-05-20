package com.example.masterticket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MasterTicketApplication

fun main(args: Array<String>) {
    runApplication<MasterTicketApplication>(*args)
}
