#!/usr/bin/env bash
curl -X POST -H "X-Token: $nanoToken" "https://nano-bot.herokuapp.com/api/telegram/setWebhook"
