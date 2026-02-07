"""EventTag model — many-to-many relationship between events and free-form tags."""

import uuid

from sqlalchemy import ForeignKey, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column

from app.database import Base


class EventTag(Base):
    __tablename__ = "event_tags"

    event_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        ForeignKey("events.id", ondelete="CASCADE"),
        primary_key=True,
    )
    tag: Mapped[str] = mapped_column(String(50), primary_key=True)

    def __repr__(self) -> str:
        return f"<EventTag event_id={self.event_id} tag={self.tag!r}>"
