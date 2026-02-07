# 08 — Roadmap & Learning Plan

---

## Phased Feature Roadmap

### Phase 0: Foundation (Weeks 1-2)

**Goal:** Project scaffolding, development environment, CI/CD.

| Task | Deliverable |
|------|-------------|
| Set up Android project | Kotlin + Compose + Hilt + module structure |
| Set up backend project | FastAPI + SQLAlchemy + Alembic + Docker |
| Database schema | PostgreSQL + PostGIS with migrations |
| Keycloak setup | Realm, client, roles configured |
| CI/CD pipeline | GitHub Actions for lint + test + build |
| Dev environment | Docker Compose for local stack |
| Seed data script | 50-100 sample events across categories |

**Learning focus:** Project structure, build tools, Docker basics.

---

### Phase 1: MVP (Weeks 3-6)

**Goal:** Core functionality — map, events, auth, search.

| Feature | Priority | Complexity |
|---------|----------|------------|
| Guest onboarding flow | P0 | Low |
| Email + password auth (Keycloak) | P0 | Medium |
| Map view with MapLibre | P0 | High |
| Event bubbles on map | P0 | Medium |
| Bubble clustering at zoom levels | P1 | Medium |
| Event list view | P0 | Low |
| Event detail screen | P0 | Low |
| Nearby events API (PostGIS) | P0 | Medium |
| Basic search (text) | P1 | Medium |
| Category filter | P1 | Low |
| Date filter | P1 | Low |
| Distance filter (slider) | P1 | Low |
| Bottom navigation | P0 | Low |
| Profile / settings screen (stub) | P2 | Low |
| Error handling (offline, API errors) | P1 | Medium |
| Loading states (shimmer/skeleton) | P1 | Low |

**MVP Exit Criteria:**
- [ ] User can open app and see events on a map
- [ ] User can tap an event bubble and see details
- [ ] User can switch to list view
- [ ] User can search events by text
- [ ] User can filter by category, date, distance
- [ ] User can sign in and sign out
- [ ] Guest mode works without login
- [ ] App handles offline gracefully (cached data or error message)
- [ ] All lint checks pass
- [ ] Core unit tests pass (>70% coverage on domain layer)
- [ ] APK builds successfully in CI

**Learning focus:** Android Compose, MVVM, REST API integration, PostGIS queries.

---

### Phase 2: User Engagement (Weeks 7-10)

**Goal:** Make the app sticky — accounts, favorites, notifications.

| Feature | Priority | Complexity |
|---------|----------|------------|
| Social login (Google, GitHub) | P0 | Medium |
| User profile page | P0 | Low |
| Edit profile (name, avatar) | P1 | Low |
| Favorite / bookmark events | P0 | Medium |
| Favorites list screen | P0 | Low |
| Push notifications (FCM) | P1 | High |
| Event reminders | P1 | Medium |
| Share event (deep link) | P1 | Medium |
| Improved search (Meilisearch) | P1 | Medium |
| Recent searches | P2 | Low |
| Pull-to-refresh | P1 | Low |
| Infinite scroll pagination | P1 | Medium |
| Image gallery on event detail | P2 | Low |
| Dark mode support | P1 | Medium |

**Learning focus:** OAuth flows, push notifications, deep linking, Meilisearch.

---

### Phase 3: Content & Community (Weeks 11-14)

**Goal:** User-generated content, recommendations, real-time.

| Feature | Priority | Complexity |
|---------|----------|------------|
| Event creation (organizer role) | P0 | High |
| Image upload (MinIO) | P0 | Medium |
| Event editing / cancellation | P0 | Medium |
| Content moderation (manual review) | P1 | Medium |
| Event recommendations (basic) | P1 | High |
| "Events near me" notifications | P1 | Medium |
| Real-time event updates (WebSocket) | P2 | High |
| Event comments / discussion | P2 | Medium |
| Report inappropriate content | P1 | Low |
| Admin dashboard (web) | P1 | High |
| Category management (admin) | P1 | Medium |
| User management (admin) | P1 | Medium |

**Learning focus:** File upload, WebSocket, content moderation, admin panels.

---

### Phase 4: Growth & Monetization (Weeks 15-20)

**Goal:** Scale, monetize, polish.

| Feature | Priority | Complexity |
|---------|----------|------------|
| Ticket integration (Eventbrite API) | P1 | High |
| External event sources (API aggregation) | P1 | High |
| Premium organizer features (paid tier) | P2 | Medium |
| Featured events (promoted placement) | P2 | Medium |
| Advanced analytics dashboard | P2 | High |
| A/B testing framework | P2 | High |
| Performance optimization (profiling) | P1 | Medium |
| Accessibility audit and fixes | P1 | Medium |
| Localization / i18n | P2 | Medium |
| App Store listing (Google Play) | P0 | Medium |
| Landing page website | P2 | Medium |

**Learning focus:** Third-party API integration, monetization, Play Store publishing.

---

### Phase 5: Platform (Months 6+)

**Goal:** Mature platform with rich ecosystem.

