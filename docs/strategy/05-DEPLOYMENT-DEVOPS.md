# 05 — Deployment & DevOps

---

## Infrastructure Overview

```
+----------------------------------------------------+
|                   PRODUCTION VPS                    |
|                  (Hetzner / DO)                     |
|                                                     |
|  +-----------------------------------------------+ |
|  |              Docker Compose Stack              | |
|  |                                                | |
|  |  +----------+  +----------+  +-----------+    | |
|  |  | Caddy    |  | FastAPI  |  | Keycloak  |    | |
|  |  | (Proxy)  |  | (App)   |  | (Auth)    |    | |
|  |  | :80/:443 |  | :8000   |  | :8080     |    | |
|  |  +----+-----+  +----+----+  +-----+-----+    | |
|  |       |              |             |           | |
|  |  +----v-----+  +----v----+  +-----v-----+    | |
|  |  | Meili-   |  | Postgres|  | Redis     |    | |
|  |  | search   |  | +PostGIS|  | (Valkey)  |    | |
|  |  | :7700    |  | :5432   |  | :6379     |    | |
|  |  +----------+  +---------+  +-----------+    | |
|  |                                                | |
|  |  +----------+  +----------+  +-----------+    | |
|  |  | MinIO    |  | Prometheus|  | Grafana  |    | |
|  |  | (S3)     |  | :9090    |  | :3000    |    | |
|  |  | :9000    |  +----------+  +-----------+    | |
|  |  +----------+                                  | |
|  +-----------------------------------------------+ |
+----------------------------------------------------+
```

---

## Docker Compose (MVP)

```yaml
# docker-compose.yml
version: "3.9"

services:
  # --- Reverse Proxy ---
  caddy:
    image: caddy:2-alpine
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile
      - caddy_data:/data
      - caddy_config:/config
    depends_on:
      - api
      - keycloak

  # --- Application API ---
  api:
    build:
      context: ./backend
      dockerfile: Dockerfile
    restart: unless-stopped
    environment:
      - DATABASE_URL=postgresql+asyncpg://eventbuzz:${DB_PASSWORD}@postgres:5432/eventbuzz
      - REDIS_URL=redis://redis:6379/0
      - MEILISEARCH_URL=http://meilisearch:7700
      - MEILISEARCH_KEY=${MEILI_MASTER_KEY}
      - KEYCLOAK_URL=http://keycloak:8080
      - KEYCLOAK_REALM=eventbuzz
      - MINIO_ENDPOINT=minio:9000
      - MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}
      - MINIO_SECRET_KEY=${MINIO_SECRET_KEY}
      - ENVIRONMENT=production
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      meilisearch:
        condition: service_started
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/api/v1/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # --- Database ---
  postgres:
    image: postgis/postgis:16-3.4-alpine
    restart: unless-stopped
    environment:
      - POSTGRES_DB=eventbuzz
      - POSTGRES_USER=eventbuzz
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U eventbuzz"]
      interval: 10s
      timeout: 5s
      retries: 5

  # --- Cache ---
  redis:
    image: valkey/valkey:8-alpine
    restart: unless-stopped
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "valkey-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # --- Search ---
  meilisearch:
    image: getmeili/meilisearch:v1.11
    restart: unless-stopped
    environment:
      - MEILI_MASTER_KEY=${MEILI_MASTER_KEY}
      - MEILI_ENV=production
    volumes:
      - meili_data:/meili_data

  # --- Auth ---
  keycloak:
    image: quay.io/keycloak/keycloak:26.0
    restart: unless-stopped
    command: start
    environment:
      - KC_DB=postgres
      - KC_DB_URL=jdbc:postgresql://postgres:5432/eventbuzz
      - KC_DB_USERNAME=eventbuzz
      - KC_DB_PASSWORD=${DB_PASSWORD}
      - KC_HOSTNAME_STRICT=false
      - KC_PROXY_HEADERS=xforwarded
      - KEYCLOAK_ADMIN=${KC_ADMIN_USER}
      - KEYCLOAK_ADMIN_PASSWORD=${KC_ADMIN_PASSWORD}
    depends_on:
      postgres:
        condition: service_healthy

  # --- Object Storage ---
  minio:
    image: minio/minio:latest
    restart: unless-stopped
    command: server /data --console-address ":9001"
    environment:
      - MINIO_ROOT_USER=${MINIO_ACCESS_KEY}
      - MINIO_ROOT_PASSWORD=${MINIO_SECRET_KEY}
    volumes:
      - minio_data:/data

  # --- Monitoring ---
  prometheus:
    image: prom/prometheus:v2.54.0
    restart: unless-stopped
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus

  grafana:
    image: grafana/grafana:11.2.0
    restart: unless-stopped
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana

volumes:
  caddy_data:
  caddy_config:
  postgres_data:
  redis_data:
  meili_data:
  minio_data:
  prometheus_data:
  grafana_data:
```

