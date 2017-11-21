#!/bin/bash
#
# docker-entrypoint for service

set -e
echo "Executing java"

java ${JAVA_ARGS} -jar search-listener-1.0-SNAPSHOT.jar

