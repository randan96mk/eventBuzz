"""Shared FastAPI dependencies."""

from collections.abc import AsyncGenerator

from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.config import Settings, get_settings
from app.database import async_session_factory


async def get_session() -> AsyncGenerator[AsyncSession, None]:
    """Yield a transactional async database session.

    Commits on success, rolls back on error.
    """
    async with async_session_factory() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise


def get_settings_dep() -> Settings:
    """Return the cached application settings."""
    return get_settings()


# Type aliases for use in route signatures
SessionDep = Depends(get_session)
SettingsDep = Depends(get_settings_dep)
