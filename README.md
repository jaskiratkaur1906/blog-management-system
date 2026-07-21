# Blog Management System

A RESTful blog management API built with Spring Boot, featuring JWT-based authentication, role-based authorization, and full CRUD operations for posts and comments.

![CI](https://github.com/jaskiratkaur1906/blog-management-system/actions/workflows/ci.yml/badge.svg)

## Overview

This project is a backend API for a simple blogging platform where users can register, log in, create and manage blog posts, and comment on posts. It demonstrates a complete, production-style layered architecture — entities, repositories, DTOs, services, security, and controllers — along with automated testing, interactive API documentation, and containerized deployment.

## Features

- **User authentication** — registration and login secured with JWT (JSON Web Tokens)
- **Password security** — passwords hashed with BCrypt, never stored in plain text
- **Role-based access control** — regular users can manage their own content; admins can moderate any post or comment
- **Full CRUD** — create, read, update, and delete blog posts and comments
- **Data validation** — request validation with clear, structured error responses
- **Centralized error handling** — consistent JSON error format across the API, with no leaked stack traces
- **Interactive API docs** — Swagger / OpenAPI UI for exploring and testing endpoints directly in the browser
- **Automated tests** — unit tests (Mockito) and integration tests (Spring Boot Test + MockMvc)
- **Containerized** — Dockerfile for building and running the app anywhere
- **CI pipeline** — GitHub Actions automatically builds and tests the project on every push and pull request

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security, JWT (jjwt) |
| Persistence | Spring Data JPA, Hibernate |
| Database | H2 (in-memory) |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |
| Containerization | Docker |
| CI/CD | GitHub Actions |

## Architecture

The project follows a standard layered architecture:

```
Controller  →  Service  →  Repository  →  Database
   (HTTP)     (business logic)   (data access)
```

- **`entity/`** — JPA entities mapping to database tables (`User`, `Role`, `Post`, `Comment`)
- **`repository/`** — Spring Data JPA repositories for database access
- **`dto/`** — request/response objects, decoupled from the database schema
- **`service/`** — business logic, validation rules, and entity ↔ DTO conversion
- **`security/`** — JWT generation/validation, Spring Security integration
- **`controller/`** — REST endpoints
- **`exception/`** — centralized exception handling
- **`config/`** — security configuration, OpenAPI setup, database seeding

## Data Model

- A **User** has a **Role** (`ROLE_USER` or `ROLE_ADMIN`)
- A **User** can author many **Posts** and many **Comments**
- A **Post** can have many **Comments**

## Getting Started

### Prerequisites

- Java 17+
- Maven (or use the included Maven Wrapper: `./mvnw`)
- Docker (optional, for containerized runs)

### Running locally

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. On startup, two roles (`ROLE_USER`, `ROLE_ADMIN`) are automatically seeded into the database.

### Running with Docker

```bash
docker build -t blog-management-system .
docker run -p 8080:8080 blog-management-system
```

### Running tests

```bash
./mvnw clean verify
```

## API Documentation

Once the app is running, interactive API docs are available at:

```
http://localhost:8080/swagger-ui.html
```

Use the **Authorize** button to attach a JWT and test protected endpoints directly from the browser.

## API Endpoints

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Log in and receive a JWT | No |
| GET | `/api/posts` | List all posts | Yes |
| GET | `/api/posts/{id}` | Get a single post | Yes |
| POST | `/api/posts` | Create a new post | Yes |
| PUT | `/api/posts/{id}` | Update your own post | Yes |
| DELETE | `/api/posts/{id}` | Delete a post (author or admin) | Yes |
| POST | `/api/posts/{postId}/comments` | Add a comment to a post | Yes |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | Delete a comment (author or admin) | Yes |

### Example: Register and authenticate

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "jaskirat", "email": "j@test.com", "password": "test123"}'

# Log in
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "jaskirat", "password": "test123"}'

# Use the returned token for protected endpoints
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "My first post", "content": "Hello world"}'
```

## Security Notes

- Passwords are hashed with BCrypt before storage.
- Authentication is stateless — a JWT is issued on login and must be included as a `Bearer` token on subsequent requests.
- Tokens expire after 1 hour.
- Authorization is enforced at the service layer: users can only modify their own posts/comments, while admins can moderate any content.

## Roadmap / Possible Extensions

- Refresh tokens for longer sessions without re-login
- Email notifications for new comments
- CI/CD deployment to a live host (e.g. Render, Heroku, AWS)
- Pagination for post listings

## License

This project was built for educational purposes.
