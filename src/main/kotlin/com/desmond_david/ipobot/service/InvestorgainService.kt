package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.DatabaseHelper
import com.desmond_david.ipobot.database.IPODataTable.close
import com.desmond_david.ipobot.database.IPODataTableDao
import com.desmond_david.ipobot.database.IpoDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.time.LocalDate

private const val SERVICE_NAME = "Investorgain"
private const val SERVICE_URL = "https://webnodejs.investorgain.com/cloud/report/data-read/331/1/2/2025/2024-25/0/all"

class InvestorgainService : IPOService {
    private val logger = KotlinLogging.logger {}

    override fun getServiceName(): String {
        return SERVICE_NAME
    }

    override fun saveData(): Int? {
        logger.info { "Saving data from Investorgain to db..." }
        val ipoDataList = mutableListOf<IpoDto>()
        try {
            val responseJsonObject =
                khttp.get(SERVICE_URL).jsonObject
            val reportTableDataJsonArray = responseJsonObject.get("reportTableData") as JSONArray
            for (entry in reportTableDataJsonArray) {
                val jsonEntry = entry as JSONObject
                val gmpValue = Jsoup.parse(jsonEntry.getString("GMP")).text()
                ipoDataList.add(
                    IpoDto(
                        jsonEntry.getString("IPO"),
                        Jsoup.parse(jsonEntry.getString("Fire Rating")).text(),
                        Jsoup.parse(jsonEntry.getString("Status")).text(),
                        jsonEntry.getInt("Price"),
                        if (gmpValue == "--") -1 else gmpValue.toInt(),
                        Jsoup.parse(jsonEntry.getString("Est Listing")).text(),
                        Jsoup.parse(jsonEntry.getString("IPO Size")).text(),
                        jsonEntry.getString("Lot").toInt(),
                        LocalDate.parse(jsonEntry.getString("~Srt_Open")),
                        LocalDate.parse(jsonEntry.getString("~Srt_Close")),
                        LocalDate.parse(jsonEntry.getString("~Srt_BoA_Dt")),
                        LocalDate.parse(jsonEntry.getString("~Str_Listing")),
                    )
                )

            }
            DatabaseHelper.storeToDb(ipoDataList)
            return reportTableDataJsonArray.length()
        } catch (e: Exception) {
            logger.error(e) { "Error getting data from Investorgain" }
            return null
        }

    }

    override fun getIposClosingOn(localDate: LocalDate): List<IpoDto> {
        val ipoDataList = mutableListOf<IpoDto>()
        transaction {
           IPODataTableDao.find { close eq localDate }.forEach {
                makeIpoDto(ipoDataList, it)
            }
        }
        return ipoDataList
    }


    override fun getData(): List<IpoDto> {
        val ipoDataList = mutableListOf<IpoDto>()
        transaction {
            IPODataTableDao.all().orderBy(Pair(close, SortOrder.DESC)).forEach {
                makeIpoDto(ipoDataList, it)
            }
        }

        return ipoDataList
    }

    private fun makeIpoDto(
        ipoDataList: MutableList<IpoDto>,
        it: IPODataTableDao
    ) {
        ipoDataList.add(
            IpoDto(
                ipo = it.ipo,
                open = it.open,
                close = it.close,
                estListing = it.estListing,
                lot = it.lot,
                gmp = it.gmp,
                price = it.price,
                status = it.status,
                ipoSize = it.ipoSize,
                rating = it.rating,
                boaDate = it.boaDate,
                listing = it.listing,
            )
        )
    }
}