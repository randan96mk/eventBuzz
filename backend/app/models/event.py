"""Event model — the central entity of EventBuzz."""

import uuid
from datetime import datetime

from geoalchemy2 import Geography
from sqlalchemy import (
    DateTime,
    ForeignKey,
    Integer,
    Numeric,
    String,
    Text,
    func,
)
from sqlalchemy.dialects.postgresql import JSONB, UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base
from app.models.category import Category  # noqa: F401 — keep for relationship resolution
from app.models.event_image import EventImage
from app.models.event_tag import EventTag  # noqa: F401
from app.models.user import User  # noqa: F401


class Event(Base):
    __tablename__ = "events"

    # -- Primary key --
    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4,
        server_default=func.gen_random_uuid(),
    )

    # -- Core fields --
    title: Mapped[str] = mapped_column(String(255), nullable=False, index=True)
    description: Mapped[str | None] = mapped_column(Text, nullable=True)

    # -- Category --
    category_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("categories.id"), nullable=False, index=True
    )

    # -- Location (PostGIS) --
    location: Mapped[str] = mapped_column(
        Geography(geometry_type="POINT", srid=4326),
        nullable=False,
    )
    address: Mapped[str | None] = mapped_column(String(500), nullable=True)
    city: Mapped[str | None] = mapped_column(String(150), nullable=True, index=True)
    country: Mapped[str | None] = mapped_column(String(100), nullable=True)

    # -- Date / time --
    start_date: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), nullable=False, index=True
    )
    end_date: Mapped[datetime | None] = mapped_column(
        DateTime(timezone=True), nullable=True
    )

    # -- Media / links --
    image_url: Mapped[str | None] = mapped_column(Text, nullable=True)
    ticket_url: Mapped[str | None] = mapped_column(Text, nullable=True)

    # -- Pricing --
    price_min: Mapped[float | None] = mapped_column(Numeric(10, 2), nullable=True)
    price_max: Mapped[float | None] = mapped_column(Numeric(10, 2), nullable=True)
    currency: Mapped[str] = mapped_column(String(3), nullable=False, default="USD")

    # -- Status & source --
    status: Mapped[str] = mapped_column(
        String(20), nullable=False, default="active", index=True
    )
    source: Mapped[str] = mapped_column(String(50), nullable=False, default="manual")
    external_id: Mapped[str | None] = mapped_column(String(255), nullable=True, unique=True)

    # -- Owner --
    created_by: Mapped[uuid.UUID | None] = mapped_column(
        UUID(as_uuid=True), ForeignKey("users.id"), nullable=True
    )

    # -- Flexible data --
    metadata_: Mapped[dict | None] = mapped_column(
        "metadata", JSONB, nullable=True, default=dict
    )

    # -- Timestamps --
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now(), nullable=False
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
        nullable=False,
    )

    # -- Relationships --
    category: Mapped["Category"] = relationship(
        "Category", back_populates="events", lazy="selectin"
    )
    creator: Mapped["User | None"] = relationship(
        "User", back_populates="events", lazy="selectin"
    )
    images: Mapped[list["EventImage"]] = relationship(
        "EventImage",
        back_populates="event",
        lazy="selectin",
        cascade="all, delete-orphan",
        order_by="EventImage.display_order",
    )
    tags: Mapped[list["EventTag"]] = relationship(
        "EventTag",
        lazy="selectin",
        cascade="all, delete-orphan",
    )

    def __repr__(self) -> str:
        return f"<Event {self.title!r} ({self.id})>"
