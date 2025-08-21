package com.desmond_david.ipobot.database

import com.desmond_david.ipobot.database.IPODataTable.boaDate
import com.desmond_david.ipobot.database.IPODataTable.gmp
import com.desmond_david.ipobot.database.IPODataTable.gmpPercent
import com.desmond_david.ipobot.database.IPODataTable.ipo
import com.desmond_david.ipobot.database.IPODataTable.ipoSize
import com.desmond_david.ipobot.database.IPODataTable.listing
import com.desmond_david.ipobot.database.IPODataTable.lot
import com.desmond_david.ipobot.database.IPODataTable.open
import com.desmond_david.ipobot.database.IPODataTable.price
import com.desmond_david.ipobot.database.IPODataTable.rating
import com.desmond_david.ipobot.database.IPODataTable.sub
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction

val logger = KotlinLogging.logger {}
private const val connectionString = "jdbc:sqlite:ipobot.db"

object DatabaseHelper {

    fun initDb() {
        initDb(connectionString)
    }

    fun initDb(connectionString: String) {
        logger.info { "Initializing database connection: $connectionString ..." }

        Database.connect(
            url = connectionString,
            driver = "org.sqlite.JDBC",
        )

        transaction {
            SchemaUtils.create(IPODataTable, ActiveGroupsTable)
        }

        logger.info { "Successfully initialized database." }
    }

    fun storeToDb(data: List<IpoDto>) {
        logger.info { "Storing ${data.size} IPO data" }
        transaction {
            IPODataTable.batchUpsert(data = data, IPODataTable.id) { dto ->
                this[ipo] = dto.ipo
                this[rating] = dto.rating
                this[sub] = dto.sub
                this[price] = dto.price
                this[gmp] = dto.gmp
                this[ipoSize] = dto.ipoSize
                this[lot] = dto.lot
                this[open] = dto.open
                this[IPODataTable.close] = dto.close
                this[boaDate] = dto.boaDate
                this[listing] = dto.listing
                this[gmpPercent] = dto.gmpPercent
            }
        }
        logger.info { "Successfully stored ${data.size} IPO data" }
    }
}
