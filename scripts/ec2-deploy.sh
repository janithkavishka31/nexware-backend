#!/usr/bin/env bash
# Manual deploy on EC2 (same steps CI uses). Run from repo root on the server.
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ ! -f .env ]]; then
  echo "Missing .env — copy from .env.example and fill Neon/JWT values first."
  exit 1
fi

git pull origin main
docker compose build --no-cache backend
docker compose up -d
docker image prune -f

echo "Health:"
curl -fsS "http://localhost:${SERVER_PORT:-8080}/actuator/health"
echo
echo "OpenAPI server URL (expect https):"
curl -fsS "http://localhost:${SERVER_PORT:-8080}/api-docs" | grep -o '"url":"[^"]*"' | head -1
