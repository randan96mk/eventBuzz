# 04 — Database Schema & API Design

---

## Entity-Relationship Diagram

```
+----------------+       +------------------+       +----------------+
|    users       |       |     events       |       |   categories   |
|----------------|       |------------------|       |----------------|
| id (UUID) PK   |<------| created_by (FK)  |   +-->| id (INT) PK    |
| keycloak_id    |       | id (UUID) PK     |   |   | name           |
| display_name   |       | title            |   |   | slug           |
| email          |       | description      |   |   | color_hex      |
| avatar_url     |       | category_id (FK) |---+   | icon_name      |
| created_at     |       | location (POINT) |       | created_at     |
| updated_at     |       | address          |       +----------------+
+----------------+       | city             |
                         | country          |
                         | start_date       |       +----------------+
                         | end_date         |       |  event_images  |
                         | image_url        |       |----------------|
                         | ticket_url       |       | id (UUID) PK   |
                         | price_min        |       | event_id (FK)  |
                         | price_max        |       | image_url      |
                         | currency         |       | display_order  |
                         | status           |       | created_at     |
                         | source           |       +----------------+
                         | external_id      |
                         | metadata (JSONB) |
                         | created_at       |
                         | updated_at       |
                         +------------------+
                                |
                                |
                         +------v-----------+
                         |   event_tags     |
                         |------------------|
                         | event_id (FK)    |
                         | tag (VARCHAR)    |
                         +------------------+
```

---

## PostgreSQL Schema (SQL)

```sql
-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;  -- For text search

-- Categories (seeded data)
CREATE TABLE categories (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    color_hex   VARCHAR(7) NOT NULL DEFAULT '#6750A4',
    icon_name   VARCHAR(50) NOT NULL DEFAULT 'event',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Users (synced from Keycloak)
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id   VARCHAR(255) UNIQUE,
    display_name  VARCHAR(100),
    email         VARCHAR(255),
    avatar_url    TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Events (core table)
CREATE TABLE events (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    category_id   INTEGER NOT NULL REFERENCES categories(id),
    location      GEOGRAPHY(POINT, 4326) NOT NULL,
    address       VARCHAR(500),
    city          VARCHAR(100),
    country       VARCHAR(100),
    start_date    TIMESTAMPTZ NOT NULL,
    end_date      TIMESTAMPTZ,
    image_url     TEXT,
    ticket_url    TEXT,
    price_min     DECIMAL(10, 2),
    price_max     DECIMAL(10, 2),
    currency      VARCHAR(3) DEFAULT 'USD',
    status        VARCHAR(20) NOT NULL DEFAULT 'active'
                  CHECK (status IN ('draft', 'active', 'cancelled', 'completed')),
    source        VARCHAR(50) NOT NULL DEFAULT 'manual'
                  CHECK (source IN ('manual', 'api', 'scraper', 'user')),
    external_id   VARCHAR(255),
    created_by    UUID REFERENCES users(id),
    metadata      JSONB DEFAULT '{}',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Event images (multiple images per event)
CREATE TABLE event_images (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id      UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    image_url     TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Event tags (flexible tagging)
CREATE TABLE event_tags (
    event_id  UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    tag       VARCHAR(50) NOT NULL,
    PRIMARY KEY (event_id, tag)
);

-- Indexes
CREATE INDEX idx_events_location ON events USING GIST (location);
CREATE INDEX idx_events_start_date ON events (start_date);
CREATE INDEX idx_events_category ON events (category_id);
CREATE INDEX idx_events_status ON events (status);
CREATE INDEX idx_events_city ON events (city);
CREATE INDEX idx_events_title_trgm ON events USING GIN (title gin_trgm_ops);
CREATE INDEX idx_event_tags_tag ON event_tags (tag);

-- Updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER events_updated_at
    BEFORE UPDATE ON events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
```

### Seed Data — Categories

```sql
INSERT INTO categories (name, slug, color_hex, icon_name) VALUES
    ('Music',          'music',      '#7C4DFF', 'music_note'),
    ('Sports',         'sports',     '#00C853', 'sports'),
    ('Food & Drink',   'food-drink', '#FF6D00', 'restaurant'),
    ('Arts & Culture', 'arts',       '#F50057', 'palette'),
    ('Tech',           'tech',       '#2979FF', 'computer'),
    ('Outdoor',        'outdoor',    '#00BFA5', 'nature'),
    ('Community',      'community',  '#FFD600', 'people'),
    ('Nightlife',      'nightlife',  '#AA00FF', 'nightlife'),
    ('Education',      'education',  '#00B0FF', 'school'),
    ('Other',          'other',      '#757575', 'event');
```

---

## Pydantic Schemas (FastAPI)

