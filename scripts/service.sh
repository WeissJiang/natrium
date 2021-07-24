#!/usr/bin/env bash
cd "$(dirname "$0")" || exit
# ---
cd ..
java $JAVA_OPTS -XX:+UseZGC -jar service/build/libs/service.jar "$@"
