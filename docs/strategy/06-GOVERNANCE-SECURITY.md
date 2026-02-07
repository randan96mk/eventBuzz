# 06 — Governance & Security

---

## Security Architecture Overview

```
+-------------------------------------------------------------------+
|                         SECURITY LAYERS                            |
|                                                                    |
|  +-------------------------------------------------------------+  |
|  |  Layer 1: Network Security                                  |  |
|  |  - TLS 1.3 (Caddy auto-HTTPS)                              |  |
|  |  - Firewall (UFW: only 80, 443, 22 open)                   |  |
|  |  - Rate limiting (Caddy + Redis)                            |  |
|  +-------------------------------------------------------------+  |
|  |  Layer 2: Authentication & Authorization                    |  |
|  |  - Keycloak (OAuth 2.0 / OIDC)                             |  |
|  |  - JWT validation on every protected endpoint               |  |
|  |  - RBAC (user, organizer, admin)                            |  |
|  +-------------------------------------------------------------+  |
|  |  Layer 3: Application Security                              |  |
|  |  - Input validation (Pydantic)                              |  |
|  |  - SQL injection prevention (SQLAlchemy parameterized)      |  |
|  |  - CORS policy                                              |  |
|  |  - Content Security Policy                                  |  |
|  +-------------------------------------------------------------+  |
|  |  Layer 4: Data Security                                     |  |
|  |  - Encrypted at rest (disk encryption)                      |  |
|  |  - Encrypted in transit (TLS)                               |  |
|  |  - Minimal PII collection                                   |  |
|  |  - Secrets management (env vars, no hardcoding)             |  |
|  +-------------------------------------------------------------+  |
|  |  Layer 5: Monitoring & Audit                                |  |
|  |  - Structured logging                                       |  |
|  |  - Audit trail for admin actions                            |  |
|  |  - Sentry error tracking                                    |  |
|  |  - Prometheus alerting                                      |  |
|  +-------------------------------------------------------------+  |
+-------------------------------------------------------------------+
```

---

## Authentication & Authorization

### Keycloak Configuration

#### Realm: `eventbuzz`

| Setting | Value |
|---------|-------|
| Realm name | eventbuzz |
| User registration | Enabled |
| Email verification | Enabled (Phase 2, optional for MVP) |
| Password policy | Min 8 chars, at least 1 uppercase, 1 number |
| Brute force protection | Enabled (lock after 5 failed attempts) |
| Session timeout | Access token: 5 min, Refresh token: 30 days |

#### Client: `eventbuzz-android`

| Setting | Value |
|---------|-------|
| Client type | Public (no client secret for mobile) |
| Authentication flow | Authorization Code + PKCE |
| Redirect URIs | `eventbuzz://callback`, `http://localhost:*` (dev) |
| Web origins | `+` (allow all from redirect) |
| PKCE method | S256 |

#### Roles

| Role | Permissions |
|------|------------|
| `user` | View events, search, filter, view profile |
| `organizer` | All user + create/edit own events |
| `admin` | All organizer + manage all events, users, categories |

#### JWT Token Structure

```json
{
  "sub": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "realm_access": {
    "roles": ["user"]
  },
  "email": "user@example.com",
  "preferred_username": "johndoe",
  "name": "John Doe",
  "iat": 1709251200,
  "exp": 1709251500,
  "iss": "https://api.eventbuzz.app/auth/realms/eventbuzz"
}
```

### Android Auth Implementation

```
1. User taps "Sign In"
2. App generates PKCE code_verifier + code_challenge
3. App opens Custom Chrome Tab to Keycloak authorize endpoint:
   GET /auth/realms/eventbuzz/protocol/openid-connect/auth
     ?client_id=eventbuzz-android
     &response_type=code
     &redirect_uri=eventbuzz://callback
     &scope=openid profile email
     &code_challenge=<S256_hash>
     &code_challenge_method=S256
4. User authenticates in browser
5. Keycloak redirects to eventbuzz://callback?code=<auth_code>
6. App exchanges code for tokens:
   POST /auth/realms/eventbuzz/protocol/openid-connect/token
     grant_type=authorization_code
     &code=<auth_code>
     &redirect_uri=eventbuzz://callback
     &client_id=eventbuzz-android
     &code_verifier=<verifier>
7. App stores tokens in EncryptedSharedPreferences
8. App attaches access token to API requests:
   Authorization: Bearer <access_token>
9. On token expiry, refresh silently:
   POST /auth/.../token
     grant_type=refresh_token
     &refresh_token=<refresh_token>
     &client_id=eventbuzz-android
```

