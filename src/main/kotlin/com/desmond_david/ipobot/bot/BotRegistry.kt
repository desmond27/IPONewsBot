package com.desmond_david.ipobot.bot

/**
 * Holds all supported bots in the system. This acts as a centralized place to access any configured bot.
 */
class BotRegistry {

    companion object {
        val bots: MutableMap<String, IPOBot> = mutableMapOf()
        fun registerBot(botName: String, ipoBot: IPOBot) {
            bots[botName] = ipoBot
        }
    }
}