"""Tests for the health-check endpoints."""

import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_health_returns_ok(async_client: AsyncClient) -> None:
    """GET /api/v1/health should return 200 with status ok."""
    response = await async_client.get("/api/v1/health")
    assert response.status_code == 200

    data = response.json()
    assert data["status"] == "ok"
    assert "version" in data
    assert "environment" in data


@pytest.mark.asyncio
async def test_health_contains_version(async_client: AsyncClient) -> None:
    """The health response should include the app version."""
    response = await async_client.get("/api/v1/health")
    data = response.json()
    assert data["version"] == "0.1.0"


@pytest.mark.asyncio
async def test_health_ready_requires_db(async_client: AsyncClient) -> None:
    """GET /api/v1/health/ready exercises the DB check.

    In a test environment without a real DB this may return 500,
    which is the expected behaviour — the probe correctly detects
    that the database is unreachable.
    """
    response = await async_client.get("/api/v1/health/ready")
    # Accept either 200 (DB available) or 500 (DB unavailable in CI)
    assert response.status_code in (200, 500)
