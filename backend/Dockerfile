# ---------- Stage 1: Build ----------
FROM python:3.12-slim AS builder

WORKDIR /build

# Install system deps needed for building wheels (e.g. for asyncpg, geoalchemy2)
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc libpq-dev && \
    rm -rf /var/lib/apt/lists/*

COPY pyproject.toml ./

# Install Python dependencies into a virtual environment
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir .


# ---------- Stage 2: Runtime ----------
FROM python:3.12-slim AS runtime

WORKDIR /app

# Runtime system libraries
RUN apt-get update && \
    apt-get install -y --no-install-recommends libpq5 && \
    rm -rf /var/lib/apt/lists/*

# Copy the virtual environment from the builder
COPY --from=builder /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Copy application source
COPY alembic.ini ./
COPY alembic/ ./alembic/
COPY app/ ./app/

# Copy entrypoint
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh

# Non-root user
RUN adduser --disabled-password --gecos "" appuser && \
    chown -R appuser:appuser /app
USER appuser

EXPOSE 8000

ENTRYPOINT ["./entrypoint.sh"]
