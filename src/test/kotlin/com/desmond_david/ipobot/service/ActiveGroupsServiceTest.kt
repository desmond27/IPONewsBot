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

private const val TELEGRAM = "telegram"
private const val MATRIX = "matrix"

class ActiveGroupsServiceTest {

    private val activeGroupsService = ActiveGroupsService()

    @BeforeEach
    fun setUp() {
        transaction {
            ActiveGroupsDao.new { groupId = "123456"; botName = TELEGRAM }
            ActiveGroupsDao.new { groupId = "789012"; botName = TELEGRAM }
            ActiveGroupsDao.new { groupId = "zxvbasu02fnap8fr"; botName = MATRIX }
            ActiveGroupsDao.new { groupId = "piwef-avpwer8234"; botName = MATRIX }
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
        var result = activeGroupsService.isGroupAlreadyActive("123456", TELEGRAM)
        assertTrue(result)
        result = activeGroupsService.isGroupAlreadyActive("123457", TELEGRAM)
        assertFalse(result)
    }

    @Test
    fun addToActiveGroups() {
        activeGroupsService.addToActiveGroups("987654", TELEGRAM)
        activeGroupsService.addToActiveGroups("gapzxucvhieur98234", MATRIX)
        var group = transaction { ActiveGroupsDao.findById("987654")!! }
        assertNotNull(group)
        assertEquals("987654", group.groupId)
        group = transaction { ActiveGroupsDao.findById("gapzxucvhieur98234")!! }
        assertNotNull(group)
        assertEquals("gapzxucvhieur98234", group.groupId)
    }

    @Test
    fun getAllActiveGroupIds() {
        var groupIds = activeGroupsService.getAllActiveGroupIds(TELEGRAM)
        assertNotNull(groupIds)
        assertTrue(groupIds.isNotEmpty())
        assertEquals(2, groupIds.size)
        assertIterableEquals(listOf("123456", "789012"), groupIds)

        groupIds = activeGroupsService.getAllActiveGroupIds(MATRIX)
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
        assertEquals(0, activeGroupsService.getAllActiveGroupIds(TELEGRAM).size)
        assertEquals(0, activeGroupsService.getAllActiveGroupIds(MATRIX).size)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun initDb() {
            DatabaseHelper.initDb("jdbc:sqlite:test-ipobot-activegroup.db")
            transaction { SchemaUtils.create(ActiveGroupsTable) }
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            Files.deleteIfExists(Paths.get("test-ipobot-activegroup.db"))
        }
    }
}