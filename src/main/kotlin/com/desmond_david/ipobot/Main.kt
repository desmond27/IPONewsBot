package com.desmond_david.ipobot

import com.desmond_david.ipobot.bot.IPONewsBot
import com.desmond_david.ipobot.database.DatabaseHelper
import com.desmond_david.ipobot.service.InvestorgainService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Starting IPONewsBot..." }

    try {
        val ipoService = InvestorgainService()

        DatabaseHelper.initDb()

        val botsApplication = TelegramBotsLongPollingApplication()
        val telegramClient = OkHttpTelegramClient(AppProperties.BOT_TOKEN)
        val ipoNewsBot = IPONewsBot(telegramClient, AppProperties.BOT_USERNAME, ipoService)
        ipoNewsBot.onRegister()
        botsApplication.registerBot(AppProperties.BOT_TOKEN, ipoNewsBot)

    } catch (e: TelegramApiException) {
        logger.error(e) { "An exception occurred while initializing the bot." }
    }
}