```python
# schemas/event.py
from datetime import datetime
from decimal import Decimal
from uuid import UUID
from pydantic import BaseModel, Field

class LocationSchema(BaseModel):
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)

class CategoryOut(BaseModel):
    id: int
    name: str
    slug: str
    color_hex: str
    icon_name: str

class EventBubble(BaseModel):
    """Minimal event data for map markers."""
    id: UUID
    title: str
    latitude: float
    longitude: float
    category_slug: str
    category_color: str
    start_date: datetime

class EventListItem(BaseModel):
    """Event data for list view cards."""
    id: UUID
    title: str
    description: str | None
    category: CategoryOut
    latitude: float
    longitude: float
    address: str | None
    city: str | None
    start_date: datetime
    end_date: datetime | None
    image_url: str | None
    price_min: Decimal | None
    price_max: Decimal | None
    currency: str
    distance_meters: float | None = None

class EventDetail(EventListItem):
    """Full event data for detail screen."""
    ticket_url: str | None
    tags: list[str]
    images: list[str]
    source: str
    created_at: datetime

class EventsNearbyRequest(BaseModel):
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)
    radius_meters: int = Field(default=5000, ge=100, le=50000)
    category: str | None = None
    date_from: datetime | None = None
    date_to: datetime | None = None
    limit: int = Field(default=50, ge=1, le=200)
    offset: int = Field(default=0, ge=0)

class PaginatedResponse(BaseModel):
    items: list[EventListItem]
    total: int
    limit: int
    offset: int
    has_more: bool
```

---

## REST API Design

### Base URL

```
https://api.eventbuzz.app/api/v1
```

### Endpoints

#### Events

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/events/nearby` | Get events near a location | Optional |
| GET | `/events/bubbles` | Get minimal event data for map markers | Optional |
| GET | `/events/{id}` | Get event detail | Optional |
| GET | `/events/search` | Search events by text | Optional |
| POST | `/events` | Create event (admin) | Required (admin) |
| PUT | `/events/{id}` | Update event (admin) | Required (admin) |
| DELETE | `/events/{id}` | Delete event (admin) | Required (admin) |

#### Categories

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/categories` | List all categories | None |

#### Health

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/health` | Service health check | None |
| GET | `/health/ready` | Readiness (DB, dependencies) | None |

---

### Endpoint Details

#### GET `/events/nearby`

Returns paginated events near a location.

**Query Parameters:**
```
lat         float    required   Latitude (-90 to 90)
lng         float    required   Longitude (-180 to 180)
radius      int      optional   Radius in meters (default: 5000, max: 50000)
category    string   optional   Category slug filter
date_from   string   optional   ISO 8601 datetime
date_to     string   optional   ISO 8601 datetime
sort        string   optional   "distance" (default) | "date" | "popular"
limit       int      optional   Results per page (default: 50, max: 200)
offset      int      optional   Pagination offset
```

**Response: 200 OK**
```json
{
  "items": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Concert in the Park",
      "description": "Join us for a free outdoor concert...",
      "category": {
        "id": 1,
        "name": "Music",
        "slug": "music",
        "color_hex": "#7C4DFF",
        "icon_name": "music_note"
      },
      "latitude": 40.7829,
      "longitude": -73.9654,
      "address": "Central Park Amphitheater",
      "city": "New York",
      "start_date": "2026-03-15T19:00:00Z",
      "end_date": "2026-03-15T22:00:00Z",
      "image_url": "https://storage.eventbuzz.app/events/concert-park.jpg",
      "price_min": 0.00,
      "price_max": 0.00,
      "currency": "USD",
      "distance_meters": 850.5
    }
  ],
  "total": 127,
  "limit": 50,
  "offset": 0,
  "has_more": true
}
```

#### GET `/events/bubbles`

Returns minimal data for rendering map markers. Lighter payload than `/nearby`.

**Query Parameters:**
```
lat         float    required
lng         float    required
radius      int      optional   (default: 5000)
category    string   optional
date_from   string   optional
date_to     string   optional
```

**Response: 200 OK**
```json
{
  "bubbles": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Concert in the Park",
      "latitude": 40.7829,
      "longitude": -73.9654,
      "category_slug": "music",
      "category_color": "#7C4DFF",
      "start_date": "2026-03-15T19:00:00Z"
    }
  ],
  "total": 127
}
```

#### GET `/events/{id}`

**Response: 200 OK**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Concert in the Park",
  "description": "Join us for a free outdoor concert featuring local bands...",
  "category": {
    "id": 1,
    "name": "Music",
    "slug": "music",
    "color_hex": "#7C4DFF",
    "icon_name": "music_note"
  },
  "latitude": 40.7829,
  "longitude": -73.9654,
  "address": "Central Park Amphitheater",
  "city": "New York",
  "start_date": "2026-03-15T19:00:00Z",
  "end_date": "2026-03-15T22:00:00Z",
  "image_url": "https://storage.eventbuzz.app/events/concert-park.jpg",
  "price_min": 0.00,
  "price_max": 0.00,
  "currency": "USD",
  "ticket_url": null,
  "tags": ["free", "outdoor", "live-music"],
  "images": [
    "https://storage.eventbuzz.app/events/concert-park.jpg",
    "https://storage.eventbuzz.app/events/concert-park-2.jpg"
  ],
  "source": "manual",
  "distance_meters": null,
  "created_at": "2026-02-01T10:00:00Z"
}
```

