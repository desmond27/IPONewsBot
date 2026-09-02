[![Java CI with Maven](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml/badge.svg)](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml)

# IPONewsBot
A Telegram bot that posts information about current and upcoming IPOs. Data currently sourced from [Investorgain](https://www.investorgain.com/).

Add to your Telegram group to try it: https://t.me/IPONewsBot. The bot automatically posts info on IPOs closing everyday at 11 AM IST except weekends.

Please open an issue if you face any bugs or to request enhancements. I will try to address as soon as time permits.

## Available commands:

To avoid too much load on the server, the below commands can only be run by admins of the group where the bot is added.

|Command|Description|
|---|---|
|/start|Enable the bot in your chat.|
|/stop|Disable the bot in your chat.|
|/getipos|Get top 5 latest listed IPOs.|
|/closingtoday|Get a list of IPOs closing today, if any.|
|/closingon yyyy-MM-dd|Get the IPOs closing on the given date in yyyy-MM-dd format (ex. 2026-07-21).|
|/refresh|Updates the latest IPO details in the server db. Can only be run from the [control group](https://github.com/desmond27/IPONewsBot#note-on-control-groups).|

## Building

- Edit the [ipobot.properties](https://github.com/desmond27/IPONewsBot/blob/main/src/main/resources/ipobot.properties) file and add the required details:
  - `DAILY_ALERT_TIME` The time when the bot will post IPO closing information.
  - `DAILY_ALERT_TIMEZONE` The timezone of the daily alert time.
  - `TELEGRAM_ENABLED` Enables the Telegram bot. This must be set to true for Telegram otherwise the bot will not initialize.
  - `MATRIX_ENABLED` Enables Matrix support. This option is currently a no-op and is reserved for future enhancement for Matrix integration.
- Create a Telegram bot using [@Botfather](https://t.me/Botfather) to obtain a bot token and bot username. Also, obtain your bot creator id: The bot creator id is your Telegram id (not handle) this can be obtained from [@userinfobot](https://t.me/userinfobot).
- Edit the [telegram.properties](https://github.com/desmond27/IPONewsBot/blob/main/src/main/resources/telegram.properties) file and add these details. Read the note in the below section about `CONTROL_GROUP_CHAT_ID`.

After setting the appropriate properties, ensure that you have a compatible Java version installed, then simply run `./mvnw clean package` on *nix systems or `mvnw.cmd clean package` on Windows to get the executable JAR in the `target` directory.

## Note on control groups

If you want to host this bot yourself on your own server, you will need to create a separate group in Telegram as a control group. This group will be used for higher-level admin operations only accessible to the bot owner. Currently only the `/refresh` operation is available that can only be executed in the control group. You must configure the control group chat id against the `CONTROL_GROUP_CHAT_ID` property in [ipobot.properties](https://github.com/desmond27/IPONewsBot/blob/main/src/main/resources/ipobot.properties).

## Acknowledgements

This project uses [TelegramBots](https://github.com/rubenlagus/TelegramBots)
