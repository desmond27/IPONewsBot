package com.desmond_david.ipobot

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

object AppProperties {

    lateinit var BOT_TOKEN: String
    lateinit var BOT_USERNAME: String
    var BOT_CREATOR_ID: Long = -1L
    var CONTROL_GROUP_CHAT_ID: Long = -1L
    lateinit var DAILY_ALERT_TIME: String
    lateinit var DAILY_ALERT_TIMEZONE: String

    init {
        logger.info { "Loading properties from ipobot.properties on classpath..."}
        try {
            Properties().also {
                it.load(AppProperties::class.java.getResourceAsStream("/ipobot.properties"))
                BOT_TOKEN = it.getProperty("BOT_TOKEN")
                BOT_USERNAME = it.getProperty("BOT_USERNAME")
                BOT_CREATOR_ID = it.getProperty("BOT_CREATOR_ID").toLong()
                CONTROL_GROUP_CHAT_ID = it.getProperty("CONTROL_GROUP_CHAT_ID").toLong()
                DAILY_ALERT_TIME = it.getProperty("DAILY_ALERT_TIME")
                DAILY_ALERT_TIMEZONE = it.getProperty("DAILY_ALERT_TIMEZONE")
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to load application properties from ipobot.properties" }
        }

        logger.info { "Loaded properties from ipobot.properties" }
    }
}