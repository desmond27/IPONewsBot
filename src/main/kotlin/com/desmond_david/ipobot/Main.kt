package com.desmond_david.ipobot

import com.desmond_david.ipobot.bot.BotRegistry
import com.desmond_david.ipobot.bot.ClosingTodayTimerTask
import com.desmond_david.ipobot.bot.telegram.TelegramBot
import com.desmond_david.ipobot.database.DatabaseHelper
import com.desmond_david.ipobot.service.IPOService
import com.desmond_david.ipobot.service.InvestorgainService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting IPONewsBot..." }

    try {
        // Initialize IPO Service
        val ipoService = InvestorgainService()

        // Initialize db
        DatabaseHelper.initDb()

        // Initialize a scheduled timer task
        logger.info { "Initializing timer to run at ${AppProperties.DAILY_ALERT_TIME} ${AppProperties.DAILY_ALERT_TIMEZONE}" }
        Scheduler.initScheduler(ClosingTodayTimerTask(ipoService))

        BotRegistry.registerBot("telegram", initTelegramBot(ipoService))

    } catch (e: Exception) {
        logger.error(e) { "An exception occurred while initializing the bot." }
    }
}

fun initTelegramBot(ipoService: IPOService): TelegramBot {

    val botApplication = TelegramBotsLongPollingApplication()
    val telegramClient = OkHttpTelegramClient(AppProperties.BOT_TOKEN)
    val telegramBot = TelegramBot(telegramClient, AppProperties.BOT_USERNAME, ipoService)

    telegramBot.onRegister()
    botApplication.registerBot(AppProperties.BOT_TOKEN, telegramBot)

    return telegramBot

}
