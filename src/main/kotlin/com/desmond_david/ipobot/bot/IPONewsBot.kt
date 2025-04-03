package com.desmond_david.ipobot.bot

import com.desmond_david.ipobot.AppProperties
import com.desmond_david.ipobot.database.IpoDto
import com.desmond_david.ipobot.service.IPOService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot
import org.telegram.telegrambots.abilitybots.api.objects.Ability
import org.telegram.telegrambots.abilitybots.api.objects.Locality
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext
import org.telegram.telegrambots.abilitybots.api.objects.Privacy
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

@Suppress("unused")
class IPONewsBot(
    telegramClient: TelegramClient,
    botUsername: String,
    ipoService: IPOService
) : AbilityBot(telegramClient, botUsername) {

    private val logger = KotlinLogging.logger {}
    private val service: IPOService = ipoService

    override fun creatorId(): Long {
        return AppProperties.BOT_CREATOR_ID
    }

    fun getIpoData(): Ability {
        return Ability.builder()
            .name("getipos")
            .info("Gets IPO data.")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->

                logger.info { "Getting IPO details..." }

                var ipoData = service.getData()
                ipoData = ipoData.subList(0, 5)
                silent.sendMd(convertIpoListToMessageBody(ipoData), ctx.chatId())
            }
            .build()
    }

    fun getTodaysClosingIpos(): Ability {
        return Ability.builder()
            .name("closingtoday")
            .info("Gets a list of IPOs closing today")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                logger.info { "Getting IPOs closing today..." }
                silent.sendMd(
                    convertIpoListToMessageBody(service.getIposClosingOn(LocalDate.now(ZoneId.of("UTC+0530")))),
                    ctx.chatId()
                )
            }
            .build()
    }

    fun getGetIposClosingOn(): Ability {
        return Ability.builder()
            .name("closingon")
            .info("Gets a list of IPOs closing today")
            .input(1)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                logger.info { "Getting IPOs closing on...${ctx.firstArg()}" }
                try {
                    silent.sendMd(
                        convertIpoListToMessageBody(service.getIposClosingOn(LocalDate.parse(ctx.firstArg()))),
                        ctx.chatId()
                    )
                } catch (e: DateTimeParseException) {
                    logger.error(e) { "Could not parse the given date when getting IPOs." }
                    silent.send("Invalid date format. Enter a date in the format yyyy-mm-dd", ctx.chatId())
                }
            }
            .build()
    }

    fun refreshIpoDb(): Ability {
        return Ability.builder()
            .name("refresh")
            .info("Refreshes the IPO database.")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                logger.info { "Refreshing IPO db." }
                silent.send("Refreshing IPO data from ${service.getServiceName()}", ctx.chatId())
                val result = service.saveData()

                if (result != null)
                    silent.send("IPO data refreshed successfully from ${service.getServiceName()}", ctx.chatId())
                else
                    silent.send(
                        "Could not refresh IPO data from ${service.getServiceName()}. Check logs for errors.",
                        ctx.chatId()
                    )
            }
            .build()
    }

    private fun convertIpoListToMessageBody(ipoData: List<IpoDto>): String {
        var message = ""
        for (ipo in ipoData) {
            val entry = """
                        *${ipo.ipo}*
                        
                        - Rating: ${ipo.rating}
                        - Status: ${ipo.status}
                        - GMP: ${ipo.gmp.toString() + "%"}
                        - IPO Size: ${ipo.ipoSize}
                        - Open date: ${ipo.open}
                        - Close date: ${ipo.close}
                        - Listing date: ${ipo.listing}
                    """.trimIndent()

            message += "$entry\n\n"
        }

        return message.ifEmpty { "No IPOs closing on this date." }
    }
}