package com.example.masterticket.controller

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import java.util.*


data class JobLauncherRequest(
    val name: String,
    val jobParameters: Properties? = null
) {
    fun getJobParameters(): JobParameters {
        return JobParametersBuilder(this.jobParameters!!).addLong("datetime", System.currentTimeMillis())
                .toJobParameters()
    }
}
