package com.desmond_david.ipobot.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object IPODataTable : IdTable<String>("ipodata") {
    val ipo  = varchar("ipo", 100).uniqueIndex()
    val rating = varchar("rating", 100)
    val sub = varchar("sub", 100)
    val price = integer("price")
    val gmp = varchar("gmp", 100)
    val estListing = varchar("estListing", 100)
    val ipoSize = varchar("ipoSize", 100)
    val lot = integer("lot")
    val open = date("open").nullable()
    val close = date("close").nullable()
    val boaDate = date("boaDate").nullable()
    val listing = date("listing").nullable()
    override val id: Column<EntityID<String>> = ipo.entityId()
}

class IPODataTableDao(id: EntityID<String>) : Entity<String>(id) {

    companion object : EntityClass<String, IPODataTableDao>(IPODataTable)

    var ipo by IPODataTable.ipo
    var rating by IPODataTable.rating
    var status by IPODataTable.sub
    var price by IPODataTable.price
    var gmp by IPODataTable.gmp
    var estListing by IPODataTable.estListing
    var ipoSize by IPODataTable.ipoSize
    var lot by IPODataTable.lot
    var open by IPODataTable.open
    var close by IPODataTable.close
    var boaDate by IPODataTable.boaDate
    var listing by IPODataTable.listing
}

data class IpoDto(
    val ipo: String,
    val rating: String,
    val sub: String,
    val price: Int,
    val gmp: String,
    val estListing: String,
    val ipoSize: String,
    val lot: Int,
    val open: LocalDate?,
    val close: LocalDate?,
    val boaDate: LocalDate?,
    val listing: LocalDate?,
)
