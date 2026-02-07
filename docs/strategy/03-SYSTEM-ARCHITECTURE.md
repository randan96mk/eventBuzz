# 03 — System Architecture

---

## High-Level Architecture Diagram

```
+------------------------------------------------------------------+
|                        ANDROID CLIENT                             |
|  +--------------------+  +-------------------+  +--------------+ |
|  |   Presentation     |  |     Domain        |  |    Data      | |
|  |   (Compose UI)     |  |   (Use Cases)     |  | (Repository) | |
|  |   + ViewModels     |  |   + Models        |  | + Remote DS  | |
|  |                    |  |   + Repository     |  | + Local DS   | |
|  |                    |  |     Interfaces     |  | + Cache      | |
|  +--------+-----------+  +---------+---------+  +------+-------+ |
|           |                        |                    |         |
|           +------------------------+--------------------+         |
|                            |                                      |
+----------------------------+--------------------------------------+
                             | HTTPS (REST JSON)
                             v
+------------------------------------------------------------------+
|                      API GATEWAY / REVERSE PROXY                  |
|                         (Caddy / Traefik)                         |
+---------------------------+--------------------------------------+
                            |
          +-----------------+------------------+
          |                 |                  |
+---------v------+ +-------v--------+ +-------v--------+
|   Auth Service | | Event Service  | | Search Service |
|   (Keycloak)   | | (FastAPI)      | | (Meilisearch)  |
+--------+-------+ +-------+--------+ +-------+--------+
         |                  |                  |
         |          +-------v--------+         |
         |          |   PostgreSQL   |<--------+
         |          |   + PostGIS    |  (sync)
         |          +-------+--------+
         |                  |
         v                  v
+--------+-------+ +-------+--------+
| User Store     | | Object Storage |
| (Keycloak DB)  | | (MinIO / S3)   |
+----------------+ +----------------+
```

---

## Android App Architecture (Clean Architecture + MVVM)

### Layer Diagram

```
+---------------------------------------------------+
|              Presentation Layer                    |
|  Screens (Composables) <-> ViewModels             |
|  - MapScreen, ListScreen, DetailScreen, etc.      |
|  - UI State (StateFlow), UI Events                |
+------------------------+--------------------------+
                         |  depends on
+------------------------v--------------------------+
|                Domain Layer                        |
|  Use Cases / Interactors                           |
|  - GetNearbyEventsUseCase                         |
|  - SearchEventsUseCase                            |
|  - GetEventDetailUseCase                          |
|  - FilterEventsUseCase                            |
|  Domain Models (Event, Location, Category, User)  |
|  Repository Interfaces                            |
+------------------------+--------------------------+
                         |  depends on
+------------------------v--------------------------+
|                  Data Layer                        |
|  Repository Implementations                       |
|  - EventRepositoryImpl                            |
|  Remote Data Sources (Retrofit/Ktor Client)       |
|  Local Data Sources (Room Database)               |
|  Cache Manager                                    |
+---------------------------------------------------+
```

### Module Structure

```
eventbuzz-android/
├── app/                          # Application module
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── kotlin/.../
│   │       ├── EventBuzzApp.kt   # Application class
│   │       ├── MainActivity.kt
│   │       └── di/               # Hilt DI modules
│   └── build.gradle.kts
│
├── core/
│   ├── common/                   # Shared utilities, extensions
│   ├── ui/                       # Design system, theme, shared composables
│   ├── network/                  # Retrofit setup, interceptors, API models
│   ├── database/                 # Room DB, DAOs, entities
│   └── datastore/                # Preferences (DataStore)
│
├── feature/
│   ├── map/                      # Map screen + ViewModel
│   ├── list/                     # Event list screen + ViewModel
│   ├── detail/                   # Event detail screen + ViewModel
│   ├── search/                   # Search + filters screen + ViewModel
│   ├── auth/                     # Login, signup, guest flow
│   └── profile/                  # Profile + settings
│
├── domain/
│   ├── model/                    # Domain models
│   ├── repository/               # Repository interfaces
│   └── usecase/                  # Use case classes
│
├── build.gradle.kts              # Root build file
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml        # Version catalog
```

---

## Technology Stack (Detailed Justification)

### Android Client

