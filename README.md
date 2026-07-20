[![Java CI with Maven](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml/badge.svg)](https://github.com/desmond27/IPONewsBot/actions/workflows/maven.yml)

# IPONewsBot
A Telegram bot that posts information about current and upcoming IPOs. Data currently sourced from Investorgain.

Add to your Telegram group to try it: https://t.me/IPONewsBot. The bot automatically posts info on IPOs closing everyday at 11 AM IST except weekends.

Please open an issue if you face any issues or to request enhancements.

## Available commands:

To avoid too much load on the server, the below commands can only be run by admins of the group where the bot is added.

|Command|Description|
|---|---|
|/start|Enable the bot in your chat.|
|/stop|Disable the bot in your chat.|
|/stop|Disable the bot in your chat.|
|/getipos|Get top 5 latest listed IPOs.|
|/closingtoday|Get a list of IPOs closing today, if any.|
|/closingon yyyy-MM-dd|Get the IPOs closing on the given date in yyyy-MM-dd format (ex. 2026-07-21).|
|/refresh|Updates the latest IPO details in the server db. Can only be run from the control group.|