### Guest Mode

- No authentication required for read-only operations
- Guest users get a local UUID stored in DataStore
- Limits apply: no favorites, no event creation
- Upgrade to full account preserves local data (by associating UUID)

---

## API Security

### Rate Limiting

Implemented via Redis token bucket at the Caddy/application level:

| Endpoint Pattern | Limit | Window | Scope |
|-----------------|-------|--------|-------|
| `GET /api/v1/events/*` | 60 requests | 1 min | Per IP |
| `GET /api/v1/search` | 30 requests | 1 min | Per IP |
| `POST /api/v1/events` | 10 requests | 1 min | Per user |
| `POST /auth/*/token` | 10 requests | 1 min | Per IP |
| Global | 300 requests | 1 min | Per IP |

Response when rate limited:
```
HTTP/1.1 429 Too Many Requests
Retry-After: 30

{
  "error": {
    "code": "RATE_LIMITED",
    "message": "Rate limit exceeded. Try again in 30 seconds.",
    "status": 429
  }
}
```

### CORS Policy

```python
# FastAPI CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://admin.eventbuzz.app",  # Admin dashboard
    ],
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["Authorization", "Content-Type"],
    allow_credentials=True,
    max_age=3600,
)
# Note: Mobile apps don't use CORS (not browser-based).
# CORS is only for web admin dashboard.
```

### Input Validation

All inputs validated via Pydantic before reaching business logic:

```python
# Example: Coordinate validation
class LocationQuery(BaseModel):
    lat: float = Field(..., ge=-90, le=90, description="Latitude")
    lng: float = Field(..., ge=-180, le=180, description="Longitude")
    radius: int = Field(default=5000, ge=100, le=50000, description="Radius in meters")

# Example: Text sanitization
class EventCreate(BaseModel):
    title: str = Field(..., min_length=3, max_length=255)
    description: str | None = Field(None, max_length=5000)

    @field_validator("title", "description")
    @classmethod
    def strip_html(cls, v: str | None) -> str | None:
        if v is None:
            return v
        return bleach.clean(v, tags=[], strip=True)
```

### SQL Injection Prevention

- All database queries use SQLAlchemy ORM or parameterized queries
- No raw SQL string concatenation
- PostGIS queries use ST_ functions with bound parameters

```python
# Safe spatial query
stmt = (
    select(Event)
    .where(
        func.ST_DWithin(
            Event.location,
            func.ST_Point(lng, lat, type_=Geography),
            radius,
        )
    )
)
```

### API Key Management (Internal Services)

```
Meilisearch  -> Master key in env var, search key for read-only
MinIO        -> Access/secret key pair in env var
Keycloak     -> Admin credentials in env var
PostgreSQL   -> Password in env var

NO secrets in:
- Source code
- Docker images
- Git history
- Client-side code
```

---

## Data Privacy

### Data Collection (Minimal)

| Data Point | Collected | Stored | Purpose | Retention |
|------------|-----------|--------|---------|-----------|
| Email | Yes (if registered) | Keycloak DB | Authentication | Until account deletion |
| Display name | Optional | Keycloak DB | User profile | Until account deletion |
| Device location | Yes (with permission) | NOT stored server-side | Nearby event queries | Ephemeral (request only) |
| Search queries | Yes | PostgreSQL | Search improvement | 90 days, then anonymized |
| App usage analytics | Yes (anonymous) | PostHog | Product improvement | 1 year |
| IP address | Yes | Logs | Security, rate limiting | 30 days |
| Device info | Minimal (OS version) | Analytics | Bug fixing | 1 year |

### What We Do NOT Collect

- Precise location history
- Contacts
- Browsing history
- Photos or media
- Financial information (MVP has no payments)

### User Rights (GDPR-Aligned)

| Right | Implementation |
|-------|---------------|
| Right to access | API endpoint: `GET /api/v1/users/me/data` returns all stored data |
| Right to rectification | User can edit profile in app and Keycloak |
| Right to erasure | API endpoint: `DELETE /api/v1/users/me` triggers cascade deletion |
| Right to data portability | Export endpoint: `GET /api/v1/users/me/export` (JSON) |
| Right to restrict processing | User can disable analytics in app settings |
| Right to object | Opt-out of non-essential processing via settings |

