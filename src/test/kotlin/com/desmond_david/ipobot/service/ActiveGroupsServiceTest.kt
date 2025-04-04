package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.ActiveGroupsDao
import com.desmond_david.ipobot.database.ActiveGroupsTable
import com.desmond_david.ipobot.database.DatabaseHelper
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Files
import java.nio.file.Paths

class ActiveGroupsServiceTest {

    private val activeGroupsService = ActiveGroupsService()

    @BeforeEach
    fun setUp() {
        transaction {
            SchemaUtils.create(ActiveGroupsTable)
            ActiveGroupsDao.new { groupId = 123456L }
            ActiveGroupsDao.new { groupId = 789012L }
        }
    }

    @AfterEach
    fun tearDown() {
        transaction {
            ActiveGroupsDao.all().forEach { it.delete() }
        }
    }

    @Test
    fun isGroupAlreadyActive() {
        var result = activeGroupsService.isGroupAlreadyActive(123456L)
        assertTrue(result)
        result = activeGroupsService.isGroupAlreadyActive(123457L)
        assertFalse(result)
    }

    @Test
    fun addToActiveGroups() {
        activeGroupsService.addToActiveGroups(987654L)
        val group = transaction { ActiveGroupsDao.findById(987654L)!! }
        assertNotNull(group)
        assertEquals(987654L, group.groupId)
    }

    @Test
    fun getAllActiveGroupIds() {
        val groupIds = activeGroupsService.getAllActiveGroupIds()
        assertNotNull(groupIds)
        assertTrue(groupIds.isNotEmpty())
        assertEquals(2, groupIds.size)
        assertIterableEquals(listOf(123456L, 789012L), groupIds)
    }

    @Test
    fun removeFromActiveGroups() {
        activeGroupsService.removeActiveGroup(123456L)
        activeGroupsService.removeActiveGroup(789012L)
        assertEquals(0, activeGroupsService.getAllActiveGroupIds().size)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun initDb() {
            DatabaseHelper.initDb("jdbc:sqlite:ipobot-activegroup.db")
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            Files.deleteIfExists(Paths.get("ipobot-activegroup.db"))
        }
    }
}