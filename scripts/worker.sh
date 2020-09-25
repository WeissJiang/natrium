#!/usr/bin/env bash
cd `dirname $0`
# ---
cd ..
java $JAVA_OPTS -jar  worker/build/libs/worker.jar
