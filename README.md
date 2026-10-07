# NexWare Distribution Platform â€” Backend

Enterprise electronics distribution and warehouse management API built as a **Spring Boot 3.4** modular monolith (Spring Modulith) on **Java 21**, with **PostgreSQL**, **Flyway**, optional **Redis** and **Kafka**.

## Features

| Module | Capabilities |
|--------|----------------|
| **Auth** | JWT register/login/refresh/logout, role-based access |
| **Product** | Categories, products, serial numbers, supplier mapping |
| **Warehouse** | Sites, locations, stock transfers |
| **Inventory** | Stock levels, adjustments, movements, low-stock alerts |
| **Reservation** | Reserve/release stock, scheduled expiry |
| **Supplier** | Supplier CRUD and search |
| **Purchase order** | Draft â†’ approve â†’ receive workflow |
| **Notification** | In-app notifications for domain events |
| **Audit** | HTTP audit trail (admin API) |
| **User** | Admin user search, update roles/status, soft delete |

API base path: `/api/v1`  
OpenAPI UI: `/swagger-ui.html`  
Health: `/actuator/health`

## Prerequisites

- **JDK 21**
- **Maven 3.9+** (or use `./mvnw` after generating the wrapper)
- **PostgreSQL 16** (Neon, Docker, or local)
- **Docker** (optional â€” for Compose infra and Testcontainers)
- **Redis** & **Kafka** (optional â€” disable with env flags, see below)

## Quick start (local)

### 1. Infrastructure with Docker Compose

```bash
docker compose up -d postgres redis kafka zookeeper
```

### 2. Configure environment

```bash
cp .env.example .env
```

Edit `.env` with your database credentials and a secure `JWT_SECRET` (minimum 32 characters).

### 3. Run the application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Or on Windows:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### 4. Explore the API

- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## Demo users (after Flyway V2 seed)

All seeded users use password: `Password123!`

| Email | Role |
|-------|------|
| `admin@nexware.lk` | ADMIN |
| `maya.fernando@nexware.lk` | WAREHOUSE_MANAGER |
| `kasun.perera@nexware.lk` | INVENTORY_STAFF |
| `nimali.wickramasinghe@nexware.lk` | PROCUREMENT_OFFICER |

Example login:

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nexware.lk","password":"Password123!"}'
```

Admin user management (requires `ADMIN` role): `GET/PUT/DELETE /api/v1/users`

## Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile (`dev`, `docker`, `test`) | `dev` |
| `JWT_SECRET` | HS256 signing secret (required in prod) | dev placeholder |
| `REDIS_ENABLED` | Enable Redis (blacklist, reservation TTL cache) | `true` |
| `KAFKA_ENABLED` | Enable Kafka publish/consume | `true` |
| `RESERVATION_EXPIRY_CRON` | Cron for auto-expiring reservations | `0 */5 * * * *` |
| `NEON_DB_*` | PostgreSQL connection (see `.env.example`) | â€” |

When `REDIS_ENABLED=false` or `KAFKA_ENABLED=false`, Spring Boot auto-configuration for those services is skipped automatically so the app can run with PostgreSQL only.

## Database migrations

Flyway scripts live in `src/main/resources/db/migration/`.

Run migrations manually (requires DB URL in environment or `-D` properties):

```bash
./mvnw flyway:migrate \
  -Dflyway.url="jdbc:postgresql://localhost:5432/nexware" \
  -Dflyway.user=nexware \
  -Dflyway.password=nexware_local
```

## Testing

Unit tests (no Docker):

```bash
./mvnw test -Dtest='*Test'
```

Integration tests (Testcontainers PostgreSQL â€” requires Docker):

```bash
./mvnw verify
```

The `test` profile disables Redis, Kafka, and the reservation scheduler.

## Build & Docker image

```bash
./mvnw -B clean package -DskipTests
docker build -t nexware-backend:local .
```

### EC2 / production (Neon PostgreSQL)

```bash
cp .env.example .env
# Edit .env â€” NEON_DB_*, JWT_SECRET, NEXWARE_OPENAPI_SERVER_URL=https://nexware.me
docker compose up -d --build
docker compose logs -f backend
```

Health check: `curl https://nexware.me/actuator/health`

Swagger UI: `https://nexware.me/swagger-ui.html` â€” use **Actuator â†’ GET /actuator/health** to verify the backend is UP (no login required).

Set `NEXWARE_OPENAPI_SERVER_URL=https://your-domain` so Swagger sends **https** requests behind Nginx.

### Local full stack (Postgres + Redis + Kafka in Docker)

```bash
cp .env.example .env
# Use the "Local Docker stack" values documented at the bottom of .env.example
docker compose -f docker-compose.yml -f docker-compose.local.yml up -d --build
```

## Project structure

```
src/main/java/com/nexware/
  auth/          user/          product/       inventory/
  warehouse/     supplier/      purchaseorder/ reservation/
  notification/  audit/         shared/        config/
```

## CI/CD

| Workflow | File | When it runs |
|----------|------|----------------|
| **CI** | `.github/workflows/ci.yml` | Every push/PR to `main` â€” `./mvnw verify` |
| **Deploy** | `.github/workflows/deploy.yml` | After CI passes on `main` â€” SSH deploy to EC2 |

### One-time setup (before auto-deploy works)

1. **On EC2** â€” clone, create `.env`, start app once manually:
   ```bash
   git clone https://github.com/janithkavishka31/nexware-backend.git
   cd nexware-backend
   cp .env.example .env
   nano .env
   docker compose up -d --build
   ```

2. **GitHub repo â†’ Settings â†’ Secrets and variables â†’ Actions** â€” add:

   | Secret | Example |
   |--------|---------|
   | `EC2_HOST` | `13.60.49.31` (EC2 public IP) |
   | `EC2_USER` | `ubuntu` |
   | `EC2_SSH_KEY` | Private key contents (PEM) for SSH to EC2 |

3. **Push to `main`** â€” CI runs tests, then Deploy pulls latest code on EC2 and rebuilds Docker.

Manual deploy on EC2: `bash scripts/ec2-deploy.sh`

## License

Proprietary â€” NexWare Distribution Platform.

