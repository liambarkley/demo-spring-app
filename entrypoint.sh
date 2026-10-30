#!/bin/sh
set -e

# An init container (platform/apps/demo-spring-app) fetches the RDS master password from AWS
# Secrets Manager and writes it to this file on a shared emptyDir - never as an env var the
# init container could pass directly, since init and main containers don't share a process
# environment, only volumes. Optional: a plain `docker run` without that init container (e.g.
# local testing) just falls back to whatever DB_PASSWORD is already set, if anything.
if [ -f /secrets/db-password ]; then
  DB_PASSWORD="$(cat /secrets/db-password)"
  export DB_PASSWORD
fi

exec java -jar /app/app.jar
