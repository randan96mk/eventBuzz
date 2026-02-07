"""Application configuration via Pydantic BaseSettings.

Settings are loaded from environment variables and an optional .env file.
"""

from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Central configuration for the EventBuzz API."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=True,
    )

    # -- Application --
    APP_NAME: str = "EventBuzz API"
    APP_VERSION: str = "0.1.0"
    ENVIRONMENT: str = "dev"  # dev | staging | production
    APP_SECRET_KEY: str = "change-me-in-production"

    # -- Database --
    DATABASE_URL: str = "postgresql+asyncpg://eventbuzz:eventbuzz@localhost:5432/eventbuzz"

    # -- Redis --
    REDIS_URL: str = "redis://localhost:6379/0"

    # -- Meilisearch --
    MEILISEARCH_URL: str = "http://localhost:7700"
    MEILISEARCH_KEY: str = ""

    # -- Keycloak --
    KEYCLOAK_URL: str = "http://localhost:8080"
    KEYCLOAK_REALM: str = "eventbuzz"

    # -- CORS --
    CORS_ORIGINS: list[str] = [
        "http://localhost:3000",
        "http://localhost:5173",
    ]

    # -- Pool settings --
    DB_POOL_SIZE: int = 20
    DB_MAX_OVERFLOW: int = 10
    DB_POOL_RECYCLE: int = 3600

    @property
    def is_production(self) -> bool:
        return self.ENVIRONMENT == "production"


@lru_cache
def get_settings() -> Settings:
    """Return a cached Settings instance."""
    return Settings()
