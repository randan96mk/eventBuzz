"""Pydantic v2 schemas for the Event resource.

Includes minimal (bubble), list, detail, create/update, query-param,
and generic paginated-response models.
"""

from datetime import datetime
from typing import Generic, TypeVar
from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field

from app.schemas.category import CategoryOut

T = TypeVar("T")


# ---------------------------------------------------------------------------
# Read schemas
# ---------------------------------------------------------------------------


class EventBubble(BaseModel):
    """Minimal payload for rendering a map marker / bubble."""

    model_config = ConfigDict(from_attributes=True)

    id: UUID
    title: str
    latitude: float
    longitude: float
    category_id: int
    color_hex: str = "#6750A4"
    start_date: datetime


class EventListItem(BaseModel):
    """Card-level representation for event lists."""

    model_config = ConfigDict(from_attributes=True)

    id: UUID
    title: str
    description: str | None = None
    category: CategoryOut
    latitude: float
    longitude: float
    address: str | None = None
    city: str | None = None
    country: str | None = None
    start_date: datetime
    end_date: datetime | None = None
    image_url: str | None = None
    price_min: float | None = None
    price_max: float | None = None
    currency: str = "USD"
    status: str = "active"
    distance_meters: float | None = None


class EventImageOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: UUID
    image_url: str
    display_order: int


class EventTagOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    tag: str


class EventDetail(EventListItem):
    """Full event detail — extends list item with tags, images, and metadata."""

    ticket_url: str | None = None
    source: str = "manual"
    external_id: str | None = None
    tags: list[EventTagOut] = []
    images: list[EventImageOut] = []
    metadata_: dict | None = Field(None, alias="metadata_")
    created_at: datetime
    updated_at: datetime


# ---------------------------------------------------------------------------
# Write schemas
# ---------------------------------------------------------------------------


class EventCreate(BaseModel):
    """Payload for creating a new event."""

    title: str = Field(..., min_length=1, max_length=255)
    description: str | None = None
    category_id: int
    latitude: float = Field(..., ge=-90, le=90)
    longitude: float = Field(..., ge=-180, le=180)
    address: str | None = None
    city: str | None = None
    country: str | None = None
    start_date: datetime
    end_date: datetime | None = None
    image_url: str | None = None
    ticket_url: str | None = None
    price_min: float | None = Field(None, ge=0)
    price_max: float | None = Field(None, ge=0)
    currency: str = Field("USD", max_length=3)
    tags: list[str] = []
    metadata_: dict | None = None


class EventUpdate(BaseModel):
    """Payload for updating an existing event — all fields optional."""

    title: str | None = Field(None, min_length=1, max_length=255)
    description: str | None = None
    category_id: int | None = None
    latitude: float | None = Field(None, ge=-90, le=90)
    longitude: float | None = Field(None, ge=-180, le=180)
    address: str | None = None
    city: str | None = None
    country: str | None = None
    start_date: datetime | None = None
    end_date: datetime | None = None
    image_url: str | None = None
    ticket_url: str | None = None
    price_min: float | None = Field(None, ge=0)
    price_max: float | None = Field(None, ge=0)
    currency: str | None = Field(None, max_length=3)
    status: str | None = None
    tags: list[str] | None = None
    metadata_: dict | None = None


# ---------------------------------------------------------------------------
# Query-param schema
# ---------------------------------------------------------------------------


class EventsNearbyParams(BaseModel):
    """Validated query parameters for the nearby-events endpoint."""

    lat: float = Field(..., ge=-90, le=90, description="Latitude of the search center")
    lng: float = Field(..., ge=-180, le=180, description="Longitude of the search center")
    radius: float = Field(
        5000, ge=100, le=50000, description="Search radius in meters"
    )
    category_id: int | None = Field(None, description="Filter by category")
    status: str = Field("active", description="Filter by event status")
    date_from: datetime | None = Field(None, description="Events starting from this date")
    date_to: datetime | None = Field(None, description="Events starting before this date")
    page: int = Field(1, ge=1, description="Page number")
    page_size: int = Field(20, ge=1, le=100, description="Items per page")


# ---------------------------------------------------------------------------
# Generic paginated response
# ---------------------------------------------------------------------------


class PaginatedResponse(BaseModel, Generic[T]):
    """Generic paginated response wrapper."""

    items: list[T]
    total: int
    page: int
    page_size: int
    pages: int
