package com.desmond_david.ipobot.bot

import com.desmond_david.ipobot.bot.telegram.TelegramBot
import com.desmond_david.ipobot.database.IpoDto
import com.desmond_david.ipobot.service.ActiveGroupsService
import com.desmond_david.ipobot.service.InvestorgainService
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDate
import java.util.Random
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramBotTest {

    @Test
    fun sendDefaultMessageIfNoIposFound() {
        val telegramBot = TelegramBot(
            Mockito.mock(TelegramClient::class.java),
            "test-username" + Random().nextInt().toString(),
            Mockito.mock(InvestorgainService::class.java),
            Mockito.mock(ActiveGroupsService::class.java),
        )
        val messageBody = telegramBot.convertIpoListToMessageBody(listOf(), null)
        assertEquals("No IPOs closing on this date.", messageBody)
    }

    @ParameterizedTest
    @MethodSource("testIpoData")
    fun convertIpoListToMessageBody(date: String?, includeClosingDate: Boolean, expectedMessage: String) {
        val telegramBot = TelegramBot(
            Mockito.mock(TelegramClient::class.java),
            "test-username" + Random().nextInt().toString(),
            Mockito.mock(InvestorgainService::class.java),
            Mockito.mock(ActiveGroupsService::class.java),
        )
        val messageBody = telegramBot.convertIpoListToMessageBody(getTestIpoData(), date, includeClosingDate)
        assertEquals(expectedMessage, messageBody)
    }

    private fun getTestIpoData(): List<IpoDto> {
        return listOf(
            IpoDto(
                "test-name",
                "****",
                "Upcoming",
                100,
                "60.5",
                "100",
                1000,
                LocalDate.now(),
                LocalDate.of(2025, 4, 5),
                LocalDate.now(),
                LocalDate.now(),
                "10.00"
            ),
            IpoDto(
                "test-name-2",
                "***",
                "Open",
                200,
                "65.5",
                "200",
                1500,
                LocalDate.now(),
                LocalDate.of(2025, 5, 4),
                LocalDate.now(),
                LocalDate.now(),
                "15.00"
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
                - Sub: Upcoming
                - GMP: 10.00%
                - Closing date: 2025-04-05

                *test-name-2*

                - Rating: ***
                - Sub: Open
                - GMP: 15.00%
                - Closing date: 2025-05-04
                
            """.trimIndent()
            val messageBodyWithNoCloseDate = """
                *test-name*

                - Rating: ****
                - Sub: Upcoming
                - GMP: 10.00%

                *test-name-2*

                - Rating: ***
                - Sub: Open
                - GMP: 15.00%

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