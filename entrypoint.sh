#!/bin/sh
set -e

# An init container (platform/apps/demo-spring-app) fetches the RDS master password and
# address from AWS Secrets Manager / SSM Parameter Store and writes them to files on a shared
# emptyDir - never as env vars the init container could pass directly, since init and main
# containers don't share a process environment, only volumes. Optional: a plain `docker run`
# without that init container (e.g. local testing) just falls back to whatever DB_PASSWORD/
# DB_HOST are already set, if anything.
if [ -f /secrets/db-password ]; then
  DB_PASSWORD="$(cat /secrets/db-password)"
  export DB_PASSWORD
fi

if [ -f /secrets/db-address ]; then
  DB_HOST="$(cat /secrets/db-address)"
  export DB_HOST
fi

exec java -jar /app/app.jar
