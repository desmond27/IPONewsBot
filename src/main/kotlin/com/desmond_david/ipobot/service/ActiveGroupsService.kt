package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.ActiveGroupsDao
import com.desmond_david.ipobot.database.ActiveGroupsTable
import com.desmond_david.ipobot.database.ActiveGroupsTable.groupId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import com.desmond_david.ipobot.database.ActiveGroupsTable.botName as botName1

class ActiveGroupsService {

    val logger = KotlinLogging.logger {}

    /**
     * Check if a group is already active by its chat ID.
     */
    fun isGroupAlreadyActive(chatId: String, botName: String): Boolean {
        var foundId: String? = null
        transaction {
            try {
                val foundEntry = ActiveGroupsTable.selectAll()
                    .where { (groupId eq chatId) and (botName1 eq botName) }
                    .single()
                foundId = foundEntry[groupId]
            } catch (_: NoSuchElementException) {
                logger.debug{ "Group $chatId is not active." }
            }
        }
        return foundId?.isNotEmpty() ?: false
    }

    /**
     * Add a group to active groups with its chat ID and bot name.
     */
    fun addToActiveGroups(chatId: String, botName: String) {
        transaction {
            ActiveGroupsDao.new {
                groupId = chatId
                this.botName = botName
            }
        }
    }

    /**
     * Get all active group IDs for a specific bot.
     */
    fun getAllActiveGroupIds(botName: String): List<String> {
        return transaction {
            ActiveGroupsDao.find { botName1 eq botName }.map { it.groupId }
        }
    }

    /**
     * Remove a group from active groups by its chat ID.
     */
    fun removeActiveGroup(chatId: String) {
        return transaction { ActiveGroupsDao.findById(chatId)?.delete() }
    }
}