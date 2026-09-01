package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.DatabaseHelper
import com.desmond_david.ipobot.database.IPODataTable.close
import com.desmond_david.ipobot.database.IPODataTableDao
import com.desmond_david.ipobot.database.IpoDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDate

private const val SERVICE_NAME = "Investorgain"
private const val SERVICE_URL = "https://webnodejs.investorgain.com/cloud/v2/report/data-read/331/1/2/2025/2024-25/0/all"

class InvestorgainService(
    private val client: OkHttpClient,
    private val mapper: ObjectMapper
) : IPOService {
    private val logger = KotlinLogging.logger {}

    override fun getServiceName(): String {
        return SERVICE_NAME
    }

    override fun saveData(): Int? {
        logger.info { "Saving data from Investorgain to db..." }

        val ipoDataList = mutableListOf<IpoDto>()

        try {
            val request = Request.Builder().url(SERVICE_URL).build()

            client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) throw IOException("Unexpected HTTP code ${'$'}{response.code}")

                val bodyString = response.body?.string() ?: throw IOException("Empty response body")

                // Use Jackson to parse JSON instead of org.json
                val rootNode: JsonNode = mapper.readTree(bodyString)
                val reportTableDataJsonArray = rootNode.get("reportTableData")

                for (entry in reportTableDataJsonArray) {
                    logger.debug { "Reading entry: $entry" }

                    // Convert JsonNode to IpoDto using the mapper
                    val dto = InvestorgainResponseMapper().mapToDto(entry)
                    ipoDataList.add(dto)
                }

                DatabaseHelper.storeToDb(ipoDataList)

                // Return number of processed entries if array, otherwise 0
                return if (reportTableDataJsonArray.isArray) reportTableDataJsonArray.size() else 0
            }
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

    fun mapToDto(json: JsonNode): IpoDto {
        // Helper to parse HTML fragments if any
        fun parseHtml(value: String?): String =
            if (value.isNullOrBlank()) "" else Jsoup.parse(value).text()

        // Parse integer from string with default fallback
        fun parseIntOrDefault(raw: String?, default: Int = -1): Int {
            if (raw.isNullOrBlank()) return default
            val cleaned = raw.filter { it.isDigit() }
            return cleaned.toIntOrNull() ?: default
        }

        // Parse date from string key
        fun parseDate(key: String): LocalDate? {
            val v = json.get(key)?.asText()
            return if (v.isNullOrBlank()) null else LocalDate.parse(v)
        }

        val nameFromHtml = parseHtml(json.get("Name")?.asText()).substringBeforeLast(" ").trim()
        val fallbackName = parseHtml(json.get("~ipo_name")?.asText())
        val ipoName = nameFromHtml.ifBlank { fallbackName }

        val rating = parseHtml(json.get("Rating")?.asText())
        val sub = parseHtml(json.get("Sub")?.asText())

        val priceRaw = json.get("Price")?.asText() ?: ""
        val price = if (priceRaw.equals("NA", ignoreCase = true)) -1 else parseIntOrDefault(priceRaw, -1)

        val gmp = parseHtml(json.get("GMP")?.asText())
        val ipoSize = parseHtml(json.get("IPO Size")?.asText())

        val lotRaw = json.get("Lot")?.asText() ?: ""
        val lot = if (lotRaw.equals("TBD", ignoreCase = true)) -1 else parseIntOrDefault(lotRaw, -1)

        val open = parseDate("~Srt_Open")
        val close = parseDate("~Srt_Close")
        val boaDate = parseDate("~Srt_BoA_Dt")
        val listing = parseDate("~Str_Listing")

        val gmpPercent = json.get("~gmp_percent_calc")?.asText()?.takeIf { it.isNotBlank() }

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
