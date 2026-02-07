"""Event endpoints — the primary API surface of EventBuzz."""

import math
from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_session
from app.core.security import get_current_user, require_admin
from app.schemas.event import (
    EventBubble,
    EventCreate,
    EventDetail,
    EventListItem,
    EventsNearbyParams,
    EventUpdate,
    PaginatedResponse,
)
from app.services.event_service import EventService

router = APIRouter(prefix="/events", tags=["events"])


# ---------------------------------------------------------------------------
# Public read endpoints
# ---------------------------------------------------------------------------


@router.get(
    "/nearby",
    response_model=PaginatedResponse[EventListItem],
    summary="Get events near a location",
)
async def get_nearby_events(
    lat: float = Query(..., ge=-90, le=90),
    lng: float = Query(..., ge=-180, le=180),
    radius: float = Query(5000, ge=100, le=50000, description="Radius in meters"),
    category_id: int | None = Query(None),
    status_filter: str = Query("active", alias="status"),
    date_from: str | None = Query(None),
    date_to: str | None = Query(None),
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    session: AsyncSession = Depends(get_session),
) -> PaginatedResponse[EventListItem]:
    """Return paginated events within *radius* meters of the given point."""
    params = EventsNearbyParams(
        lat=lat,
        lng=lng,
        radius=radius,
        category_id=category_id,
        status=status_filter,
        page=page,
        page_size=page_size,
    )
    items, total = await EventService.get_nearby_events(session, params)
    pages = math.ceil(total / page_size) if total else 0
    return PaginatedResponse(
        items=items,
        total=total,
        page=page,
        page_size=page_size,
        pages=pages,
    )


@router.get(
    "/bubbles",
    response_model=list[EventBubble],
    summary="Minimal event data for map markers",
)
async def get_event_bubbles(
    lat: float = Query(..., ge=-90, le=90),
    lng: float = Query(..., ge=-180, le=180),
    radius: float = Query(5000, ge=100, le=50000),
    category_id: int | None = Query(None),
    session: AsyncSession = Depends(get_session),
) -> list[EventBubble]:
    """Return lightweight event bubbles for rendering map markers."""
    return await EventService.get_event_bubbles(
        session, lat=lat, lng=lng, radius=radius, category_id=category_id
    )


@router.get(
    "/search",
    response_model=PaginatedResponse[EventListItem],
    summary="Search events by text",
)
async def search_events(
    q: str = Query(..., min_length=1, max_length=200),
    category_id: int | None = Query(None),
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    session: AsyncSession = Depends(get_session),
) -> PaginatedResponse[EventListItem]:
    """Full-text search over events (falls back to ILIKE when Meilisearch is unavailable)."""
    items, total = await EventService.search_events(
        session, query=q, category_id=category_id, page=page, page_size=page_size
    )
    pages = math.ceil(total / page_size) if total else 0
    return PaginatedResponse(
        items=items,
        total=total,
        page=page,
        page_size=page_size,
        pages=pages,
    )


@router.get(
    "/{event_id}",
    response_model=EventDetail,
    summary="Get event details",
    responses={404: {"description": "Event not found"}},
)
async def get_event(
    event_id: UUID,
    session: AsyncSession = Depends(get_session),
) -> EventDetail:
    """Return the full detail of a single event."""
    event = await EventService.get_event_by_id(session, event_id)
    if event is None:
        raise HTTPException(status_code=404, detail="Event not found")
    return event


# ---------------------------------------------------------------------------
# Admin write endpoints
# ---------------------------------------------------------------------------


@router.post(
    "",
    response_model=EventDetail,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new event (admin)",
)
async def create_event(
    data: EventCreate,
    session: AsyncSession = Depends(get_session),
    current_user: dict = Depends(require_admin),
) -> EventDetail:
    """Create an event. Requires admin role."""
    return await EventService.create_event(session, data, created_by=current_user.get("sub"))


@router.put(
    "/{event_id}",
    response_model=EventDetail,
    summary="Update an event (admin)",
    responses={404: {"description": "Event not found"}},
)
async def update_event(
    event_id: UUID,
    data: EventUpdate,
    session: AsyncSession = Depends(get_session),
    current_user: dict = Depends(require_admin),
) -> EventDetail:
    """Update an existing event. Requires admin role."""
    event = await EventService.update_event(session, event_id, data)
    if event is None:
        raise HTTPException(status_code=404, detail="Event not found")
    return event


@router.delete(
    "/{event_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Soft-delete an event (admin)",
    responses={404: {"description": "Event not found"}},
)
async def delete_event(
    event_id: UUID,
    session: AsyncSession = Depends(get_session),
    current_user: dict = Depends(require_admin),
) -> None:
    """Soft-delete an event by setting status to 'deleted'. Requires admin role."""
    success = await EventService.delete_event(session, event_id)
    if not success:
        raise HTTPException(status_code=404, detail="Event not found")
