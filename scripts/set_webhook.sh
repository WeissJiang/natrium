#!/usr/bin/env bash
cd `dirname $0`
# ---
curl -X POST -H "X-Token: $nanoToken" "https://nano-bot.herokuapp.com/api/telegram/setWebhook"
