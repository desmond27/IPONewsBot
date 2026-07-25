package com.desmond_david.ipobot

import com.desmond_david.ipobot.bot.BotRegistry
import com.desmond_david.ipobot.bot.ClosingTodayTimerTask
import com.desmond_david.ipobot.bot.telegram.TelegramBot
import com.desmond_david.ipobot.database.DatabaseHelper
import com.desmond_david.ipobot.service.ActiveGroupsService
import com.desmond_david.ipobot.service.IPOService
import com.desmond_david.ipobot.service.InvestorgainService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting IPONewsBot..." }

    try {
        // Initialize db
        DatabaseHelper.initDb()

        // Initialize services
        val ipoService = InvestorgainService()
        val activeGroupsService = ActiveGroupsService()

        // Initialize a scheduled timer task
        logger.info { "Initializing timer to run at ${AppProperties.DAILY_ALERT_TIME} ${AppProperties.DAILY_ALERT_TIMEZONE}" }
        Scheduler.initScheduler(ClosingTodayTimerTask(ipoService))

        if(AppProperties.TELEGRAM_ENABLED)
            BotRegistry.registerBot(initTelegramBot(ipoService, activeGroupsService))

    } catch (e: Exception) {
        logger.error(e) { "An exception occurred while initializing the bot." }
    }
}

fun initTelegramBot(ipoService: IPOService, activeGroupsService: ActiveGroupsService): TelegramBot {

    val botApplication = TelegramBotsLongPollingApplication()
    val telegramClient = OkHttpTelegramClient(TelegramProperties.BOT_TOKEN)
    val telegramBot = TelegramBot(telegramClient, TelegramProperties.BOT_USERNAME, ipoService, activeGroupsService)

    telegramBot.onRegister()
    botApplication.registerBot(TelegramProperties.BOT_TOKEN, telegramBot)

    return telegramBot
}
