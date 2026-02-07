"""Business-logic layer for Event operations.

All PostGIS spatial queries live here so that API routes remain thin.
"""

from uuid import UUID

from geoalchemy2 import func as geo_func
from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.event import Event
from app.models.event_tag import EventTag
from app.schemas.event import (
    EventBubble,
    EventCreate,
    EventDetail,
    EventImageOut,
    EventListItem,
    EventsNearbyParams,
    EventTagOut,
    EventUpdate,
)


def _point_wkt(lng: float, lat: float) -> str:
    """Return a WKT POINT string. Note: PostGIS uses (lng, lat) order."""
    return f"SRID=4326;POINT({lng} {lat})"


def _event_to_list_item(row, distance: float | None = None) -> EventListItem:
    """Map an ORM Event (with joined category) to an EventListItem schema."""
    event: Event = row[0] if hasattr(row, "__getitem__") else row
    dist = row[1] if hasattr(row, "__getitem__") and len(row) > 1 else distance

    return EventListItem(
        id=event.id,
        title=event.title,
        description=event.description,
        category=event.category,
        latitude=0.0,  # filled below
        longitude=0.0,
        address=event.address,
        city=event.city,
        country=event.country,
        start_date=event.start_date,
        end_date=event.end_date,
        image_url=event.image_url,
        price_min=float(event.price_min) if event.price_min is not None else None,
        price_max=float(event.price_max) if event.price_max is not None else None,
        currency=event.currency,
        status=event.status,
        distance_meters=float(dist) if dist is not None else None,
    )


