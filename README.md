# 🚀 Spring Boot REST API — PostgreSQL + Docker + CI/CD

A production-grade Spring Boot REST API backed by **PostgreSQL**, managed with **Flyway migrations**, containerized with **Docker**, and deployed via a **GitHub Actions CI/CD pipeline**.

---

## 🛠️ Tech Stack

| Layer          | Technology                              |
|---------------|------------------------------------------|
| Language       | Java 21                                 |
| Framework      | Spring Boot 3.3                         |
| Database       | PostgreSQL 16                           |
| Migrations     | Flyway                                  |
| Build Tool     | Maven                                   |
| Testing        | JUnit 5, MockMvc, Mockito (H2 for tests)|
| Container      | Docker, Docker Compose                  |
| CI/CD          | GitHub Actions / Jenkins                |

---

## 📂 Project Structure

```
springboot-cicd-project/
├── src/main/
│   ├── java/com/devops/api/
│   │   ├── controller/         ProductController, HealthController
│   │   ├── service/            ProductService, ProductServiceImpl
│   │   ├── repository/         ProductRepository
│   │   ├── model/              Product.java
│   │   ├── dto/                ProductDTO, ApiResponse
│   │   └── exception/          GlobalExceptionHandler, custom exceptions
│   └── resources/
│       ├── application.properties
│       └── db/migration/
│           ├── V1__create_products_table.sql
│           ├── V2__seed_sample_data.sql
│           └── V3__add_stock_summary_view.sql
├── src/test/
│   ├── java/                   ProductServiceTest, ProductControllerTest, ProductIntegrationTest
│   └── resources/
│       └── application-test.properties   (H2 for fast tests)
├── .github/workflows/
│   ├── ci-cd.yml               Full pipeline with PostgreSQL service
│   └── pr-validation.yml
├── Dockerfile                  Multi-stage build (JDK21 builder → JRE21 runtime)
├── docker-compose.yml          PostgreSQL + Spring Boot together
├── Jenkinsfile
├── .env.example
└── pom.xml
```

---

## 🌐 API Endpoints

| Method | Endpoint                                          | Description              |
|--------|---------------------------------------------------|--------------------------|
| GET    | `/api/v1/products`                                | Get all products         |
| GET    | `/api/v1/products/{id}`                           | Get product by ID        |
| POST   | `/api/v1/products`                                | Create product           |
| PUT    | `/api/v1/products/{id}`                           | Update product           |
| DELETE | `/api/v1/products/{id}`                           | Delete product           |
| GET    | `/api/v1/products/category/{category}`            | Filter by category       |
| GET    | `/api/v1/products/search?name=...`                | Search by name           |
| GET    | `/api/v1/products/price-range?minPrice=&maxPrice=`| Filter by price range    |
| GET    | `/api/v1/products/in-stock`                       | In-stock products        |
| GET    | `/api/v1/products/out-of-stock`                   | Out-of-stock products    |
| GET    | `/api/v1/health`                                  | App health check         |
| GET    | `/api/v1/info`                                    | App info                 |

---

## 🚀 Option A — Run with Docker Compose (Easiest, Recommended)

This starts **both PostgreSQL and the Spring Boot app** with one command.

### Prerequisites
- Docker Desktop installed and running

```bash
# 1. Clone / extract project
cd springboot-cicd-project

# 2. Build and start everything
docker-compose up --build -d

# 3. Watch logs (optional)
docker-compose logs -f springboot-api

# 4. Test it
curl http://localhost:8080/api/v1/health
curl http://localhost:8080/api/v1/products
```

### Stop everything
```bash
docker-compose down          # stops containers, keeps DB data
docker-compose down -v       # stops and WIPES database volume
```

---

## 🚀 Option B — Run Locally with Maven (Requires PostgreSQL installed)

### Step 1 — Install & Start PostgreSQL

**Windows:** Download from https://www.postgresql.org/download/windows/
During install set password to `devpass`

**Mac (Homebrew):**
```bash
brew install postgresql@16
brew services start postgresql@16
```

**Ubuntu/Debian:**
```bash
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### Step 2 — Create the Database

```bash
# Open PostgreSQL shell
psql -U postgres

