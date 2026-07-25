package com.desmond_david.ipobot.bot

import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Holds all supported bots in the system. This acts as a centralized place to access any configured bot.
 */
class BotRegistry {

    companion object {
        private val logger = KotlinLogging.logger {}
        val bots: MutableMap<String, IPOBot> = mutableMapOf()
        fun registerBot(ipoBot: IPOBot) {
            logger.info { "Registering bot: ${ipoBot.getBotName()}" }
            bots[ipoBot.getBotName()] = ipoBot
        }
    }
}