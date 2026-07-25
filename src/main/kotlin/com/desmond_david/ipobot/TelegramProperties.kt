package com.desmond_david.ipobot

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

object TelegramProperties {

    lateinit var BOT_TOKEN: String
    lateinit var BOT_USERNAME: String
    var BOT_CREATOR_ID: Long = -1L
    var CONTROL_GROUP_CHAT_ID: Long = -1L

    init {
        logger.info { "Loading properties from telegram.properties on classpath..."}
        try {
            Properties().also {
                it.load(AppProperties::class.java.getResourceAsStream("/telegram.properties"))
                BOT_TOKEN = it.getProperty("BOT_TOKEN")
                BOT_USERNAME = it.getProperty("BOT_USERNAME")
                BOT_CREATOR_ID = it.getProperty("BOT_CREATOR_ID").toLong()
                CONTROL_GROUP_CHAT_ID = it.getProperty("CONTROL_GROUP_CHAT_ID").toLong()
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to load telegram properties from telegram.properties" }
        }

        logger.info { "Loaded properties from telegram.properties" }
    }
}