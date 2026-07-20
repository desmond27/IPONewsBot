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
private const val SERVICE_URL = "https://webnodejs.investorgain.com/cloud/v2/report/data-read/331/1/2/2025/2024-25/0/all"

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
                logger.debug { "Reading entry: $entry" }

                ipoDataList.add(
                    InvestorgainResponseMapper().mapToDto(entry as JSONObject)
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

    override fun getIpoClosingNext(): IpoDto? {
        TODO("Not yet implemented")
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
                lot = it.lot,
                gmp = it.gmp,
                price = it.price,
                sub = it.status,
                ipoSize = it.ipoSize,
                rating = it.rating,
                boaDate = it.boaDate,
                listing = it.listing,
                gmpPercent = it.gmpPercent
            )
        )
    }
}

class InvestorgainResponseMapper {

    fun mapToDto(json: JSONObject): IpoDto {
        fun parseHtml(value: String?): String =
            if (value.isNullOrBlank()) "" else Jsoup.parse(value).text()

        fun parseIntOrDefault(raw: String?, default: Int = -1): Int {
            if (raw.isNullOrBlank()) return default
            val cleaned = raw.filter { it.isDigit() }
            return cleaned.toIntOrNull() ?: default
        }

        fun parseDate(key: String): LocalDate? {
            val v = json.optString(key)
            return if (v.isNullOrBlank()) null else LocalDate.parse(v)
        }

        val nameFromHtml = parseHtml(json.optString("Name")).substringBeforeLast(" ").trim()
        val fallbackName = parseHtml(json.optString("~ipo_name"))
        val ipoName = nameFromHtml.ifBlank { fallbackName }

        val rating = parseHtml(json.optString("Rating"))
        val sub = parseHtml(json.optString("Sub"))

        val priceRaw = json.optString("Price")
        val price = if (priceRaw.equals("NA", ignoreCase = true)) -1 else parseIntOrDefault(priceRaw, -1)

        val gmp = parseHtml(json.optString("GMP"))
        val ipoSize = parseHtml(json.optString("IPO Size"))

        val lotRaw = json.optString("Lot")
        val lot = if (lotRaw.equals("TBD", ignoreCase = true)) -1 else parseIntOrDefault(lotRaw, -1)

        val open = parseDate("~Srt_Open")
        val close = parseDate("~Srt_Close")
        val boaDate = parseDate("~Srt_BoA_Dt")
        val listing = parseDate("~Str_Listing")

        val gmpPercent = json.optString("~gmp_percent_calc").takeIf { it.isNotBlank() }

        return IpoDto(
            ipo = ipoName,
            rating = rating,
            sub = sub,
            price = price,
            gmp = gmp,
            ipoSize = ipoSize,
            lot = lot,
            open = open,
            close = close,
            boaDate = boaDate,
            listing = listing,
            gmpPercent = gmpPercent
        )
    }
}
