package com.desmond_david.ipobot.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object ActiveGroupsTable: IdTable<String>("active_groups") {
    val groupId = varchar("group_id", 100).uniqueIndex()
    val botName = varchar("bot_name", 50)
    override val id: Column<EntityID<String>> = groupId.entityId()
}

class ActiveGroupsDao(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, ActiveGroupsDao>(ActiveGroupsTable)

    var groupId by ActiveGroupsTable.groupId
    var botName by ActiveGroupsTable.botName
}
