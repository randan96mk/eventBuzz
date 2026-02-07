# Clarification Flow — Accepted Defaults

> All defaults accepted on 2026-02-07. This file records the locked-in decisions.

| # | Question | Answer |
|---|----------|--------|
| A1 | Target Region | **A — Single city** (to be specified during dev) |
| A2 | Target Audience | **A — General public (all ages)** |
| A3 | Event Types | **H — All of the above** |
| A4 | Monetization | **A — Free (MVP)** |
| A5 | Expected Scale (Year 1) | **A — < 1,000 users (prototype/learning)** |
| B1 | Authentication Type | **E — Email + Social login (Google/GitHub)** |
| B2 | Backend Language | **A — Python (FastAPI)** |
| B3 | Hosting Preference | **E — PaaS (Railway/Render/Fly.io)** |
| B4 | Map Tile Provider | **A — OpenStreetMap + MapLibre** |
| B5 | Database | **B — Supabase (hosted Postgres, open-source)** |
| B6 | API Style | **D — REST for MVP, GraphQL later** |
| C1 | Design Language | **C — Material Design 3 + custom theming** |
| C2 | Primary Navigation | **A — Bottom nav (Map / List / Search / Profile)** |
| C3 | Offline Support | **B — Cache recently viewed events** |
| D1 | Data Privacy | **E — Reasonable defaults (GDPR-aligned)** |
| D2 | Content Moderation | **A — Admin-only event creation** |
| E1 | Solo or Team | **A — Solo developer (learning)** |
| E2 | Dev Environment | **C — Mixed (local Android, cloud backend)** |
| E3 | AI Coding Tools | **A — Claude Code (CLI)** |

## Implementation Implications

These defaults mean:
- **Simple auth**: Keycloak with email + Google/GitHub OAuth
- **Small infra**: Single Docker Compose stack, Supabase for managed DB
- **REST API first**: No GraphQL complexity in MVP
- **Admin-seeded content**: No user-generated events in MVP
- **Minimal compliance**: Privacy policy + basic data handling, no full GDPR audit yet
- **Solo workflow**: No team collaboration tooling, focus on AI-assisted development
