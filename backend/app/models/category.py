"""Category model for event classification."""

from datetime import datetime

from sqlalchemy import DateTime, Integer, String, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class Category(Base):
    __tablename__ = "categories"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)
    slug: Mapped[str] = mapped_column(String(100), unique=True, nullable=False, index=True)
    color_hex: Mapped[str] = mapped_column(String(7), nullable=False, default="#6750A4")
    icon_name: Mapped[str] = mapped_column(String(50), nullable=False, default="event")

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now(), nullable=False
    )

    # -- Relationships --
    events: Mapped[list["Event"]] = relationship(  # noqa: F821
        "Event", back_populates="category", lazy="selectin"
    )

    def __repr__(self) -> str:
        return f"<Category {self.name!r} (id={self.id})>"
