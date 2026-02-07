# EventBuzz

A location-based event discovery Android app that displays event bubbles on an interactive map.

## Overview

EventBuzz helps users discover nearby events through an intuitive map interface. Users can browse events as map bubbles, filter by category/date/distance, and view detailed event information.

## Strategy Documents

The full development strategy is in [`docs/strategy/`](./docs/strategy/):

| Doc | Title | Description |
|-----|-------|-------------|
| 00 | [Strategy Overview](./docs/strategy/00-STRATEGY-OVERVIEW.md) | Master index, executive summary, tech decisions |
| 01 | [Clarification Flow](./docs/strategy/01-CLARIFICATION-FLOW.md) | Required Q&A before implementation |
| 02 | [Product Design & UI/UX](./docs/strategy/02-PRODUCT-DESIGN-UI-UX.md) | Screens, wireframes, design system |
| 03 | [System Architecture](./docs/strategy/03-SYSTEM-ARCHITECTURE.md) | Clean Architecture, tech stack, modules |
| 04 | [Database Schema & API](./docs/strategy/04-DATABASE-SCHEMA-API.md) | PostgreSQL/PostGIS schema, REST API, caching |
| 05 | [Deployment & DevOps](./docs/strategy/05-DEPLOYMENT-DEVOPS.md) | Docker, CI/CD, infrastructure |
| 06 | [Governance & Security](./docs/strategy/06-GOVERNANCE-SECURITY.md) | Auth, privacy, compliance, audit |
| 07 | [AI-Assisted Development](./docs/strategy/07-AI-ASSISTED-DEVELOPMENT.md) | AI workflows, prompt patterns, automation |
| 08 | [Roadmap & Learning Plan](./docs/strategy/08-ROADMAP-LEARNING-PLAN.md) | Phased roadmap, self-learning curriculum |

## Tech Stack (Open-Source First)

| Layer | Technology |
|-------|-----------|
| Android | Kotlin + Jetpack Compose |
| Maps | MapLibre GL + OpenStreetMap |
| Backend | FastAPI (Python) |
| Database | PostgreSQL + PostGIS |
| Auth | Keycloak |
| Search | Meilisearch |
| Storage | MinIO (S3-compatible) |
| CI/CD | GitHub Actions |

## Getting Started

1. Read the [Clarification Flow](./docs/strategy/01-CLARIFICATION-FLOW.md) and answer the questions
2. Review the [System Architecture](./docs/strategy/03-SYSTEM-ARCHITECTURE.md)
3. Follow the [Roadmap](./docs/strategy/08-ROADMAP-LEARNING-PLAN.md) starting with Phase 0

## License

TBD
