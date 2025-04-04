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
            ActiveGroupsDao.new { groupId = "123456" }
            ActiveGroupsDao.new { groupId = "789012" }
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
        var result = activeGroupsService.isGroupAlreadyActive("123456")
        assertTrue(result)
        result = activeGroupsService.isGroupAlreadyActive("123457")
        assertFalse(result)
    }

    @Test
    fun addToActiveGroups() {
        activeGroupsService.addToActiveGroups("987654")
        val group = transaction { ActiveGroupsDao.findById("987654")!! }
        assertNotNull(group)
        assertEquals("987654", group.groupId)
    }

    @Test
    fun getAllActiveGroupIds() {
        val groupIds = activeGroupsService.getAllActiveGroupIds()
        assertNotNull(groupIds)
        assertTrue(groupIds.isNotEmpty())
        assertEquals(2, groupIds.size)
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