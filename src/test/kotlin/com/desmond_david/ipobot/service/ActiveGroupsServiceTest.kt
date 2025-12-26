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
            ActiveGroupsDao.new { groupId = "123456"; botName = "telegram" }
            ActiveGroupsDao.new { groupId = "789012"; botName = "telegram" }
            ActiveGroupsDao.new { groupId = "zxvbasu02fnap8fr"; botName = "matrix" }
            ActiveGroupsDao.new { groupId = "piwef-avpwer8234"; botName = "matrix" }
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
        activeGroupsService.addToActiveGroups("987654", "telegram")
        activeGroupsService.addToActiveGroups("gapzxucvhieur98234", "matrix")
        var group = transaction { ActiveGroupsDao.findById("987654")!! }
        assertNotNull(group)
        assertEquals("987654", group.groupId)
        group = transaction { ActiveGroupsDao.findById("gapzxucvhieur98234")!! }
        assertNotNull(group)
        assertEquals("gapzxucvhieur98234", group.groupId)
    }

    @Test
    fun getAllActiveGroupIds() {
        var groupIds = activeGroupsService.getAllActiveGroupIds("telegram")
        assertNotNull(groupIds)
        assertTrue(groupIds.isNotEmpty())
        assertEquals(2, groupIds.size)
        assertIterableEquals(listOf("123456", "789012"), groupIds)

        groupIds = activeGroupsService.getAllActiveGroupIds("matrix")
        assertNotNull(groupIds)
        assertTrue(groupIds.isNotEmpty())
        assertEquals(2, groupIds.size)
        assertIterableEquals(listOf("zxvbasu02fnap8fr", "piwef-avpwer8234"), groupIds)
    }

    @Test
    fun removeFromActiveGroups() {
        activeGroupsService.removeActiveGroup("123456")
        activeGroupsService.removeActiveGroup("789012")
        activeGroupsService.removeActiveGroup("zxvbasu02fnap8fr")
        activeGroupsService.removeActiveGroup("piwef-avpwer8234")
        assertEquals(0, activeGroupsService.getAllActiveGroupIds("telegram").size)
        assertEquals(0, activeGroupsService.getAllActiveGroupIds("matrix").size)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun initDb() {
            DatabaseHelper.initDb("jdbc:sqlite:ipobot-activegroup.db")
            transaction { SchemaUtils.create(ActiveGroupsTable) }
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            Files.deleteIfExists(Paths.get("ipobot-activegroup.db"))
        }
    }
}