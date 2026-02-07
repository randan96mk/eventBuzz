"""FastAPI application entry point.

Creates the app instance, configures middleware, includes routers,
and manages the application lifespan (startup / shutdown).
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import get_settings
from app.database import engine


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Manage startup and shutdown lifecycle events.

    On startup:  nothing extra needed — engine is created at import time.
    On shutdown: dispose the engine connection pool gracefully.
    """
    yield
    # Shutdown: dispose the async engine pool
    await engine.dispose()


def create_app() -> FastAPI:
    """Build and return the configured FastAPI application."""
    settings = get_settings()

    app = FastAPI(
        title=settings.APP_NAME,
        version=settings.APP_VERSION,
        description=(
            "EventBuzz API — discover and explore local events on an interactive map. "
            "Provides endpoints for browsing events by location, category, and date."
        ),
        lifespan=lifespan,
        docs_url="/docs" if not settings.is_production else None,
        redoc_url="/redoc" if not settings.is_production else None,
    )

    # -- CORS middleware --
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.CORS_ORIGINS,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # -- Include API routers --
    from app.api.v1.router import api_v1_router

    app.include_router(api_v1_router, prefix="/api/v1")

    return app


app = create_app()
