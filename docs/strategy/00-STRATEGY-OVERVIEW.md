# EventBuzz - Android Event Discovery App: Development Strategy

## Document Index

This strategy package provides a complete, end-to-end blueprint for building **EventBuzz** — a location-based event discovery Android application — using cloud-based development, open-source tools, and AI-assisted workflows.

### Document Map

| # | Document | Purpose |
|---|----------|---------|
| 00 | **Strategy Overview** (this file) | Master index and executive summary |
| 01 | [Clarification Flow](./01-CLARIFICATION-FLOW.md) | Required Q&A before implementation begins |
| 02 | [Product Design & UI/UX](./02-PRODUCT-DESIGN-UI-UX.md) | Screens, flows, wireframes, design system |
| 03 | [System Architecture](./03-SYSTEM-ARCHITECTURE.md) | MVP architecture, tech stack, component design |
| 04 | [Database Schema & API](./04-DATABASE-SCHEMA-API.md) | Data models, API outline, caching strategy |
| 05 | [Deployment & DevOps](./05-DEPLOYMENT-DEVOPS.md) | CI/CD, cloud deployment, infrastructure |
| 06 | [Governance & Security](./06-GOVERNANCE-SECURITY.md) | Auth, privacy, compliance, audit |
| 07 | [AI-Assisted Development](./07-AI-ASSISTED-DEVELOPMENT.md) | AI coding workflows, prompt patterns, automation |
| 08 | [Roadmap & Learning Plan](./08-ROADMAP-LEARNING-PLAN.md) | Phased roadmap, self-learning curriculum |

---

## Executive Summary

**EventBuzz** is an Android mobile app that displays location-based event bubbles on an interactive map. Users can discover nearby events, filter by category/date/distance, and view event details.

### Guiding Principles

1. **Open-source first** — paid services only when no viable open-source alternative exists, with written justification.
2. **MVP then iterate** — ship the smallest useful product, then improve through phased upgrades.
3. **Clean Architecture** — SOLID principles, modular codebase, testable at every layer.
4. **AI-augmented development** — use AI tools for code generation, review, testing, and documentation throughout the lifecycle.
5. **Cloud-native, self-learning** — all development happens via cloud-based tooling; the strategy doubles as a learning path.

### Core Technology Decisions (Open-Source First)

| Layer | Choice | License / Cost | Justification |
|-------|--------|---------------|---------------|
| Language | Kotlin | Apache 2.0 | Official Android language, null-safe, coroutines |
| UI Framework | Jetpack Compose | Apache 2.0 | Modern declarative UI, official Google toolkit |
| Maps | MapLibre GL Native | BSD-3 | Open-source fork of Mapbox GL, no API key fees |
| Map Tiles | OpenStreetMap | ODbL | Free community-maintained geodata |
| Backend | FastAPI (Python) | MIT | Lightweight, async, auto-docs, fast to prototype |
| Database | PostgreSQL + PostGIS | PostgreSQL License | Best open-source spatial database |
| Auth | Keycloak | Apache 2.0 | Full-featured open-source IAM |
| Object Storage | MinIO | AGPL-3.0 | S3-compatible open-source storage |
| Search | Meilisearch | MIT | Fast, typo-tolerant, open-source search |
| CI/CD | GitHub Actions | Free tier sufficient | Integrated with repo, YAML-based |
| Analytics | Plausible / PostHog | MIT (self-hosted) | Privacy-friendly, open-source |
| Cloud Hosting | Self-hosted (Docker/K8s) or Hetzner/DigitalOcean | Low cost | Avoid vendor lock-in |

### MVP Scope

- Guest + basic email/password authentication
- Interactive map with event bubbles (clustered at zoom levels)
- Event list view with search and filters (date, category, distance)
- Event detail screen
- Admin-seeded event data
- REST API backend
- Basic logging and error tracking

### What This Strategy Is NOT

- Not production-ready code — it is a **plan and prompt system** for AI-assisted development.
- Not a fixed spec — it is a **living document** meant to evolve as questions are answered.
- Not framework-locked — alternatives are noted at every decision point.

---

## How to Use This Strategy

### As an AI Prompt

Feed documents sequentially to an AI coding assistant:

1. Start with `01-CLARIFICATION-FLOW.md` — answer all questions first.
2. Move to `02-PRODUCT-DESIGN-UI-UX.md` — confirm screen flows.
3. Use `03-SYSTEM-ARCHITECTURE.md` to scaffold the project.
4. Reference `04-DATABASE-SCHEMA-API.md` when building the backend.
5. Follow `05-DEPLOYMENT-DEVOPS.md` for CI/CD setup.
6. Apply `06-GOVERNANCE-SECURITY.md` throughout.
7. Use `07-AI-ASSISTED-DEVELOPMENT.md` as your daily workflow guide.
8. Track progress against `08-ROADMAP-LEARNING-PLAN.md`.

### As a Self-Learning Curriculum

Each document includes learning checkpoints and resource links. Follow the learning plan in document 08 to build skills progressively while building the app.

---

## Decision Log

Track key decisions as the project evolves:

| Date | Decision | Rationale | Alternatives Considered |
|------|----------|-----------|------------------------|
| _TBD_ | _Fill in as project progresses_ | | |

---

*This strategy was designed to be modular. Each document can be used independently or as part of the full suite.*
