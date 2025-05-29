package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.ActiveGroupsDao
import org.jetbrains.exposed.sql.transactions.transaction

class ActiveGroupsService {

    fun isGroupAlreadyActive(chatId: Long): Boolean {
        var foundId: Long? = null
        transaction {
            foundId = ActiveGroupsDao.findById(chatId)?.groupId
        }
        return foundId != null
    }

    fun addToActiveGroups(chatId: Long) {
        transaction {
            ActiveGroupsDao.new { groupId = chatId }
        }
    }

    fun getAllActiveGroupIds(): List<Long> {
        return transaction { ActiveGroupsDao.all().map { it.groupId } }
    }

    fun removeActiveGroup(chatId: Long) {
        return transaction { ActiveGroupsDao.findById(chatId)?.delete() }
    }
}