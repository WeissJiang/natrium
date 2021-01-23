#!/usr/bin/env bash
cd "$(dirname "$0")" || exit
# ---
export nanoApiKey=
cd ..
curl -X POST -H "X-Token: $nanoApiKey" "https://nano-bot.herokuapp.com/api/telegram/setWebhook"
