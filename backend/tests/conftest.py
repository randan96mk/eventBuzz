"""Shared pytest fixtures for the EventBuzz test suite."""

import asyncio
from collections.abc import AsyncGenerator

import pytest
from httpx import ASGITransport, AsyncClient

from app.config import Settings, get_settings
from app.main import create_app


def _test_settings() -> Settings:
    """Return settings overrides suitable for testing."""
    return Settings(
        DATABASE_URL="postgresql+asyncpg://eventbuzz:eventbuzz@localhost:5432/eventbuzz_test",
        ENVIRONMENT="test",
        APP_SECRET_KEY="test-secret-key",
        CORS_ORIGINS=["http://localhost:3000"],
    )


@pytest.fixture(scope="session")
def event_loop():
    """Create a single event loop for the entire test session."""
    loop = asyncio.new_event_loop()
    yield loop
    loop.close()


@pytest.fixture()
def app():
    """Create a fresh FastAPI app with test settings."""
    test_app = create_app()
    test_app.dependency_overrides[get_settings] = _test_settings
    return test_app


@pytest.fixture()
async def async_client(app) -> AsyncGenerator[AsyncClient, None]:
    """Provide an async HTTP client bound to the test app."""
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        yield client
