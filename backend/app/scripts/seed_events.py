"""Seed 50 sample events with realistic data.

Run with:
    python -m app.scripts.seed_events

Uses random locations within a configurable city bounding box
(default: New York City).
"""

import asyncio
import random
from datetime import datetime, timedelta, timezone

from sqlalchemy import func, select

from app.database import async_session_factory, engine
from app.models.category import Category
from app.models.event import Event
from app.models.event_tag import EventTag

# ---------------------------------------------------------------------------
# NYC bounding box (default) — override by changing these values
# ---------------------------------------------------------------------------
BBOX_LAT_MIN = 40.6892
BBOX_LAT_MAX = 40.8200
BBOX_LNG_MIN = -74.0200
BBOX_LNG_MAX = -73.9100

# ---------------------------------------------------------------------------
# Realistic sample data pools
# ---------------------------------------------------------------------------

MUSIC_EVENTS = [
    "Jazz Under the Stars",
    "Indie Rock Showcase",
    "Summer Symphony in the Park",
    "Hip-Hop Block Party",
    "Acoustic Open Mic Night",
]

SPORTS_EVENTS = [
    "5K Charity Run",
    "Basketball Tournament",
    "Yoga in the Park",
    "Beach Volleyball Meetup",
    "Cycling Grand Tour",
]

ARTS_EVENTS = [
    "Contemporary Art Exhibition",
    "Stand-Up Comedy Night",
    "Shakespeare in the Park",
    "Street Art Walking Tour",
    "Pottery Workshop",
]

FOOD_EVENTS = [
    "International Food Festival",
    "Wine Tasting Evening",
    "Vegan Cooking Class",
    "Farmers Market Sunday",
    "Craft Beer Festival",
]

NIGHTLIFE_EVENTS = [
    "Rooftop DJ Set",
    "Latin Dance Night",
    "Silent Disco",
    "Karaoke Battle",
    "Neon Glow Party",
]

COMMUNITY_EVENTS = [
    "Neighborhood Cleanup",
    "Cultural Heritage Festival",
    "Local Business Expo",
    "Book Swap Meetup",
    "Community Town Hall",
]

TECH_EVENTS = [
    "AI & Machine Learning Meetup",
    "Startup Pitch Night",
    "Hackathon Weekend",
    "Web3 Developer Workshop",
    "Cloud Architecture Summit",
]

OUTDOORS_EVENTS = [
    "Sunset Kayaking Trip",
    "Urban Bird-Watching Walk",
    "Mountain Trail Hike",
    "Stargazing Night",
    "Botanical Garden Tour",
]

FAMILY_EVENTS = [
    "Kids Science Fair",
    "Family Movie Night Outdoors",
    "Petting Zoo Visit",
    "Easter Egg Hunt",
    "Children's Story Time",
]

WORKSHOP_EVENTS = [
    "Watercolor Painting Workshop",
    "Intro to Photography",
    "Creative Writing Bootcamp",
    "Leather Crafting Class",
    "Candle Making Workshop",
]

EVENTS_BY_SLUG: dict[str, list[str]] = {
    "music": MUSIC_EVENTS,
    "sports": SPORTS_EVENTS,
    "arts-theater": ARTS_EVENTS,
    "food-drink": FOOD_EVENTS,
    "nightlife": NIGHTLIFE_EVENTS,
    "community": COMMUNITY_EVENTS,
    "tech": TECH_EVENTS,
    "outdoors": OUTDOORS_EVENTS,
    "family": FAMILY_EVENTS,
    "workshops": WORKSHOP_EVENTS,
}

TAGS_POOL: dict[str, list[str]] = {
    "music": ["live-music", "concert", "festival", "jazz", "indie"],
    "sports": ["fitness", "running", "tournament", "outdoor", "wellness"],
    "arts-theater": ["art", "comedy", "theater", "gallery", "performance"],
    "food-drink": ["food", "wine", "cooking", "vegan", "craft-beer"],
    "nightlife": ["dj", "dance", "party", "rooftop", "club"],
    "community": ["volunteer", "networking", "culture", "meetup", "charity"],
    "tech": ["ai", "startup", "hackathon", "developer", "cloud"],
    "outdoors": ["nature", "hiking", "kayaking", "adventure", "wildlife"],
    "family": ["kids", "family-friendly", "education", "fun", "interactive"],
    "workshops": ["workshop", "craft", "creative", "hands-on", "learning"],
}

