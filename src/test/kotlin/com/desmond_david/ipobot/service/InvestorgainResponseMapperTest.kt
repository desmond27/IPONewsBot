package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.IpoDto
import org.json.JSONObject
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InvestorgainResponseMapperTest {

    private val mapper = InvestorgainResponseMapper()

    @Test
    fun mapToDto_withAllFields_parsesCorrectly() {
        val json = JSONObject(
            """
            {
              "~orderby1": 5500,
              "Name": "<a href=\"/gmp/\" title=\"Test One IPO\" target=\"_parent\">Test One IPO</a> <span class=\"badge rounded-pill bg-warning d-inline ms-2\" >U</span>",
              "GMP": "&#8377;<b>21</b> (21.65%)<br><small style=\"font-size: 12px; color: #007BFF;\"><b>21-Aug 23:34</b></small>",
              "Fire Rating": "<span style='font-size: 12px;'>&#128293;&#128293;&#128293;&#128293;</span>",
              "Sub": "",
              "Price": "97",
              "IPO Size": "&#8377;772.00 Cr",
              "Lot": "148",
              "~P/E": "22.88",
              "~id": 1385,
              "Open": "26-Aug",
              "Close": "29-Aug",
              "BoA Dt": "1-Sep",
              "Listing": "3-Sep",
              "~Srt_Open": "2025-08-26",
              "~Srt_Close": "2025-08-29",
              "~Srt_BoA_Dt": "2025-09-01",
              "~Str_Listing": "2025-09-03",
              "~urlrewrite_folder_name": "/gmp/test-one-ipo/1385/",
              "~Display_Order": 5500,
              "~Highlight_Row": "color-lightyellow",
              "~IPO_Category": "IPO",
              "~gmp_percent_calc": "21.65",
              "~ipo_name": "Test One IPO"
            }
            """.trimIndent()
        )

        val dto: IpoDto = mapper.mapToDto(json)

        // Name should be taken from "Name" HTML, trimming the trailing status "U"
        assertEquals("Test One IPO", dto.ipo)
        // Fire rating flames should become plain text (4 fire emojis)
        assertEquals("🔥🔥🔥🔥", dto.rating)
        // Sub field is empty -> empty string
        assertEquals("", dto.sub)
        // Price numeric
        assertEquals(97, dto.price)
        // GMP text stripped of HTML
        assertEquals("₹21 (21.65%) 21-Aug 23:34", dto.gmp)
        // IPO Size stripped of HTML entity
        assertEquals("₹772.00 Cr", dto.ipoSize)
        // Lot numeric
        assertEquals(148, dto.lot)
        // Dates parsed from sortable fields
        assertEquals(LocalDate.parse("2025-08-26"), dto.open)
        assertEquals(LocalDate.parse("2025-08-29"), dto.close)
        assertEquals(LocalDate.parse("2025-09-01"), dto.boaDate)
        assertEquals(LocalDate.parse("2025-09-03"), dto.listing)
        // GMP percent
        assertEquals("21.65", dto.gmpPercent)
    }

    @Test
    fun mapToDto_handlesNA_TBD_andEmpty() {
        val json = JSONObject(
            """
            {
              "Name": "<b>Sample IPO U</b>",
              "GMP": "<b>--</b>",
              "Fire Rating": "",
              "Sub": "<span>Open</span>",
              "Price": "NA",
              "IPO Size": "",
              "Lot": "TBD",
              "~Srt_Open": "",
              "~Srt_Close": "",
              "~Srt_BoA_Dt": "",
              "~Str_Listing": "",
              "~gmp_percent_calc": ""
            }
            """.trimIndent()
        )

        val dto = mapper.mapToDto(json)

        assertEquals("Sample IPO", dto.ipo)
        // Empty rating HTML -> empty string
        assertEquals("", dto.rating)
        // Sub should be text content
        assertEquals("Open", dto.sub)
        // NA -> -1
        assertEquals(-1, dto.price)
        // GMP stripped of html showing just --
        assertEquals("--", dto.gmp)
        // Empty IPO Size -> empty string
        assertEquals("", dto.ipoSize)
        // TBD lot -> -1
        assertEquals(-1, dto.lot)
        // Empty date strings -> nulls
        assertNull(dto.open)
        assertNull(dto.close)
        assertNull(dto.boaDate)
        assertNull(dto.listing)
        // Empty percent -> null
        assertNull(dto.gmpPercent)
    }

    @Test
    fun mapToDto_usesFallbackIpoName_whenNameMissingOrBlank() {
        val json = JSONObject(
            """
            {
              "Name": "",
              "~ipo_name": "Fallback IPO Name",
              "GMP": "<b>10</b>",
              "Price": "123",
              "IPO Size": "&#8377;10.00 Cr",
              "Lot": "100"
            }
            """.trimIndent()
        )

        val dto = mapper.mapToDto(json)

        assertEquals("Fallback IPO Name", dto.ipo)
        assertEquals(123, dto.price)
        assertEquals("₹10.00 Cr", dto.ipoSize)
        assertEquals(100, dto.lot)
        assertEquals("10", dto.gmp)
    }
}