# Run these commands:
CREATE DATABASE productdb;
CREATE USER devuser WITH PASSWORD 'devpass';
GRANT ALL PRIVILEGES ON DATABASE productdb TO devuser;
\q
```

### Step 3 — Configure Environment Variables

**Windows (Command Prompt):**
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/productdb
set DB_USERNAME=devuser
set DB_PASSWORD=devpass
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/productdb"
$env:DB_USERNAME="devuser"
$env:DB_PASSWORD="devpass"
```

**Mac/Linux:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/productdb
export DB_USERNAME=devuser
export DB_PASSWORD=devpass
```

### Step 4 — Build and Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

Flyway will automatically create tables and insert 15 sample products on first run.

App running at → **http://localhost:8080**

---

## 🧪 Running Tests

Tests use H2 in-memory database — **no PostgreSQL needed** to run tests.

```bash
# Run all tests (29 tests across 3 files)
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=ProductControllerTest
mvn test -Dtest=ProductIntegrationTest

# Generate test report
mvn surefire-report:report
```

Expected output:
```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 📊 Testing the API (cURL Examples)

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Get all products (15 pre-loaded via Flyway)
curl http://localhost:8080/api/v1/products

# Create a product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"AirPods Pro","description":"Active Noise Cancelling","price":249.99,"quantity":20,"category":"Electronics"}'

# Get by ID
curl http://localhost:8080/api/v1/products/1

# Search by name
curl "http://localhost:8080/api/v1/products/search?name=Mac"

# Filter by category
curl http://localhost:8080/api/v1/products/category/Electronics

# Filter by price range
curl "http://localhost:8080/api/v1/products/price-range?minPrice=40&maxPrice=200"

# In-stock products
curl http://localhost:8080/api/v1/products/in-stock

# Update product
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"MacBook Pro 14","description":"Updated M3 Pro","price":2199.99,"quantity":5,"category":"Electronics"}'

# Delete product
curl -X DELETE http://localhost:8080/api/v1/products/1
```

---

## 🗃️ Flyway Migrations

Flyway manages the database schema. Migrations run automatically on startup:

| Version | File                               | Description                    |
|---------|------------------------------------|--------------------------------|
| V1      | `V1__create_products_table.sql`    | Creates products table + indexes |
| V2      | `V2__seed_sample_data.sql`         | Inserts 15 sample products     |
| V3      | `V3__add_stock_summary_view.sql`   | Adds stock summary view        |

To add a new migration: create `V4__your_change.sql` in `src/main/resources/db/migration/`

---

## 🐳 Docker Commands Reference

```bash
# Build image only
docker build -t springboot-cicd-api .

# Run only the API (if postgres is already running)
docker run -d -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/productdb \
  -e DB_USERNAME=devuser \
  -e DB_PASSWORD=devpass \
  --name springboot-api springboot-cicd-api

# View container logs
docker logs springboot-api -f

# Connect to running PostgreSQL container
docker exec -it productdb-postgres psql -U devuser -d productdb

# Run SQL directly
docker exec -it productdb-postgres psql -U devuser -d productdb -c "SELECT * FROM products;"
```

---

## ⚙️ Environment Variables

| Variable        | Default                                      | Description          |
|-----------------|----------------------------------------------|----------------------|
| `DB_URL`        | `jdbc:postgresql://localhost:5432/productdb` | PostgreSQL JDBC URL  |
| `DB_USERNAME`   | `devuser`                                    | DB username          |
| `DB_PASSWORD`   | `devpass`                                    | DB password          |
| `SERVER_PORT`   | `8080`                                       | App port             |
| `JAVA_OPTS`     | `-Xms256m -Xmx512m`                          | JVM memory settings  |

---

## 🔐 GitHub Secrets for CI/CD

| Secret           | Purpose                            |
|------------------|------------------------------------|
| `DOCKER_USERNAME`| Docker Hub username                |
| `DOCKER_PASSWORD`| Docker Hub access token            |
| `DEPLOY_HOST`    | Server IP for SSH deployment       |
| `DEPLOY_USER`    | SSH user on server                 |
| `DEPLOY_SSH_KEY` | Private SSH key                    |
| `DB_PASSWORD`    | Production DB password             |
