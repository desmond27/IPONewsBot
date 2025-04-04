package com.desmond_david.ipobot.database

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IPODataTableDaoTableTest {

    @Test
    @DisplayName("Initialize DB and create table.")
    fun dbMustInitializeAndTableMustBeCreated() {

        DatabaseHelper.initDb("jdbc:sqlite:ipobot-test.db")

        transaction {
            IPODataTable.upsert {
                it[ipo] = "IPO name"
                it[rating] = "IPO rating"
                it[status] = "status"
                it[price] = 100
                it[gmp] = 63.5
                it[estListing] = "est. listing"
                it[ipoSize] = "100 Cr."
                it[lot] = 1000
                it[open] = LocalDate.parse("2025-12-12")
                it[close] = LocalDate.parse("2025-12-16")
                it[boaDate] = LocalDate.parse("2025-12-16")
                it[listing] = LocalDate.parse("2025-12-16")
            }

            val ipoEntry = IPODataTableDao["IPO name"]
            assertNotNull(ipoEntry)
            assertEquals("IPO name", ipoEntry.ipo)
            assertEquals("IPO rating", ipoEntry.rating)
            assertEquals("status", ipoEntry.status)
            assertEquals(100, ipoEntry.price)
            assertEquals(63.5, ipoEntry.gmp)
            assertEquals("est. listing", ipoEntry.estListing)
            assertEquals("100 Cr.", ipoEntry.ipoSize)
            assertEquals(1000, ipoEntry.lot)
            assertEquals(LocalDate.parse("2025-12-12"), ipoEntry.open)
            assertEquals(LocalDate.parse("2025-12-16"), ipoEntry.close)
            assertEquals(LocalDate.parse("2025-12-16"), ipoEntry.boaDate)
            assertEquals(LocalDate.parse("2025-12-16"), ipoEntry.listing)
        }
        transaction {
            // Putting this in a separate transaction for observational purposes.
            IPODataTable.deleteAll()
        }

        // Delete the db file to avoid interfering with other tests.
        Files.deleteIfExists(Paths.get("ipobot-test.db"))
    }
}