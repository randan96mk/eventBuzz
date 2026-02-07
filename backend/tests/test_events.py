"""Tests for the event endpoints.

These tests exercise the HTTP layer.  Database-dependent operations are
tested against the real DB when available, or expected to return a
connection error in CI environments without PostGIS.
"""

import pytest
from httpx import AsyncClient


# ---------------------------------------------------------------------------
# GET /api/v1/events/nearby
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_nearby_requires_lat_lng(async_client: AsyncClient) -> None:
    """Omitting required lat/lng query params should return 422."""
    response = await async_client.get("/api/v1/events/nearby")
    assert response.status_code == 422


@pytest.mark.asyncio
async def test_nearby_validates_lat_range(async_client: AsyncClient) -> None:
    """Latitude outside [-90, 90] should be rejected."""
    response = await async_client.get(
        "/api/v1/events/nearby", params={"lat": 100, "lng": -73.98}
    )
    assert response.status_code == 422


@pytest.mark.asyncio
async def test_nearby_validates_lng_range(async_client: AsyncClient) -> None:
    """Longitude outside [-180, 180] should be rejected."""
    response = await async_client.get(
        "/api/v1/events/nearby", params={"lat": 40.75, "lng": -200}
    )
    assert response.status_code == 422


# ---------------------------------------------------------------------------
# GET /api/v1/events/bubbles
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_bubbles_requires_lat_lng(async_client: AsyncClient) -> None:
    """Omitting required params should return 422."""
    response = await async_client.get("/api/v1/events/bubbles")
    assert response.status_code == 422


# ---------------------------------------------------------------------------
# GET /api/v1/events/search
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_search_requires_query(async_client: AsyncClient) -> None:
    """Omitting the `q` parameter should return 422."""
    response = await async_client.get("/api/v1/events/search")
    assert response.status_code == 422


# ---------------------------------------------------------------------------
# GET /api/v1/events/{id}
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_get_event_invalid_uuid(async_client: AsyncClient) -> None:
    """A malformed UUID should return 422."""
    response = await async_client.get("/api/v1/events/not-a-uuid")
    assert response.status_code == 422


# ---------------------------------------------------------------------------
# POST /api/v1/events  (admin-only)
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_create_event_requires_auth(async_client: AsyncClient) -> None:
    """Creating an event without a Bearer token should return 401 or 403."""
    response = await async_client.post(
        "/api/v1/events",
        json={
            "title": "Test Event",
            "category_id": 1,
            "latitude": 40.75,
            "longitude": -73.98,
            "start_date": "2026-06-01T18:00:00Z",
        },
    )
    assert response.status_code in (401, 403)


# ---------------------------------------------------------------------------
# DELETE /api/v1/events/{id}  (admin-only)
# ---------------------------------------------------------------------------


@pytest.mark.asyncio
async def test_delete_event_requires_auth(async_client: AsyncClient) -> None:
    """Deleting without auth should return 401 or 403."""
    response = await async_client.delete(
        "/api/v1/events/00000000-0000-0000-0000-000000000001"
    )
    assert response.status_code in (401, 403)
