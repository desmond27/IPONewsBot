package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.ActiveGroupsDao
import org.jetbrains.exposed.sql.transactions.transaction

class ActiveGroupsService {

    fun isGroupAlreadyActive(chatId: String): Boolean {
        var foundId: String? = null
        transaction {
            foundId = ActiveGroupsDao.findById(chatId)?.groupId
        }
        return foundId != null
    }

    fun addToActiveGroups(chatId: String) {
        transaction {
            ActiveGroupsDao.new { groupId = chatId }
        }
    }

    fun getAllActiveGroupIds(): List<String> {
        return transaction { ActiveGroupsDao.all().map { it.groupId } }
    }
}