[![Java CI with Maven](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml/badge.svg)](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml)

# IPONewsBot
A Telegram bot that posts information about current and upcoming IPOs. Data currently sourced from Investorgain.

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
|/refresh|Updates the latest IPO details in the server db. Can only be run from the control group.|

# Building

Edit the [ipobot.properties](https://github.com/desmond27/IPONewsBot/blob/main/src/main/resources/ipobot.properties) file and add the required details. You may need to create your own bot using [@Botfather](https://t.me/Botfather) to obtain the bot token and bot username. The bot creator id is your Telegram id (not handle) this can be obtained from [@userinfobot](https://t.me/userinfobot).

After setting the appropriate properties, ensure that you have a compatible Java version installed, then simply run `./mvnw clean package` on *nix systems or `mvnw.cmd clean package` on Windows to get the executable JAR.

## Note on control groups

If you want to host this bot yourself on your own server, you will need to create a separate group in Telegram as a control group. This group will be used for higher-level admin operations. Currently only the `/refresh` operation is available that can only be executed in the control group. You must configure the control group chat id against the `CONTROL_GROUP_CHAT_ID` property in [ipobot.properties](https://github.com/desmond27/IPONewsBot/blob/main/src/main/resources/ipobot.properties).