| Component | Choice | License | Why This Over Alternatives |
|-----------|--------|---------|--------------------------|
| Language | **Kotlin** | Apache 2.0 | Only modern option for Android. Java is legacy. |
| UI | **Jetpack Compose** | Apache 2.0 | Declarative, less boilerplate than XML Views. Official Google direction. |
| Navigation | **Compose Navigation** | Apache 2.0 | Integrated with Compose. Type-safe routes. |
| DI | **Hilt** | Apache 2.0 | Simpler than Dagger, official Android recommendation. Koin is lighter but less compile-time safety. |
| Networking | **Ktor Client** | Apache 2.0 | Kotlin-native, multiplatform-ready. Retrofit also viable but Java-based. |
| JSON | **Kotlinx Serialization** | Apache 2.0 | Kotlin-native, no reflection. Moshi/Gson are alternatives. |
| Local DB | **Room** | Apache 2.0 | Official Android ORM. SQLDelight is alternative for KMP. |
| Preferences | **DataStore** | Apache 2.0 | Replaces SharedPreferences, coroutine-based. |
| Images | **Coil** | Apache 2.0 | Kotlin-first, lightweight. Glide is alternative. |
| Maps | **MapLibre GL Native** | BSD-3 | Open-source Mapbox fork. No API key cost. Google Maps requires billing. |
| Map Tiles | **OpenStreetMap** | ODbL | Free, community-maintained. Best open-source option. |
| Async | **Kotlin Coroutines + Flow** | Apache 2.0 | Native Kotlin async. RxJava is legacy alternative. |
| Testing | **JUnit 5 + Turbine + Mockk** | Various OSS | Standard Kotlin test stack. |
| UI Testing | **Compose Test** | Apache 2.0 | Official Compose testing library. |

### Backend

| Component | Choice | License | Why This Over Alternatives |
|-----------|--------|---------|--------------------------|
| Framework | **FastAPI** (Python) | MIT | Auto-generates OpenAPI docs. Async support. Fastest to prototype. Spring Boot is heavier. Express lacks type hints. |
| Runtime | **Python 3.12+** | PSF | Stable, wide library ecosystem. |
| ORM | **SQLAlchemy 2.0 + GeoAlchemy2** | MIT | Best Python ORM for PostgreSQL + PostGIS. |
| Migrations | **Alembic** | MIT | Standard for SQLAlchemy. |
| Validation | **Pydantic v2** | MIT | Built into FastAPI. Fast, type-safe. |
| Auth | **Keycloak** | Apache 2.0 | Full IAM: SSO, social login, RBAC, admin console. Firebase Auth is proprietary. Auth0 is paid. |
| Search | **Meilisearch** | MIT | Typo-tolerant, fast, easy to self-host. Elasticsearch is heavier. Algolia is paid. |
| Object Storage | **MinIO** | AGPL-3.0 | S3-compatible, self-hosted. Use S3 if on AWS. |
| Cache | **Redis** (Valkey fork) | BSD-3 | In-memory cache, rate limiting, session store. Valkey is the open-source continuation. |
| Task Queue | **Celery + Redis** | BSD-3 | Background jobs (image processing, notifications). Huey is lighter alternative. |

### Database

| Component | Choice | License | Why |
|-----------|--------|---------|-----|
| Primary DB | **PostgreSQL 16** | PostgreSQL | Best open-source relational DB. ACID, mature, extensible. |
| Spatial | **PostGIS** | GPL-2.0 | Industry-standard geospatial extension. Required for location queries. |
| Full-text | PostgreSQL built-in + Meilisearch | -- | Postgres for basic, Meilisearch for advanced search. |

### Infrastructure

| Component | Choice | License/Cost | Why |
|-----------|--------|-------------|-----|
| Reverse Proxy | **Caddy** | Apache 2.0 | Auto HTTPS, simple config. Traefik is alternative for Docker-native routing. |
| Containers | **Docker + Docker Compose** | Apache 2.0 | Standard containerization. |
| Orchestration | Docker Compose (MVP) -> **K3s** (scale) | Various OSS | Compose for single-node MVP. K3s for lightweight Kubernetes later. |
| CI/CD | **GitHub Actions** | Free (2,000 min/month) | Integrated with GitHub. No Jenkins overhead. |
| Monitoring | **Prometheus + Grafana** | Apache 2.0 | Industry standard open-source observability. |
| Logging | **Loki** (Grafana stack) | AGPL-3.0 | Integrates with Grafana. Lighter than ELK. |
| Error Tracking | **Sentry** (self-hosted) | BSL-1.1 (self-hosted is free) | Best error tracking. Self-hosted avoids cost. |
| Analytics | **PostHog** (self-hosted) | MIT | Product analytics. Self-hosted is free. Plausible for simpler web analytics. |

---

## Paid Services Justification

These paid services are recommended **only** where open-source falls short:

