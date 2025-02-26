package com.desmond_david.ipobot.database

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DatabaseHelperTest {

    @Test
    fun storeToDb() {
        transaction {
            DatabaseHelper.storeToDb(getTestIpoData())
        }
        transaction {
            val batchUpsert = IPODataTable.selectAll()
            assertNotNull(batchUpsert)
            assertEquals(1, batchUpsert.count())
            IPODataTable.deleteAll()
        }
    }

    private fun getTestIpoData(): List<IpoDto> {
        return listOf(
            IpoDto(
                "test-name",
                "****",
                "Upcoming",
                100,
                60,
                "estlisting-test",
                "100 Cr",
                1000,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
            ),
        )
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun initDatabase() {
            DatabaseHelper.initDb()
        }
    }
}