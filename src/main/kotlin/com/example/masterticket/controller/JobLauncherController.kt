package com.example.masterticket.controller

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("job")
@RequiredArgsConstructor
class JobLauncherController(
    val jobLauncher: JobLauncher,
    val jobRegistry: JobRegistry
) {

    @PostMapping("/launcher")
    @Throws(Exception::class)
    fun launchJob(@RequestBody request: JobLauncherRequest): ExitStatus? {
        val job = jobRegistry.getJob(request.name)
        return jobLauncher.run(job, request.getJobParameters()).exitStatus
    }
}