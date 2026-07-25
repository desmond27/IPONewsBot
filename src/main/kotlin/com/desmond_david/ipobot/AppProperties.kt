package com.desmond_david.ipobot

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

object AppProperties {

    lateinit var DAILY_ALERT_TIME: String
    lateinit var DAILY_ALERT_TIMEZONE: String
    var TELEGRAM_ENABLED: Boolean = false
    var MATRIX_ENABLED: Boolean = false

    init {
        logger.info { "Loading properties from ipobot.properties on classpath..."}
        try {
            Properties().also {
                it.load(AppProperties::class.java.getResourceAsStream("/ipobot.properties"))
                DAILY_ALERT_TIME = it.getProperty("DAILY_ALERT_TIME")
                DAILY_ALERT_TIMEZONE = it.getProperty("DAILY_ALERT_TIMEZONE")
                TELEGRAM_ENABLED = it.getProperty("TELEGRAM_ENABLED").toBoolean()
                MATRIX_ENABLED = it.getProperty("MATRIX_ENABLED").toBoolean()
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to load application properties from ipobot.properties" }
        }

        logger.info { "Loaded properties from ipobot.properties" }
    }
}