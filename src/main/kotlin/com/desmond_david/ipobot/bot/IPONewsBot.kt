package com.desmond_david.ipobot.bot

import com.desmond_david.ipobot.AppProperties
import com.desmond_david.ipobot.database.IpoDto
import com.desmond_david.ipobot.service.ActiveGroupsService
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
    private val activeGroupsService = ActiveGroupsService()

    override fun creatorId(): Long {
        return AppProperties.BOT_CREATOR_ID
    }

    fun startBot(): Ability {
        return Ability.builder()
            .name("start")
            .info("Enables the current chat to receive messages from this bot")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                val chatId = ctx.chatId()
                logger.info { "Activating the bot for chat id: ${ctx.chatId()}" }
                if(!activeGroupsService.isGroupAlreadyActive(chatId)) {
                    activeGroupsService.addToActiveGroups(chatId)
                    silent.send("Bot enabled for this group.", chatId)
                } else {
                    silent.send("Bot is already enabled for this group.", chatId)
                }
            }
            .build()
    }

    fun stopBot(): Ability {
        return Ability.builder()
            .name("stop")
            .info("Disables the current chat from receive messages from this bot")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                val chatId = ctx.chatId()
                logger.info { "Deactivating the bot for chat id: ${ctx.chatId()}" }
                if(activeGroupsService.isGroupAlreadyActive(chatId)) {
                    activeGroupsService.removeActiveGroup(chatId)
                    silent.send("Bot disabled for this group.", chatId)
                } else {
                    silent.send("Bot is already not enabled for this group.", chatId)
                }
            }
            .build()
    }

    fun getIpoData(): Ability {
        return Ability.builder()
            .name("getipos")
            .info("Gets IPO data.")
            .input(0)
            .locality(Locality.GROUP)
            .privacy(Privacy.GROUP_ADMIN)
            .action { ctx: MessageContext ->
                if(canRun(ctx)) {
                    logger.info { "Getting IPO details..." }

                    var ipoData = service.getData()
                    ipoData = ipoData.subList(0, 5)
                    silent.sendMd(convertIpoListToClosingTodayMessageBody(ipoData), ctx.chatId())
                }
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
                if(canRun(ctx)) {
                    refreshIpoData(ctx)
                    logger.info { "Getting IPOs closing today..." }
                    val ipoMessage =
                        convertIpoListToClosingTodayMessageBody(service.getIposClosingOn(LocalDate.now(ZoneId.of("UTC+0530"))))
                    val activeGroupIds = activeGroupsService.getAllActiveGroupIds()
                    activeGroupIds.forEach {
                        silent.sendMd(ipoMessage, it)
                    }
                }
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
                        convertIpoListToClosingTodayMessageBody(service.getIposClosingOn(LocalDate.parse(ctx.firstArg()))),
                        ctx.chatId()
                    )
                } catch (e: DateTimeParseException) {
                    logger.error(e) { "Could not parse the given date when getting IPOs." }
                    silent.send("Invalid date format. Enter a date in the format yyyy-MM-dd", ctx.chatId())
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
                if(canRun(ctx)) {
                    logger.info { "Refreshing IPO db." }
                    silent.send("Refreshing IPO data from ${service.getServiceName()}", ctx.chatId())
                    refreshIpoData(ctx)
                }
            }
            .build()
    }

    private fun refreshIpoData(ctx: MessageContext) {
        val result = service.saveData()

        if (result != null)
            silent.send("IPO data refreshed successfully from ${service.getServiceName()}", ctx.chatId())
        else
            silent.send(
                "Could not refresh IPO data from ${service.getServiceName()}. Check logs for errors.",
                ctx.chatId()
            )
    }

    private fun convertIpoListToClosingTodayMessageBody(ipoData: List<IpoDto>): String {
        var message = ""
        for (ipo in ipoData) {
            val entry = """
                        IPO(s) closing today:
                        
                        *${ipo.ipo}*
                        
                        - Rating: ${ipo.rating}
                        - Status: ${ipo.status}
                        - GMP: ${ipo.gmp.toString() + "%"}
                    """.trimIndent()

            message += "$entry\n\n"
        }

        return message.ifEmpty { "No IPOs closing on this date." }
    }

    private fun canRun(ctx: MessageContext): Boolean {
        if(ctx.chatId() != AppProperties.CONTROL_GROUP_CHAT_ID) {
            silent.send("This command can only be run from the control group.", ctx.chatId())
            return false
        }

        return true
    }
}