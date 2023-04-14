#!/usr/bin/env bash

set -eo pipefail

echo "Starting up containers..."
docker-compose up -d --remove-orphans
echo "Done"

echo "Migrating database..."
