package com.desmond_david.ipobot.bot

import com.desmond_david.ipobot.AppProperties
import com.desmond_david.ipobot.service.IPOService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.TimerTask

class ClosingTodayTimerTask(var ipoService: IPOService) : TimerTask() {
    var logger = KotlinLogging.logger {}

    override fun run() {
        logger.info { "Running scheduled task to post IPOs closing today info." }

        val today = LocalDate.now(ZoneId.of(AppProperties.DAILY_ALERT_TIMEZONE))

        if (today.dayOfWeek.equals(DayOfWeek.SATURDAY) || today.dayOfWeek.equals(DayOfWeek.SUNDAY)) {
            // Don't run on Saturdays and Sundays.
            logger.info { "Today is a weekend. Skipping posting IPOs closing today info." }
        } else {
            logger.info { "Updating IPO data from ${ipoService.getServiceName()}." }
            ipoService.saveData()

            logger.info { "Posting IPOs closing today info to subscribed channels." }
            BotRegistry.bots.values.forEach { it.sendTodaysClosingIPOInfo() }
        }

        logger.info {
            "Scheduled task completed. Next execution time ${
                LocalDateTime.now().plusHours(24).truncatedTo(ChronoUnit.MINUTES)
            }"
        }
    }
}