| Feature | Priority |
|---------|----------|
| iOS app (Kotlin Multiplatform) |
| Event organizer mobile app |
| Advanced recommendation engine (ML) |
| Social features (follow users, activity feed) |
| Calendar integration |
| Venue pages |
| Event series / recurring events |
| Multi-language support |
| Offline-first with sync |
| Performance budget and optimization |

---

## Roadmap Visual Timeline

```
Week:  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15+
       |-----|-----|-----|-----|------|------|------|------|------>
Phase: |  0  |        1 (MVP)       |      2      |   3  | 4+
       |Found|  Map+Auth+Search     | Engage+Push |Content| Grow
       |ation|                      |             |       |
                    ^                       ^
                    |                       |
                MVP Launch            v2.0 Launch
```

---

## Self-Learning Roadmap

### Track 1: Android Development

```
Level 1 — Fundamentals (Weeks 1-2)
├── Kotlin language basics
│   ├── Null safety, data classes, sealed classes
│   ├── Coroutines and Flow
│   └── Extension functions, scope functions
├── Android fundamentals
│   ├── Activity lifecycle
│   ├── Permissions
│   └── Intents and navigation
└── Resources
    ├── Kotlin Koans (free, interactive)
    │   https://kotlinlang.org/docs/koans.html
    ├── Android Basics with Compose (Google, free)
    │   https://developer.android.com/courses/android-basics-compose/course
    └── Kotlin Coroutines Guide (official)
        https://kotlinlang.org/docs/coroutines-guide.html

Level 2 — Jetpack Compose (Weeks 3-4)
├── Compose fundamentals
│   ├── Composable functions
│   ├── State management (remember, State, StateFlow)
│   ├── Layouts (Row, Column, Box, LazyColumn)
│   └── Modifiers
├── Material 3 in Compose
│   ├── Theme setup
│   ├── Components (Cards, Buttons, Chips)
│   └── Navigation bar
└── Resources
    ├── Compose Pathway (Google, free)
    │   https://developer.android.com/courses/pathways/compose
    ├── Compose Samples (GitHub)
    │   https://github.com/android/compose-samples
    └── Thinking in Compose (article)
        https://developer.android.com/develop/ui/compose/mental-model

Level 3 — Architecture (Weeks 4-6)
├── Clean Architecture
│   ├── Presentation / Domain / Data layers
│   ├── Use cases
│   └── Repository pattern
├── Dependency Injection with Hilt
├── Navigation (Compose Navigation)
├── State management patterns
└── Resources
    ├── Guide to App Architecture (Google, free)
    │   https://developer.android.com/topic/architecture
    ├── Now in Android (reference app)
    │   https://github.com/android/nowinandroid
    └── Clean Architecture on Android (article series)

Level 4 — Advanced (Weeks 7+)
├── MapLibre integration
├── Custom composables and animations
├── Performance profiling
├── Testing (unit, integration, UI)
├── Accessibility
└── App distribution (Play Store)
```

### Track 2: Backend Development

```
Level 1 — Python + FastAPI (Weeks 1-3)
├── Python fundamentals (if needed)
│   ├── Type hints
│   ├── async/await
│   └── Virtual environments
├── FastAPI
│   ├── Path operations, query params
│   ├── Pydantic schemas
│   ├── Dependency injection
│   ├── Middleware
│   └── Auto-generated docs
└── Resources
    ├── FastAPI Tutorial (official, free)
    │   https://fastapi.tiangolo.com/tutorial/
    ├── Python Type Hints Cheat Sheet
    └── Real Python (free articles)

Level 2 — Database (Weeks 3-5)
├── PostgreSQL
│   ├── SQL fundamentals
│   ├── Indexing strategies
│   └── JSON/JSONB
├── PostGIS
│   ├── Spatial data types (POINT, POLYGON)
│   ├── Spatial queries (ST_DWithin, ST_Distance)
│   ├── Spatial indexes (GIST)
│   └── Coordinate systems (SRID 4326)
├── SQLAlchemy 2.0
│   ├── ORM models
│   ├── Async queries
│   └── Relationships
├── Alembic migrations
└── Resources
    ├── PostgreSQL Tutorial (free)
    │   https://www.postgresqltutorial.com/
    ├── Introduction to PostGIS (free workshop)
    │   https://postgis.net/workshops/postgis-intro/
    └── SQLAlchemy 2.0 Tutorial (official)

Level 3 — Services & Auth (Weeks 5-7)
├── Keycloak
│   ├── OAuth 2.0 / OIDC concepts
│   ├── Realm and client configuration
│   ├── JWT validation
│   └── RBAC
├── Meilisearch
│   ├── Index configuration
│   ├── Search parameters
│   └── Data sync
├── Redis / Valkey
│   ├── Caching patterns
│   ├── Rate limiting
│   └── Session management
└── Resources
    ├── OAuth 2.0 Simplified (free book)
    │   https://www.oauth.com/
    ├── Keycloak Documentation (official)
    ├── Meilisearch Documentation (official)
    └── Redis University (free courses)
```

### Track 3: Cloud & DevOps

