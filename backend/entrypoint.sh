#!/usr/bin/env bash
set -euo pipefail

echo "Running Alembic migrations..."
alembic upgrade head

echo "Starting EventBuzz API..."
exec uvicorn app.main:app \
    --host 0.0.0.0 \
    --port 8000 \
    --workers "${UVICORN_WORKERS:-1}" \
    --log-level "${LOG_LEVEL:-info}"
