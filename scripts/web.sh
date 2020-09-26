#!/usr/bin/env bash
cd `dirname $0`
# ---
cd ..
java $JAVA_OPTS -jar web/build/libs/web.jar --spring.profiles.active=prod
