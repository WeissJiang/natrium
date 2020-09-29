#!/usr/bin/env bash
cd `dirname $0`
# ---
cd ..
java $JAVA_OPTS -XX:+UseZGC -jar web/build/libs/web.jar
