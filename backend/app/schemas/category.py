"""Pydantic schemas for the Category resource."""

from datetime import datetime

from pydantic import BaseModel, ConfigDict


class CategoryOut(BaseModel):
    """Public representation of a category."""

    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str
    slug: str
    color_hex: str
    icon_name: str
    created_at: datetime
