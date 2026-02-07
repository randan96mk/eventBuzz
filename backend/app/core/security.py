"""JWT / Keycloak authentication helpers.

Provides FastAPI dependencies for extracting and validating Bearer tokens
issued by Keycloak.  Currently uses a local symmetric decode as a stub;
production should fetch the JWKS from the Keycloak realm and validate
with the RS256 public key.

TODO: Replace stub with full Keycloak JWKS validation:
  1. Fetch JWKS from {KEYCLOAK_URL}/realms/{REALM}/protocol/openid-connect/certs
  2. Cache the JWK set (with TTL refresh).
  3. Validate token signature, audience, issuer, and expiry.
"""

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jose import JWTError, jwt

from app.config import Settings, get_settings

bearer_scheme = HTTPBearer(auto_error=False)


async def get_current_user(
    credentials: HTTPAuthorizationCredentials | None = Depends(bearer_scheme),
    settings: Settings = Depends(get_settings),
) -> dict:
    """Decode the JWT and return the payload as a dict.

    Raises 401 if the token is missing or cannot be decoded.
    """
    if credentials is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing authentication token",
            headers={"WWW-Authenticate": "Bearer"},
        )

    token = credentials.credentials

    try:
        # --- STUB: symmetric decode for local development ---
        # In production, replace with RS256 validation against Keycloak JWKS.
        payload = jwt.decode(
            token,
            settings.APP_SECRET_KEY,
            algorithms=["HS256"],
            options={
                "verify_aud": False,
                "verify_iss": False,
            },
        )
    except JWTError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Invalid token: {exc}",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc

    return payload


async def require_admin(
    user: dict = Depends(get_current_user),
) -> dict:
    """Ensure the current user has an admin role.

    Checks for ``"admin"`` in the ``realm_access.roles`` claim (Keycloak
    convention) or in a top-level ``roles`` list.

    Returns the user payload if authorized; raises 403 otherwise.
    """
    roles: list[str] = []

    # Keycloak nests roles under realm_access
    realm_access = user.get("realm_access")
    if isinstance(realm_access, dict):
        roles = realm_access.get("roles", [])

    # Fallback: top-level roles claim
    if not roles:
        roles = user.get("roles", [])

    if "admin" not in roles:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Admin role required",
        )

    return user
