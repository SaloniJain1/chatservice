# Backend Role Assignment - Chat History Microservice

## Overview
This is a production-ready Java Spring Boot microservice for managing RAG chatbot history. It supports session management, message storage, and secure retrieval.

## Features
- **Session Management**: Create, Rename, Delete, Favorite sessions.
- **Message History**: Store and retrieve chat messages with context.
- **Security**: API Key authentication.
- **Rate Limiting**: basic rate limiting to prevent abuse.
- **Documentation**: Swagger UI integration.
- **Containerization**: Docker & Docker Compose setup.

## Setup & Running

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development without Docker)

### Running with Docker
1.  **Clone the repository**.
2.  **Run Docker Compose**:
    ```bash
    docker-compose up --build
    ```
3.  **Access the application**:
    - API: `http://localhost:8080/api/sessions`
    - Swagger UI: `http://localhost:8080/swagger-ui.html`
    - pgAdmin: `http://localhost:5050` (Login: `admin@admin.com` / `admin`)

### API Usage
**Authentication**: All requests must include the header `X-API-KEY: my-secret-api-key` (default value).

#### Endpoints
- `POST /api/sessions` - Create a new session. `{"title": "My Chat"}`
- `GET /api/sessions/{id}` - Get session details.
- `PATCH /api/sessions/{id}` - Update session (rename/favorite). `{"title": "New Name", "isFavorite": true}`
- `DELETE /api/sessions/{id}` - Delete session.
- `POST /api/sessions/{id}/messages` - Add a message. `{"role": "USER", "content": "Hello", "context": "rag context"}`
- `GET /api/sessions/{id}/messages` - Get messages for a session.

## Configuration
Environment variables can be set in `docker-compose.yml` or a `.env` file.
See `.env.example` for reference.
