"""Aggregate router for API v1.

Includes all v1 sub-routers with their prefixes.
"""

from fastapi import APIRouter

from app.api.v1.categories import router as categories_router
from app.api.v1.events import router as events_router
from app.api.v1.health import router as health_router

api_v1_router = APIRouter()

api_v1_router.include_router(health_router)
api_v1_router.include_router(events_router)
api_v1_router.include_router(categories_router)
