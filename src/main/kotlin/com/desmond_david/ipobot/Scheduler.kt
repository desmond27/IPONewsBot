package com.desmond_david.ipobot

import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Date
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

object Scheduler {
    val logger = KotlinLogging.logger {}
    val timer = Timer()

    fun initScheduler(timerTask: TimerTask) {

        val configuredExecTime = AppProperties.DAILY_ALERT_TIME.split(":")

        val timeNow = LocalDateTime.now().atZone(ZoneId.of(AppProperties.DAILY_ALERT_TIMEZONE))

        val nextExecutionTime = LocalDateTime.of(
            timeNow.plusDays(1).toLocalDate(),
            LocalTime.of(configuredExecTime[0].toInt(), configuredExecTime[1].toInt(), 0)
        )

        logger.info { "Scheduled task will run at $nextExecutionTime" }

        timer.scheduleAtFixedRate(
            timerTask,
            Date.from(nextExecutionTime.atZone(ZoneId.of(AppProperties.DAILY_ALERT_TIMEZONE)).toInstant()),
            86400L * 1000L  // 24 hours
        )
    }
}