---

## Caddyfile (Reverse Proxy)

```
# Caddyfile
{
    email admin@eventbuzz.app
}

api.eventbuzz.app {
    # API routes
    handle /api/* {
        reverse_proxy api:8000
    }

    # Auth routes
    handle /auth/* {
        reverse_proxy keycloak:8080
    }

    # Search (internal, proxied through API in production)
    # Direct access disabled for security

    # Storage (public read)
    handle /storage/* {
        reverse_proxy minio:9000
    }

    # Monitoring (restricted)
    handle /grafana/* {
        # Add IP allowlist or basic auth in production
        reverse_proxy grafana:3000
    }
}
```

---

## Backend Dockerfile

```dockerfile
# backend/Dockerfile
FROM python:3.12-slim AS base

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libpq-dev gcc curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install Python dependencies
COPY pyproject.toml ./
RUN pip install --no-cache-dir -e ".[prod]"

# Copy application code
COPY app/ ./app/
COPY alembic/ ./alembic/
COPY alembic.ini ./

# Run migrations and start server
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh

EXPOSE 8000

ENTRYPOINT ["./entrypoint.sh"]
```

```bash
#!/bin/bash
# backend/entrypoint.sh
set -e

echo "Running database migrations..."
alembic upgrade head

echo "Starting FastAPI server..."
exec uvicorn app.main:app \
    --host 0.0.0.0 \
    --port 8000 \
    --workers 4 \
    --proxy-headers \
    --forwarded-allow-ips='*'
```

---

## Environment Configuration

```bash
# .env.example (NEVER commit actual .env)
# Database
DB_PASSWORD=change-me-strong-password

# Meilisearch
MEILI_MASTER_KEY=change-me-meili-key

# Keycloak
KC_ADMIN_USER=admin
KC_ADMIN_PASSWORD=change-me-kc-password

# MinIO
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=change-me-minio-secret

# Grafana
GRAFANA_PASSWORD=change-me-grafana-password

# App
APP_SECRET_KEY=change-me-app-secret
ENVIRONMENT=production
```

---

## CI/CD Pipeline (GitHub Actions)

### Android Build & Test

```yaml
# .github/workflows/android.yml
name: Android CI

on:
  push:
    branches: [main, develop]
    paths: ['android/**']
  pull_request:
    branches: [main]
    paths: ['android/**']

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - name: Run ktlint
        working-directory: android
        run: ./gradlew ktlintCheck

  test:
    runs-on: ubuntu-latest
    needs: lint
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: gradle-${{ hashFiles('**/*.gradle.kts') }}
      - name: Run unit tests
        working-directory: android
        run: ./gradlew testDebugUnitTest
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: android/app/build/reports/tests/

  build:
    runs-on: ubuntu-latest
    needs: test
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - name: Build debug APK
        working-directory: android
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: android/app/build/outputs/apk/debug/app-debug.apk
```

### Backend CI

```yaml
# .github/workflows/backend.yml
name: Backend CI

on:
  push:
    branches: [main, develop]
    paths: ['backend/**']
  pull_request:
    branches: [main]
    paths: ['backend/**']

jobs:
  lint-and-test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgis/postgis:16-3.4-alpine
        env:
          POSTGRES_DB: eventbuzz_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: "3.12"
      - name: Install dependencies
        working-directory: backend
        run: pip install -e ".[dev]"
      - name: Run ruff (linter)
        working-directory: backend
        run: ruff check .
      - name: Run ruff (formatter)
        working-directory: backend
        run: ruff format --check .
      - name: Run tests
        working-directory: backend
        env:
          DATABASE_URL: postgresql+asyncpg://test:test@localhost:5432/eventbuzz_test
        run: pytest --cov=app --cov-report=xml
      - name: Upload coverage
        uses: actions/upload-artifact@v4
        with:
          name: coverage
          path: backend/coverage.xml

  docker:
    runs-on: ubuntu-latest
    needs: lint-and-test
    steps:
      - uses: actions/checkout@v4
      - name: Build Docker image
        working-directory: backend
        run: docker build -t eventbuzz-api:${{ github.sha }} .
```