### Data Deletion Cascade

When a user requests account deletion:
```
1. Mark account as "pending deletion" (grace period: 30 days)
2. After 30 days:
   - Delete Keycloak account
   - Delete user record from PostgreSQL
   - Delete user's events (or transfer to "system" user if public)
   - Anonymize analytics data
   - Purge from search index
   - Log deletion for audit trail (anonymized)
```

---

## Privacy Policy Requirements

The app must include a privacy policy covering:

1. **What data is collected** and why
2. **How data is stored** and protected
3. **Third-party services** used (list each)
4. **User rights** and how to exercise them
5. **Contact information** for privacy inquiries
6. **Cookie/tracking policy** (for web admin)
7. **Children's privacy** (if applicable, COPPA)
8. **International transfers** (if data crosses borders)
9. **Policy update process**

Host the privacy policy at: `https://eventbuzz.app/privacy`

---

## Audit Logging

### What Gets Logged

| Action | Severity | Data Logged |
|--------|----------|-------------|
| User login | INFO | user_id, timestamp, method |
| User logout | INFO | user_id, timestamp |
| Failed login | WARN | IP, timestamp, email_attempted |
| Event created | INFO | user_id, event_id, timestamp |
| Event updated | INFO | user_id, event_id, changed_fields |
| Event deleted | WARN | user_id, event_id, timestamp |
| Admin action | WARN | admin_id, action, target, timestamp |
| Rate limit hit | WARN | IP, endpoint, timestamp |
| Permission denied | WARN | user_id, endpoint, timestamp |
| System error | ERROR | error_type, stack_trace, request_id |

### Log Format (Structured JSON)

```json
{
  "timestamp": "2026-03-15T19:00:00.123Z",
  "level": "INFO",
  "service": "event-service",
  "request_id": "req_abc123",
  "action": "event.created",
  "user_id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "resource": {
    "type": "event",
    "id": "550e8400-e29b-41d4-a716-446655440000"
  },
  "ip": "203.0.113.50",
  "user_agent": "EventBuzz-Android/1.0.0"
}
```

### Log Retention

| Log Type | Retention | Storage |
|----------|-----------|---------|
| Application logs | 30 days | Loki |
| Audit logs | 1 year | PostgreSQL (audit_logs table) |
| Security events | 1 year | PostgreSQL + alerts |
| Analytics | 1 year | PostHog |
| Error traces | 90 days | Sentry |

---

## Security Checklist (MVP Launch)

### Server
- [ ] UFW firewall: only ports 80, 443, 22 open
- [ ] SSH: key-only auth, no root login, non-standard port
- [ ] Fail2ban installed
- [ ] Automatic security updates enabled
- [ ] Disk encryption enabled

### Application
- [ ] All secrets in environment variables (not in code)
- [ ] .env excluded in .gitignore
- [ ] Pydantic validation on all inputs
- [ ] SQL injection prevention verified
- [ ] XSS prevention on any user-generated content
- [ ] Rate limiting configured
- [ ] CORS policy set

### Authentication
- [ ] PKCE enabled for mobile client
- [ ] Token expiry configured (5 min access, 30 day refresh)
- [ ] Brute force protection enabled
- [ ] Password policy enforced
- [ ] Tokens stored in EncryptedSharedPreferences (Android)

### Data
- [ ] TLS for all connections
- [ ] Database credentials rotated from defaults
- [ ] Backups encrypted
- [ ] PII inventory documented
- [ ] Data deletion flow tested

### Monitoring
- [ ] Error alerting configured
- [ ] Failed login monitoring
- [ ] Rate limit breach alerting
- [ ] Uptime monitoring

---

## Dependency Security

### Backend (Python)
```bash
# Check for known vulnerabilities
pip-audit

# Pin all dependencies in pyproject.toml
# Use Dependabot or Renovate for automated updates
```

### Android (Kotlin/Gradle)
```bash
# Gradle dependency verification
./gradlew dependencyCheckAnalyze

# Use Gradle version catalog for consistent versions
# Enable Dependabot for automated PRs
```

### GitHub Actions
```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "pip"
    directory: "/backend"
    schedule:
      interval: "weekly"
  - package-ecosystem: "gradle"
    directory: "/android"
    schedule:
      interval: "weekly"
  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
```

---

*Next: [07 — AI-Assisted Development](./07-AI-ASSISTED-DEVELOPMENT.md)*