class EventService:
    """Static methods encapsulating event business logic."""

    # ------------------------------------------------------------------
    # Nearby (spatial) query
    # ------------------------------------------------------------------

    @staticmethod
    async def get_nearby_events(
        session: AsyncSession,
        params: EventsNearbyParams,
    ) -> tuple[list[EventListItem], int]:
        """Return events within *params.radius* meters, ordered by distance."""
        ref_point = func.ST_GeogFromText(_point_wkt(params.lng, params.lat))

        distance_col = func.ST_Distance(Event.location, ref_point).label("distance")
        lng_col = func.ST_X(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "longitude"
        )
        lat_col = func.ST_Y(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "latitude"
        )

        base = (
            select(Event, distance_col, lng_col, lat_col)
            .where(
                func.ST_DWithin(Event.location, ref_point, params.radius),
                Event.status == params.status,
            )
        )

        if params.category_id is not None:
            base = base.where(Event.category_id == params.category_id)
        if params.date_from is not None:
            base = base.where(Event.start_date >= params.date_from)
        if params.date_to is not None:
            base = base.where(Event.start_date <= params.date_to)

        # Count
        count_q = select(func.count()).select_from(base.subquery())
        total = (await session.execute(count_q)).scalar_one()

        # Paginated data
        offset = (params.page - 1) * params.page_size
        rows = (
            await session.execute(
                base.order_by("distance").offset(offset).limit(params.page_size)
            )
        ).all()

        items: list[EventListItem] = []
        for row in rows:
            event, dist, lng, lat = row
            item = EventListItem(
                id=event.id,
                title=event.title,
                description=event.description,
                category=event.category,
                latitude=lat,
                longitude=lng,
                address=event.address,
                city=event.city,
                country=event.country,
                start_date=event.start_date,
                end_date=event.end_date,
                image_url=event.image_url,
                price_min=float(event.price_min) if event.price_min is not None else None,
                price_max=float(event.price_max) if event.price_max is not None else None,
                currency=event.currency,
                status=event.status,
                distance_meters=float(dist) if dist is not None else None,
            )
            items.append(item)

        return items, total

    # ------------------------------------------------------------------
    # Bubbles (lightweight map markers)
    # ------------------------------------------------------------------

    @staticmethod
    async def get_event_bubbles(
        session: AsyncSession,
        *,
        lat: float,
        lng: float,
        radius: float,
        category_id: int | None = None,
    ) -> list[EventBubble]:
        """Return minimal event data for rendering map markers."""
        ref_point = func.ST_GeogFromText(_point_wkt(lng, lat))

        lng_col = func.ST_X(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "longitude"
        )
        lat_col = func.ST_Y(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "latitude"
        )

        stmt = (
            select(Event, lng_col, lat_col)
            .where(
                func.ST_DWithin(Event.location, ref_point, radius),
                Event.status == "active",
            )
        )

        if category_id is not None:
            stmt = stmt.where(Event.category_id == category_id)

        rows = (await session.execute(stmt)).all()

        return [
            EventBubble(
                id=event.id,
                title=event.title,
                latitude=lat_val,
                longitude=lng_val,
                category_id=event.category_id,
                color_hex=event.category.color_hex if event.category else "#6750A4",
                start_date=event.start_date,
            )
            for event, lng_val, lat_val in rows
        ]

    # ------------------------------------------------------------------
    # Single event detail
    # ------------------------------------------------------------------

    @staticmethod
    async def get_event_by_id(
        session: AsyncSession,
        event_id: UUID,
    ) -> EventDetail | None:
        """Return full event detail or None."""
        lng_col = func.ST_X(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "longitude"
        )
        lat_col = func.ST_Y(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "latitude"
        )

        stmt = select(Event, lng_col, lat_col).where(Event.id == event_id)
        row = (await session.execute(stmt)).first()

        if row is None:
            return None

        event, lng_val, lat_val = row

        return EventDetail(
            id=event.id,
            title=event.title,
            description=event.description,
            category=event.category,
            latitude=lat_val,
            longitude=lng_val,
            address=event.address,
            city=event.city,
            country=event.country,
            start_date=event.start_date,
            end_date=event.end_date,
            image_url=event.image_url,
            ticket_url=event.ticket_url,
            price_min=float(event.price_min) if event.price_min is not None else None,
            price_max=float(event.price_max) if event.price_max is not None else None,
            currency=event.currency,
            status=event.status,
            source=event.source,
            external_id=event.external_id,
            tags=[EventTagOut(tag=t.tag) for t in event.tags],
            images=[
                EventImageOut(id=img.id, image_url=img.image_url, display_order=img.display_order)
                for img in event.images
            ],
            metadata_=event.metadata_,
            created_at=event.created_at,
            updated_at=event.updated_at,
        )

    # ------------------------------------------------------------------
    # Text search (ILIKE fallback)
    # ------------------------------------------------------------------

    @staticmethod
    async def search_events(
        session: AsyncSession,
        *,
        query: str,
        category_id: int | None = None,
        page: int = 1,
        page_size: int = 20,
    ) -> tuple[list[EventListItem], int]:
        """Search events by title/description using ILIKE.

        TODO: Integrate Meilisearch for full-text search when available.
        """
        pattern = f"%{query}%"

        lng_col = func.ST_X(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "longitude"
        )
        lat_col = func.ST_Y(func.ST_GeomFromWKB(Event.location.cast_to_geometry())).label(
            "latitude"
        )

        base = (
            select(Event, lng_col, lat_col)
            .where(
                Event.status == "active",
                (Event.title.ilike(pattern)) | (Event.description.ilike(pattern)),
            )
        )

        if category_id is not None:
            base = base.where(Event.category_id == category_id)

        count_q = select(func.count()).select_from(base.subquery())
        total = (await session.execute(count_q)).scalar_one()

        offset = (page - 1) * page_size
        rows = (
            await session.execute(base.order_by(Event.start_date).offset(offset).limit(page_size))
        ).all()

        items = [
            EventListItem(
                id=event.id,
                title=event.title,
                description=event.description,
                category=event.category,
                latitude=lat_val,
                longitude=lng_val,
                address=event.address,
                city=event.city,
                country=event.country,
                start_date=event.start_date,
                end_date=event.end_date,
                image_url=event.image_url,
                price_min=float(event.price_min) if event.price_min is not None else None,
                price_max=float(event.price_max) if event.price_max is not None else None,
                currency=event.currency,
                status=event.status,
            )
            for event, lng_val, lat_val in rows
        ]

        return items, total

    # ------------------------------------------------------------------
    # Create
    # ------------------------------------------------------------------

    @staticmethod
    async def create_event(
        session: AsyncSession,
        data: EventCreate,
        created_by: str | None = None,
    ) -> EventDetail:
        """Persist a new event and return its detail."""
        event = Event(
            title=data.title,
            description=data.description,
            category_id=data.category_id,
            location=_point_wkt(data.longitude, data.latitude),
            address=data.address,
            city=data.city,
            country=data.country,
            start_date=data.start_date,
            end_date=data.end_date,
            image_url=data.image_url,
            ticket_url=data.ticket_url,
            price_min=data.price_min,
            price_max=data.price_max,
            currency=data.currency,
            created_by=created_by,
            metadata_=data.metadata_,
        )

        # Tags
        for tag_name in data.tags:
            event.tags.append(EventTag(tag=tag_name))

        session.add(event)
        await session.flush()
        await session.refresh(event)

        return await EventService.get_event_by_id(session, event.id)  # type: ignore[return-value]

    # ------------------------------------------------------------------
    # Update
    # ------------------------------------------------------------------

    @staticmethod
    async def update_event(
        session: AsyncSession,
        event_id: UUID,
        data: EventUpdate,
    ) -> EventDetail | None:
        """Update an existing event. Returns None if not found."""
        stmt = select(Event).where(Event.id == event_id)
        result = await session.execute(stmt)
        event = result.scalar_one_or_none()

        if event is None:
            return None

        update_data = data.model_dump(exclude_unset=True)

        # Handle lat/lng -> location
        lat = update_data.pop("latitude", None)
        lng = update_data.pop("longitude", None)
        if lat is not None and lng is not None:
            event.location = _point_wkt(lng, lat)

        # Handle tags replacement
        tags = update_data.pop("tags", None)
        if tags is not None:
            # Remove existing tags and replace
            event.tags.clear()
            for tag_name in tags:
                event.tags.append(EventTag(tag=tag_name))

        # Apply remaining scalar fields
        for field, value in update_data.items():
            if hasattr(event, field):
                setattr(event, field, value)

        await session.flush()
        await session.refresh(event)

        return await EventService.get_event_by_id(session, event.id)  # type: ignore[return-value]

    # ------------------------------------------------------------------
    # Soft delete
    # ------------------------------------------------------------------

    @staticmethod
    async def delete_event(
        session: AsyncSession,
        event_id: UUID,
    ) -> bool:
        """Soft-delete an event by setting status='deleted'. Returns False if not found."""
        stmt = select(Event).where(Event.id == event_id)
        result = await session.execute(stmt)
        event = result.scalar_one_or_none()

        if event is None:
            return False

        event.status = "deleted"
        await session.flush()
        return True