| Service | Use Case | Why No Open-Source Alternative | Cost |
|---------|----------|-------------------------------|------|
| **GitHub** (free tier) | Code hosting, CI/CD | Gitea is self-hosted alternative but adds ops burden | Free |
| **Hetzner / DigitalOcean** | VPS hosting | Physical servers needed; can't self-host hosting | ~$5-20/month |
| **Domain name** | Custom domain | Required for production | ~$10/year |
| **Google Play Store** | App distribution | No open-source alternative for mainstream Android distribution | $25 one-time |
| **Apple Developer** (if iOS later) | iOS distribution | No alternative | $99/year |

Everything else is achievable with open-source.

---

## Backend Service Architecture

### Event Service (FastAPI)

```
event-service/
├── app/
│   ├── main.py                    # FastAPI app entry
│   ├── config.py                  # Settings (Pydantic BaseSettings)
│   ├── api/
│   │   ├── v1/
│   │   │   ├── events.py          # Event endpoints
│   │   │   ├── categories.py      # Category endpoints
│   │   │   └── health.py          # Health check
│   │   └── deps.py                # Shared dependencies
│   ├── models/
│   │   ├── event.py               # SQLAlchemy models
│   │   ├── category.py
│   │   └── location.py
│   ├── schemas/
│   │   ├── event.py               # Pydantic schemas
│   │   └── common.py              # Shared schemas
│   ├── services/
│   │   ├── event_service.py       # Business logic
│   │   └── search_service.py      # Meilisearch integration
│   ├── repositories/
│   │   └── event_repository.py    # Data access
│   └── core/
│       ├── security.py            # JWT validation
│       └── exceptions.py          # Custom exceptions
├── alembic/                       # DB migrations
├── tests/
├── Dockerfile
├── pyproject.toml
└── docker-compose.yml
```

### API Gateway Pattern

```
Client -> Caddy (TLS termination + routing)
              |
              +-> /api/v1/events/*   -> Event Service (port 8000)
              +-> /api/v1/search/*   -> Meilisearch (port 7700)
              +-> /auth/*            -> Keycloak (port 8080)
              +-> /storage/*         -> MinIO (port 9000)
```

---

## Data Flow (MVP)

### Event Discovery Flow

```
1. User opens app
2. App requests location permission
3. App sends GET /api/v1/events?lat=X&lng=Y&radius=Z
4. Backend queries PostGIS:
   SELECT * FROM events
   WHERE ST_DWithin(location, ST_Point(X,Y)::geography, Z)
   AND start_date >= NOW()
   ORDER BY ST_Distance(location, ST_Point(X,Y)::geography)
   LIMIT 50;
5. Backend returns JSON array of events
6. App renders event bubbles on MapLibre map
7. User taps bubble -> bottom sheet with preview
8. User taps preview -> GET /api/v1/events/{id} -> detail screen
```

### Authentication Flow

```
1. User taps "Sign In"
2. App opens Keycloak login page (WebView or Custom Tab)
3. User authenticates
4. Keycloak returns authorization code
5. App exchanges code for access token + refresh token
6. App stores tokens in EncryptedSharedPreferences
7. All API calls include: Authorization: Bearer <token>
8. Backend validates token via Keycloak JWKS endpoint
```

---

## Scalability Plan

| Scale | Users | Architecture | Hosting |
|-------|-------|-------------|---------|
| MVP | < 1K | Single Docker Compose stack | 1 VPS ($5-10/mo) |
| Growth | 1K-10K | Separate DB server, add Redis cache | 2-3 VPS ($20-50/mo) |
| Scale | 10K-100K | K3s cluster, read replicas, CDN | 3-5 nodes ($100-200/mo) |
| Large | 100K+ | Full Kubernetes, managed DB, global CDN | Cloud-managed ($500+/mo) |

---

## Key Architecture Decisions

| Decision | Choice | Trade-off |
|----------|--------|-----------|
| Monolith vs Microservices | Modular monolith (MVP) | Simpler to develop/deploy; split later if needed |
| REST vs GraphQL | REST (MVP) | Lower learning curve; GraphQL adds flexibility later |
| Server-rendered vs SPA admin | SPA (React/Vue) or Keycloak admin | Keycloak provides admin console for free |
| Real-time | Polling (MVP) -> WebSocket (Phase 2) | Polling is simpler; WebSocket for live updates |
| Image storage | MinIO (self-hosted) | Avoids cloud vendor lock-in; S3-compatible |
| Map renderer | MapLibre GL | Open-source, no usage fees, performant |

---

*Next: [04 — Database Schema & API](./04-DATABASE-SCHEMA-API.md)*
