"""Category endpoints."""

from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_session
from app.models.category import Category
from app.schemas.category import CategoryOut

router = APIRouter(prefix="/categories", tags=["categories"])


@router.get(
    "",
    response_model=list[CategoryOut],
    summary="List all event categories",
)
async def list_categories(
    session: AsyncSession = Depends(get_session),
) -> list[CategoryOut]:
    """Return every category, ordered by name."""
    result = await session.execute(select(Category).order_by(Category.name))
    categories = result.scalars().all()
    return [CategoryOut.model_validate(c) for c in categories]
