#!/usr/bin/env bash
set -euo pipefail
cd ~/nexware-backend
git fetch origin main
git reset --hard origin/main
git log -1 --oneline
docker compose build --no-cache backend
docker compose up -d
sleep 45
echo "--- env ---"
docker compose exec backend env | grep NEXWARE_OPENAPI || true
echo "--- api-docs server ---"
curl -s http://localhost:8080/api-docs | grep -o '"url":"[^"]*"' | head -3
echo "--- health endpoint ---"
curl -s http://localhost:8080/api-docs | grep -o '/api/v1/health' | head -1 || true
echo "--- health ---"
curl -s http://localhost:8080/actuator/health
echo
