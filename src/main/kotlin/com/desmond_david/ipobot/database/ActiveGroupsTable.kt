package com.desmond_david.ipobot.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object ActiveGroupsTable: IdTable<Long>("active_groups") {
    val groupId = long("group_id").uniqueIndex()
    override val id: Column<EntityID<Long>> = groupId.entityId()
}

class ActiveGroupsDao(id: EntityID<Long>) : Entity<Long>(id) {

    companion object : EntityClass<Long, ActiveGroupsDao>(ActiveGroupsTable)

    var groupId by ActiveGroupsTable.groupId
}