NYC_STREETS = [
    "123 Broadway",
    "456 5th Avenue",
    "789 Park Avenue",
    "101 West 42nd Street",
    "200 Central Park West",
    "55 Water Street",
    "300 Madison Avenue",
    "88 Greenwich Street",
    "42 Canal Street",
    "150 East 14th Street",
]

DESCRIPTIONS = [
    "Join us for an unforgettable experience in the heart of New York City. "
    "This event brings together enthusiasts from all walks of life.",
    "A vibrant gathering celebrating creativity, community, and connection. "
    "Don't miss this unique opportunity to explore something new.",
    "Whether you're a seasoned pro or a curious beginner, this event has "
    "something for everyone. Come ready to be inspired!",
    "Experience the best the city has to offer at this carefully curated event. "
    "Tickets are limited, so grab yours today.",
    "An exciting event featuring top talent, great food, and amazing atmosphere. "
    "Perfect for a memorable outing with friends or family.",
]

IMAGE_URLS = [
    "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
    "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?w=800",
    "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800",
    "https://images.unsplash.com/photo-1505236858219-8359eb29e329?w=800",
    "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=800",
]


def _random_point() -> str:
    lat = random.uniform(BBOX_LAT_MIN, BBOX_LAT_MAX)
    lng = random.uniform(BBOX_LNG_MIN, BBOX_LNG_MAX)
    return f"SRID=4326;POINT({lng} {lat})"


async def seed() -> None:
    """Insert 50 sample events spread across all categories."""
    async with async_session_factory() as session:
        # Fetch categories
        result = await session.execute(select(Category))
        categories = result.scalars().all()

        if not categories:
            print(
                "No categories found. Run  python -m app.scripts.seed_categories  first."
            )
            return

        cat_map = {c.slug: c for c in categories}

        # Check how many events already exist
        count = (await session.execute(select(func.count(Event.id)))).scalar_one()
        if count >= 50:
            print(f"Already {count} events in the database — skipping seed.")
            return

        now = datetime.now(tz=timezone.utc)
        created = 0

        for slug, titles in EVENTS_BY_SLUG.items():
            category = cat_map.get(slug)
            if category is None:
                continue

            for title in titles:
                start = now + timedelta(
                    days=random.randint(1, 90),
                    hours=random.randint(8, 21),
                )
                duration_hours = random.choice([1, 2, 3, 4, 8])
                end = start + timedelta(hours=duration_hours)
                price_min = round(random.uniform(0, 50), 2) if random.random() > 0.3 else None
                price_max = (
                    round(price_min + random.uniform(10, 100), 2)
                    if price_min is not None
                    else None
                )

                event = Event(
                    title=title,
                    description=random.choice(DESCRIPTIONS),
                    category_id=category.id,
                    location=_random_point(),
                    address=random.choice(NYC_STREETS),
                    city="New York",
                    country="US",
                    start_date=start,
                    end_date=end,
                    image_url=random.choice(IMAGE_URLS),
                    price_min=price_min,
                    price_max=price_max,
                    currency="USD",
                    status="active",
                    source="seed",
                )

                # Add 1-3 random tags
                tag_pool = TAGS_POOL.get(slug, [])
                chosen_tags = random.sample(tag_pool, k=min(random.randint(1, 3), len(tag_pool)))
                for t in chosen_tags:
                    event.tags.append(EventTag(tag=t))

                session.add(event)
                created += 1

        await session.commit()
        print(f"Seeded {created} events across {len(EVENTS_BY_SLUG)} categories.")

    await engine.dispose()


def main() -> None:
    asyncio.run(seed())


if __name__ == "__main__":
    main()