```
Level 1 — Containers (Weeks 1-2)
├── Docker
│   ├── Dockerfile writing
│   ├── Docker Compose
│   ├── Volumes and networking
│   └── Multi-stage builds
└── Resources
    ├── Docker Getting Started (official, free)
    │   https://docs.docker.com/get-started/
    └── Docker Compose Tutorial (official)

Level 2 — CI/CD (Weeks 3-4)
├── GitHub Actions
│   ├── Workflow syntax
│   ├── Jobs and steps
│   ├── Secrets management
│   ├── Artifact handling
│   └── Matrix builds
└── Resources
    ├── GitHub Actions Documentation (free)
    └── GitHub Actions for Android (community guides)

Level 3 — Infrastructure (Weeks 5-8)
├── Linux server administration
│   ├── SSH, UFW, systemd
│   ├── Disk management
│   └── Log management
├── Reverse proxy (Caddy)
├── TLS certificates
├── Monitoring (Prometheus + Grafana)
├── Backups
└── Resources
    ├── Linux Journey (free)
    │   https://linuxjourney.com/
    ├── Caddy Documentation (official)
    └── Prometheus Getting Started (official)

Level 4 — Orchestration (Months 3+)
├── Kubernetes concepts (K3s for lightweight)
├── Helm charts
├── Horizontal pod autoscaling
├── Infrastructure as Code (Terraform, optional)
└── Resources
    ├── Kubernetes the Hard Way (free)
    ├── K3s Documentation (official)
    └── Terraform Getting Started (HashiCorp, free)
```

### Track 4: AI-Assisted Engineering

```
Level 1 — Prompt Engineering (Week 1)
├── Writing effective prompts
├── Context management
├── Iterative refinement
└── Resources
    ├── Anthropic Prompt Engineering Guide
    ├── Claude Code Documentation

Level 2 — AI-Powered Workflows (Ongoing)
├── Code generation patterns
├── Test generation
├── Documentation generation
├── Code review with AI
├── Debugging with AI
└── Resources
    ├── Practice with actual development tasks
    └── Document effective prompts in project wiki

Level 3 — AI Integration (Months 3+)
├── Building AI features into the app
│   ├── Event recommendations
│   ├── Smart search
│   └── Content moderation
└── Resources
    ├── ML on Android (TensorFlow Lite)
    ├── OpenAI / Anthropic API integration
    └── Open-source ML models (Hugging Face)
```

---

## Learning Milestones

| Milestone | Checkpoint | Evidence |
|-----------|-----------|---------|
| M1: Environment Ready | Docker Compose stack runs locally | All services start and respond to health checks |
| M2: First Screen | Map displays with hardcoded markers | Screenshot of map with 3+ markers |
| M3: API Connected | Events load from backend to map | Network log showing API call + map update |
| M4: Auth Working | User can sign in via Keycloak | Login flow completes, token stored |
| M5: MVP Complete | All MVP features functional | Demo video walking through all features |
| M6: CI/CD Pipeline | Automated build on push | Green GitHub Actions run |
| M7: First Deployment | App accessible on server | URL responds with data |
| M8: Phase 2 Complete | Favorites, notifications working | Feature demo |
| M9: Play Store Ready | Signed release APK | Store listing draft |

---

## Recommended Learning Resources (All Free/Open-Source)

### Books (Free Online)
- **Kotlin in Action** — sample chapters free from JetBrains
- **Designing Data-Intensive Applications** — foundational concepts
- **The Twelve-Factor App** — https://12factor.net/

### Courses (Free)
- **Android Basics with Compose** — Google (developer.android.com)
- **CS50** — Harvard (edx.org) — general CS foundations
- **Git & GitHub** — freeCodeCamp (youtube.com)

### Reference Applications
- **Now in Android** — Google's reference Compose app
  https://github.com/android/nowinandroid
- **Compose Samples** — official samples gallery
  https://github.com/android/compose-samples
- **FastAPI Full Stack Template** — project structure reference
  https://github.com/fastapi/full-stack-fastapi-template

### Communities
- **Kotlin Slack** — kotlinlang.slack.com
- **Android Dev Discord** — community discussions
- **r/androiddev** — Reddit community
- **FastAPI Discussions** — GitHub Discussions

---

## Weekly Study Schedule (Suggested)

For a solo learner spending 10-15 hours/week:

| Day | Activity | Hours |
|-----|----------|-------|
| Mon | Theory: Read docs / tutorials | 2 |
| Tue | Practice: Implement feature with AI | 2-3 |
| Wed | Practice: Continue implementation | 2-3 |
| Thu | Review: AI code review + refactor | 1-2 |
| Fri | DevOps: CI/CD, Docker, deployment | 1-2 |
| Sat | Explore: Try new library or concept | 2-3 |
| Sun | Rest / light reading | 0-1 |

---

## Progress Tracking Template

Use this template in your project wiki or notes:

```markdown
## Week [N] Progress

### Completed
- [ ] Feature: ...
- [ ] Learning: ...
- [ ] DevOps: ...

### In Progress
- [ ] ...

### Blocked
- [ ] ... (reason: ...)

### Key Learnings
1. ...
2. ...

### Next Week Plan
1. ...
2. ...
```

---

*Back to: [00 — Strategy Overview](./00-STRATEGY-OVERVIEW.md)*
