package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.ActiveGroupsDao
import com.desmond_david.ipobot.database.ActiveGroupsTable
import org.jetbrains.exposed.sql.transactions.transaction

class ActiveGroupsService {

    /**
     * Check if a group is already active by its chat ID.
     */
    fun isGroupAlreadyActive(chatId: String): Boolean {
        var foundId: String? = null
        transaction {
            foundId = ActiveGroupsDao.findById(chatId)?.groupId
        }
        return foundId != null
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
            ActiveGroupsDao.find { ActiveGroupsTable.botName eq botName }.map { it.groupId }
        }
    }

    /**
     * Remove a group from active groups by its chat ID.
     */
    fun removeActiveGroup(chatId: String) {
        return transaction { ActiveGroupsDao.findById(chatId)?.delete() }
    }
}