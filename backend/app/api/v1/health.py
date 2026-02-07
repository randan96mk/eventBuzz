"""Health and readiness endpoints."""

from fastapi import APIRouter, Depends
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_session, get_settings_dep
from app.config import Settings
from app.schemas.common import ErrorResponse, HealthResponse, ReadyResponse

router = APIRouter(prefix="/health", tags=["health"])


@router.get(
    "",
    response_model=HealthResponse,
    summary="Basic health check",
)
async def health(
    settings: Settings = Depends(get_settings_dep),
) -> HealthResponse:
    """Return basic application health information."""
    return HealthResponse(
        status="ok",
        version=settings.APP_VERSION,
        environment=settings.ENVIRONMENT,
    )


@router.get(
    "/ready",
    response_model=ReadyResponse,
    responses={503: {"model": ErrorResponse}},
    summary="Readiness probe — verifies database connectivity",
)
async def readiness(
    session: AsyncSession = Depends(get_session),
) -> ReadyResponse:
    """Check that the database is reachable."""
    await session.execute(text("SELECT 1"))
    return ReadyResponse(status="ok", database="connected")