#### GET `/events/search`

Full-text search via Meilisearch.

**Query Parameters:**
```
q           string   required   Search query
lat         float    optional   For distance calculation
lng         float    optional   For distance calculation
category    string   optional
limit       int      optional   (default: 20, max: 100)
```

**Response: 200 OK**
```json
{
  "items": [ /* same as EventListItem */ ],
  "query": "concert park",
  "total": 5,
  "processing_time_ms": 12
}
```

---

### Error Response Format

All errors follow a consistent format:

```json
{
  "error": {
    "code": "EVENT_NOT_FOUND",
    "message": "Event with ID 550e8400-... does not exist.",
    "status": 404
  }
}
```

**Standard Error Codes:**

| HTTP Status | Code | Description |
|-------------|------|-------------|
| 400 | `VALIDATION_ERROR` | Invalid request parameters |
| 401 | `UNAUTHORIZED` | Missing or invalid token |
| 403 | `FORBIDDEN` | Insufficient permissions |
| 404 | `NOT_FOUND` | Resource not found |
| 429 | `RATE_LIMITED` | Too many requests |
| 500 | `INTERNAL_ERROR` | Unexpected server error |

---

## Caching Strategy

### Layer 1: Android Client Cache

```
Room Database (SQLite)
├── events table           # Cache nearby events (TTL: 15 min)
├── categories table       # Cache categories (TTL: 24 hours)
└── search_history table   # Recent searches (no TTL, user-managed)

DataStore Preferences
├── last_location          # Last known user location
├── filter_preferences     # User's default filters
└── cache_timestamps       # When each cache was last refreshed
```

### Layer 2: HTTP Caching

```
Response Headers:
  Cache-Control: public, max-age=300      # Events list: 5 min
  Cache-Control: public, max-age=3600     # Event detail: 1 hour
  Cache-Control: public, max-age=86400    # Categories: 24 hours
  ETag: "abc123"                          # Conditional requests

Client uses OkHttp cache interceptor (10MB disk cache).
```

### Layer 3: Server-Side Cache (Redis / Valkey)

```
Key Pattern                    TTL        Description
events:nearby:{geohash}:{cat}  5 min     Pre-computed nearby results
events:detail:{id}             30 min    Event detail
categories:all                 24 hours  Category list
search:popular                 1 hour    Popular search terms
rate:user:{id}                 1 min     Rate limit counter
```

### Cache Invalidation

- **Event created/updated/deleted** -> invalidate relevant `events:nearby:*` keys
- **Category changed** -> invalidate `categories:all`
- Client-side: stale-while-revalidate pattern (show cached, fetch fresh in background)

---

## Offline Support (MVP)

1. **Room database** caches the last 50 viewed events
2. **OkHttp cache** stores recent API responses
3. **No offline map tiles** in MVP (MapLibre caches tiles automatically for recently viewed areas)
4. When offline:
   - Show cached events on map and list
   - Display "Offline" banner
   - Queue any actions (favorites, etc.) for sync when online

---

## Location Indexing Strategy

### PostGIS Spatial Index

The `GIST` index on the `location` column enables fast spatial queries:

```sql
-- Finds events within 5km of a point in ~1-5ms (with index)
SELECT id, title,
       ST_Distance(location, ST_Point(-73.9654, 40.7829)::geography) AS distance
FROM events
WHERE ST_DWithin(location, ST_Point(-73.9654, 40.7829)::geography, 5000)
  AND status = 'active'
  AND start_date >= NOW()
ORDER BY distance
LIMIT 50;
```

### Geohash Clustering

For map marker clustering at low zoom levels:

```sql
-- Get event counts per geohash cell (for clustering)
SELECT ST_GeoHash(location::geometry, 5) AS geohash,
       COUNT(*) AS event_count,
       AVG(ST_X(location::geometry)) AS avg_lng,
       AVG(ST_Y(location::geometry)) AS avg_lat
FROM events
WHERE ST_DWithin(location, ST_Point(-73.9654, 40.7829)::geography, 50000)
  AND status = 'active'
GROUP BY geohash;
```

---

## Meilisearch Index Configuration

```json
{
  "index": "events",
  "primaryKey": "id",
  "searchableAttributes": [
    "title",
    "description",
    "address",
    "city",
    "tags"
  ],
  "filterableAttributes": [
    "category_slug",
    "city",
    "country",
    "status",
    "start_date"
  ],
  "sortableAttributes": [
    "start_date",
    "created_at"
  ],
  "rankingRules": [
    "words",
    "typo",
    "proximity",
    "attribute",
    "sort",
    "exactness"
  ]
}
```

Sync from PostgreSQL to Meilisearch via background task (Celery) on event create/update/delete.

---

*Next: [05 — Deployment & DevOps](./05-DEPLOYMENT-DEVOPS.md)*
