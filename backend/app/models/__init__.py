"""SQLAlchemy models package.

All models are imported here so that Alembic's ``target_metadata``
(which uses ``Base.metadata``) can discover every table.
"""

from app.database import Base
from app.models.category import Category
from app.models.event import Event
from app.models.event_image import EventImage
from app.models.event_tag import EventTag
from app.models.user import User

__all__ = [
    "Base",
    "Category",
    "Event",
    "EventImage",
    "EventTag",
    "User",
]
