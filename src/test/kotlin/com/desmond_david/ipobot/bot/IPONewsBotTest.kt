package com.desmond_david.ipobot.bot

import com.desmond_david.ipobot.database.IpoDto
import com.desmond_david.ipobot.service.InvestorgainService
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals

class IPONewsBotTest {

    @Test
    fun sendDefaultMessageIfNoIposFound() {
        val ipoNewsBot = IPONewsBot(
            Mockito.mock(TelegramClient::class.java),
            "test-username" + java.util.Random().nextInt().toString(),
            Mockito.mock(InvestorgainService::class.java)
        )
        val messageBody = ipoNewsBot.convertIpoListToMessageBody(listOf(), null)
        assertEquals("No IPOs closing on this date.", messageBody)
    }

    @ParameterizedTest
    @MethodSource("testIpoData")
    fun convertIpoListToMessageBody(date: String?, includeClosingDate: Boolean, expectedMessage: String) {
        val ipoNewsBot = IPONewsBot(
            Mockito.mock(TelegramClient::class.java),
            "test-username" + java.util.Random().nextInt().toString(),
            Mockito.mock(InvestorgainService::class.java)
        )
        val messageBody = ipoNewsBot.convertIpoListToMessageBody(getTestIpoData(), date, includeClosingDate)
        assertEquals(expectedMessage, messageBody)
    }

    private fun getTestIpoData(): List<IpoDto> {
        return listOf(
            IpoDto(
                "test-name",
                "****",
                "Upcoming",
                100,
                60.5,
                "777 (15%)",
                "100 Cr",
                1000,
                LocalDate.now(),
                LocalDate.of(2025, 4, 5),
                LocalDate.now(),
                LocalDate.now()
            ),
            IpoDto(
                "test-name-2",
                "***",
                "Open",
                200,
                65.5,
                "999 (10%)",
                "200 Cr",
                1500,
                LocalDate.now(),
                LocalDate.of(2025, 5, 4),
                LocalDate.now(),
                LocalDate.now()
            ),
        )
    }

    companion object {
        @JvmStatic
        fun testIpoData(): Stream<Arguments> {

            val ipoClosingToday = "IPO(s) closing today:\n\n"
            val ipoClosingOn = "IPO(s) closing on 2025-03-01\n\n"

            val messageBodyWithCloseDate = """
                *test-name*

                - Rating: ****
                - Status: Upcoming
                - GMP: 15%
                - Closing date: 2025-04-05

                *test-name-2*

                - Rating: ***
                - Status: Open
                - GMP: 10%
                - Closing date: 2025-05-04
                
            """.trimIndent()
            val messageBodyWithNoCloseDate = """
                *test-name*

                - Rating: ****
                - Status: Upcoming
                - GMP: 15%

                *test-name-2*

                - Rating: ***
                - Status: Open
                - GMP: 10%

            """.trimIndent()

            return Stream.of(
                Arguments.of(null, true, ipoClosingToday + messageBodyWithCloseDate),
                Arguments.of(null, false, ipoClosingToday + messageBodyWithNoCloseDate),
                Arguments.of("2025-03-01", true, ipoClosingOn + messageBodyWithCloseDate),
                Arguments.of("2025-03-01", false, ipoClosingOn + messageBodyWithNoCloseDate)
            )
        }
    }
}