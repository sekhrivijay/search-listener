#!/bin/bash
#
# docker-entrypoint for service

set -e
echo "Executing java"

java ${JAVA_ARGS} -jar search-listener-0.0.1-SNAPSHOT.jar