### Deployment Pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/checkout@v4

      - name: Deploy to server
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/eventbuzz
            git pull origin main
            docker compose pull
            docker compose build api
            docker compose up -d --remove-orphans
            docker compose exec api alembic upgrade head
            echo "Deployment complete"

      - name: Health check
        run: |
          sleep 10
          curl -f https://api.eventbuzz.app/api/v1/health || exit 1
```

---

## Deployment Steps (First-Time Setup)

### 1. Provision Server

```bash
# On a fresh Ubuntu 22.04 VPS (Hetzner CX21 — 2 vCPU, 4GB RAM, ~$5/mo)

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# Install Docker Compose plugin
sudo apt install docker-compose-plugin

# Create app directory
sudo mkdir -p /opt/eventbuzz
sudo chown $USER:$USER /opt/eventbuzz
```

### 2. Clone & Configure

```bash
cd /opt/eventbuzz
git clone https://github.com/your-username/eventbuzz.git .

# Create environment file
cp .env.example .env
# Edit .env with production values (use strong passwords)
nano .env
```

### 3. Launch Stack

```bash
# Start all services
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f api

# Run initial migrations
docker compose exec api alembic upgrade head

# Seed categories
docker compose exec api python -m app.scripts.seed_categories
```

### 4. Configure DNS

Point your domain to the server IP:
```
A    api.eventbuzz.app    -> YOUR_SERVER_IP
```

Caddy automatically provisions Let's Encrypt TLS certificates.

### 5. Configure Keycloak

1. Open `https://api.eventbuzz.app/auth`
2. Log in with admin credentials
3. Create realm `eventbuzz`
4. Create client `eventbuzz-android` (public, PKCE)
5. Configure redirect URIs for mobile app
6. Enable email/password registration
7. (Optional) Configure social identity providers

---

## Backup Strategy

```bash
# Automated daily backup script
#!/bin/bash
# /opt/eventbuzz/scripts/backup.sh

BACKUP_DIR="/opt/backups/eventbuzz"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# PostgreSQL dump
docker compose exec -T postgres pg_dump -U eventbuzz eventbuzz \
    | gzip > "$BACKUP_DIR/db_$DATE.sql.gz"

# MinIO data (if not using external S3)
docker compose exec -T minio mc mirror /data "$BACKUP_DIR/minio_$DATE/"

# Keep last 7 daily backups
find $BACKUP_DIR -type f -mtime +7 -delete

echo "Backup completed: $DATE"
```

Add to crontab: `0 3 * * * /opt/eventbuzz/scripts/backup.sh`

---

## Monitoring Setup

### Prometheus Targets

```yaml
# monitoring/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'fastapi'
    static_configs:
      - targets: ['api:8000']
    metrics_path: /metrics

  - job_name: 'caddy'
    static_configs:
      - targets: ['caddy:2019']

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
```

### Key Alerts (Grafana)

| Alert | Condition | Severity |
|-------|-----------|----------|
| API Down | Health check fails for 2 min | Critical |
| High Latency | p95 > 2s for 5 min | Warning |
| DB Connections | Pool > 80% | Warning |
| Disk Space | > 85% used | Warning |
| Error Rate | 5xx > 1% for 5 min | Critical |

---

## Resource Requirements

### MVP (< 1K users)

| Service | RAM | CPU | Disk |
|---------|-----|-----|------|
| FastAPI | 256MB | 0.25 | - |
| PostgreSQL | 512MB | 0.5 | 5GB |
| Redis | 64MB | 0.1 | 100MB |
| Meilisearch | 256MB | 0.25 | 1GB |
| Keycloak | 512MB | 0.5 | 500MB |
| MinIO | 128MB | 0.1 | 5GB |
| Caddy | 32MB | 0.1 | - |
| Monitoring | 256MB | 0.2 | 2GB |
| **Total** | **~2GB** | **~2 vCPU** | **~14GB** |

Fits on a single 4GB VPS ($5-10/month).

---

*Next: [06 — Governance & Security](./06-GOVERNANCE-SECURITY.md)*
