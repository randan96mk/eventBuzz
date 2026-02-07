# 01 — Clarification Flow

> **MANDATORY**: Do not proceed to implementation until all critical questions (marked with `[CRITICAL]`) are answered. Non-critical questions have sensible defaults.

---

## Instructions

For each question below:
- **Text input** fields require a free-form answer.
- **Multiple-choice** fields list options; select one (or specify "Other").
- **Default** values are used if no answer is provided.

Copy this document, fill in the "Your Answer" column, and use the completed version as input to the AI assistant.

---

## Section A: Product & Market

### A1. Target Region `[CRITICAL]`
**Type:** Multiple-choice + Text

| Option | Description |
|--------|-------------|
| A | Single city (specify which) |
| B | Single country (specify which) |
| C | Multi-country / regional |
| D | Global from day one |

**Default:** A (single city — reduces scope for MVP)
**Your Answer:** _______________

---

### A2. Target Audience `[CRITICAL]`
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | General public (all ages) |
| B | Young adults (18-35) |
| C | Professionals / networking |
| D | Students / campus |
| E | Specific niche (describe) |

**Default:** A
**Your Answer:** _______________

---

### A3. Event Types `[CRITICAL]`
**Type:** Multiple-choice (select all that apply)

| Option | Description |
|--------|-------------|
| A | Concerts / music |
| B | Meetups / community |
| C | Sports |
| D | Food & drink |
| E | Conferences / tech |
| F | Arts / culture |
| G | Outdoor / adventure |
| H | All of the above |

**Default:** H
**Your Answer:** _______________

---

### A4. Monetization Model
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Free (no revenue plan yet) |
| B | Freemium (free + premium tier) |
| C | Ads (banner / interstitial) |
| D | Commission on ticket sales |
| E | Subscription for organizers |
| F | Hybrid (specify) |

**Default:** A (free for MVP, decide later)
**Your Answer:** _______________

---

### A5. Expected User Scale (Year 1)
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | < 1,000 users (prototype / learning) |
| B | 1,000 - 10,000 users |
| C | 10,000 - 100,000 users |
| D | 100,000+ users |

**Default:** A
**Your Answer:** _______________

---

## Section B: Technical Decisions

### B1. Authentication Type `[CRITICAL]`
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Guest-only (no accounts) |
| B | Email + password |
| C | Social login (Google/GitHub) |
| D | Phone number (OTP) |
| E | B + C combined |
| F | B + C + D combined |

**Default:** E (Email + Social login)
**Your Answer:** _______________

---

### B2. Backend Language Preference
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Python (FastAPI) — fast prototyping, great docs |
| B | Kotlin (Ktor/Spring Boot) — same language as Android |
| C | Node.js (Express/Fastify) — large ecosystem |
| D | Go — high performance, simple deployment |
| E | Java (Spring Boot) — enterprise, mature |
| F | No preference (AI decides) |

**Default:** A (FastAPI — lowest barrier for solo/learning dev)
**Your Answer:** _______________

---

### B3. Hosting Preference `[CRITICAL]`
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Self-hosted (VPS — Hetzner/DigitalOcean/Linode) |
| B | AWS Free Tier |
| C | Google Cloud Free Tier |
| D | Azure Free Tier |
| E | Railway / Render / Fly.io (PaaS) |
| F | Fully local (development only) |

**Default:** E (PaaS — simplest for learning)
**Your Answer:** _______________

---

### B4. Map Tile Provider
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | OpenStreetMap + MapLibre (fully open-source, free) |
| B | Mapbox (freemium, 50K loads/month free) |
| C | Google Maps (paid, $200/month credit) |
| D | No preference |

**Default:** A (open-source first principle)
**Your Answer:** _______________

---

### B5. Database Preference
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | PostgreSQL + PostGIS (self-managed) |
| B | Supabase (hosted Postgres, open-source) |
| C | Firebase / Firestore (Google, proprietary) |
| D | MongoDB + GeoJSON |
| E | No preference |

**Default:** B (Supabase — managed Postgres with auth, free tier)
**Your Answer:** _______________

---

### B6. API Style
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | REST (simpler, well-understood) |
| B | GraphQL (flexible queries, steeper learning curve) |
| C | gRPC (high performance, complex setup) |
| D | REST for MVP, GraphQL later |

**Default:** D
**Your Answer:** _______________

---

## Section C: Design & UX

### C1. Design Language
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Material Design 3 (Google's system) |
| B | Custom design system |
| C | Material Design 3 base + custom theming |

**Default:** C
**Your Answer:** _______________

---

### C2. Primary Navigation
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Bottom navigation bar (Map / List / Search / Profile) |
| B | Side drawer navigation |
| C | Tab-based top navigation |
| D | Map-centric (single screen with overlays) |

**Default:** A
**Your Answer:** _______________

---

### C3. Offline Support Level
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | None (online-only) |
| B | Cache recently viewed events |
| C | Full offline map + cached events |
| D | Offline-first with sync |

**Default:** B
**Your Answer:** _______________

---

## Section D: Governance & Compliance

### D1. Data Privacy Requirements
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Minimal (basic privacy policy) |
| B | GDPR-compliant |
| C | CCPA-compliant |
| D | GDPR + CCPA |
| E | Not sure — apply reasonable defaults |

**Default:** E
**Your Answer:** _______________

---

### D2. Content Moderation
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Admin-only event creation (no user submissions) |
| B | User submissions with manual review |
| C | User submissions with automated + manual review |
| D | Open posting (community moderated) |

**Default:** A (safest for MVP)
**Your Answer:** _______________

---

## Section E: Development Process

### E1. Solo or Team?
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Solo developer (learning) |
| B | 2-3 person team |
| C | Larger team (4+) |

**Default:** A
**Your Answer:** _______________

---

### E2. Development Environment
**Type:** Multiple-choice

| Option | Description |
|--------|-------------|
| A | Android Studio (local) |
| B | Cloud IDE (Gitpod / GitHub Codespaces) |
| C | Mixed (local for Android, cloud for backend) |

**Default:** C
**Your Answer:** _______________

---

### E3. AI Coding Tools Available
**Type:** Multiple-choice (select all that apply)

| Option | Description |
|--------|-------------|
| A | Claude Code (CLI) |
| B | GitHub Copilot |
| C | Cursor |
| D | ChatGPT / GPT-4 |
| E | Other (specify) |

**Default:** A
**Your Answer:** _______________

---

## Quick-Start Defaults

If you want to start immediately, these defaults create a viable MVP:

| Question | Default |
|----------|---------|
| Region | Single city |
| Audience | General public |
| Events | All types |
| Monetization | Free (MVP) |
| Scale | < 1,000 users |
| Auth | Email + Social login |
| Backend | FastAPI (Python) |
| Hosting | PaaS (Railway/Render) |
| Maps | OSM + MapLibre |
| Database | Supabase (Postgres) |
| API | REST (MVP), GraphQL later |
| Design | Material 3 + custom theme |
| Navigation | Bottom nav bar |
| Offline | Cache recent events |
| Privacy | Reasonable defaults |
| Moderation | Admin-only |
| Developer | Solo learner |
| IDE | Mixed |
| AI Tool | Claude Code |

**To accept all defaults:** Write "Accept all defaults" and proceed to document 02.

---

*Next: [02 — Product Design & UI/UX](./02-PRODUCT-DESIGN-UI-UX.md)*
