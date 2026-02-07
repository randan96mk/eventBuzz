"""Seed default event categories.

Run with:
    python -m app.scripts.seed_categories
"""

import asyncio

from sqlalchemy import select

from app.database import async_session_factory, engine
from app.models.category import Category

DEFAULT_CATEGORIES: list[dict] = [
    {"name": "Music", "slug": "music", "color_hex": "#E91E63", "icon_name": "music_note"},
    {"name": "Sports", "slug": "sports", "color_hex": "#4CAF50", "icon_name": "sports"},
    {"name": "Arts & Theater", "slug": "arts-theater", "color_hex": "#9C27B0", "icon_name": "theater_comedy"},
    {"name": "Food & Drink", "slug": "food-drink", "color_hex": "#FF9800", "icon_name": "restaurant"},
    {"name": "Nightlife", "slug": "nightlife", "color_hex": "#673AB7", "icon_name": "nightlife"},
    {"name": "Community", "slug": "community", "color_hex": "#2196F3", "icon_name": "groups"},
    {"name": "Tech", "slug": "tech", "color_hex": "#00BCD4", "icon_name": "computer"},
    {"name": "Outdoors", "slug": "outdoors", "color_hex": "#8BC34A", "icon_name": "park"},
    {"name": "Family", "slug": "family", "color_hex": "#FFC107", "icon_name": "family_restroom"},
    {"name": "Workshops", "slug": "workshops", "color_hex": "#795548", "icon_name": "build"},
]


async def seed() -> None:
    """Insert default categories if they do not already exist."""
    async with async_session_factory() as session:
        existing = (await session.execute(select(Category.slug))).scalars().all()
        existing_slugs = set(existing)

        added = 0
        for cat_data in DEFAULT_CATEGORIES:
            if cat_data["slug"] not in existing_slugs:
                session.add(Category(**cat_data))
                added += 1

        await session.commit()
        print(f"Seeded {added} categories ({len(existing_slugs)} already existed).")

    await engine.dispose()


def main() -> None:
    asyncio.run(seed())


if __name__ == "__main__":
    main()
