"""Shared / utility Pydantic schemas."""

from pydantic import BaseModel


class ErrorResponse(BaseModel):
    """Standard error response envelope."""

    detail: str
    code: str | None = None


class HealthResponse(BaseModel):
    """Response for the health-check endpoint."""

    status: str = "ok"
    version: str
    environment: str


class ReadyResponse(BaseModel):
    """Response for the readiness probe."""

    status: str = "ok"
    database: str = "connected"
