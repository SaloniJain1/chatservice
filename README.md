# Chat History Microservice

This project provides a robust, production-ready backend for managing RAG (Retrieval-Augmented Generation) chatbot history. It handles session management, message storage, and secure retrieval of chat contexts.

## 🚀 Quick Start (Docker Compose)

The easiest way to run the service is using Docker Compose.

1.  Navigate to the project subdirectory:
    ```bash
    cd BackendRoleAssignment
    ```
2.  Start the containers:
    ```bash
    docker-compose up --build -d
    ```
3.  Access the services:
    - **Main API**: [http://localhost:8080](http://localhost:8080)
    - **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    - **pgAdmin**: [http://localhost:5050](http://localhost:5050) (Login: `admin@admin.com` / `admin`)

---

## 📖 API Documentation (Swagger)

Once the application is running, you can explore and test the APIs directly through the Swagger UI:
👉 **[Open Swagger UI](http://localhost:8080/swagger-ui/index.html)**

The interactive documentation allows you to see all available endpoints, required parameters, and response schemas.

---

## 🛠 Exposed APIs

The service exposes the following endpoints for chat management:

### Session Management
- `GET /api/sessions`: List all chat sessions for the authenticated user.
- `POST /api/sessions`: Create a new chat session.
- `GET /api/sessions/{id}`: Retrieve details of a specific session.
- `PATCH /api/sessions/{id}`: Update session metadata (title, favorite status).
- `DELETE /api/sessions/{id}`: Permanently delete a session and its history.

### Message History
- `GET /api/sessions/{id}/messages`: Fetch a paginated history of messages in a session.
- `POST /api/sessions/{id}/messages`: Append a new user or AI message to a session.

---

## 🔐 Security & JWT

The service uses JWT (JSON Web Token) for user identification via the `x-user-id` header (internally handled or passed by a gateway).

### Test Token Details
During development and testing, we use a generated RSA-signed JWT token. It is valid for **1 year** (Expiration: `2027-02-07`).

#### 🔑 Test JWT Token
To authenticate your requests, use the following token as-is in the **`Authorization`** header with the **`Bearer`** prefix:

```text
Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJleHAiOjE4MDE5NDQwMDB9.dU9BNPiKVhHEus35uLAZkLV4zt9Am64nQCk8BPkiOAy0W4df5FwwBjpNycGvD9ukGYjOX978iqvB2Szk6UppLc7YQeuF5fGgIFIynCwxlaene8srkt4R5fd54Jbm3va66JW3SPVuO8oIr-Z6X-8L2r37VqFODjHtWrL8pY0JpKUsdDpvuMpiG5tS8_gaxAKf-V9q6ug7vdfOPL-wjR3H9aAwQ38pdJQ37v5Rg0KI3X0FSeqicXiP78s7tDvwL_dZcniNUL36jTGbf1sm2TcRPsc6csLL-0_t8w6-rJaqNBknhvP5Q0pWFTO6yDWZ28EhJrMRWBQyuc4domoNGso46g
```

- **Subject**: `test-user`
- **Keys**: Signature is verified using the public RSA key found in the project root.

> [!IMPORTANT]
> For production, ensure that tokens are short-lived and follow best practices for rotating RSA keys.

---

## 📂 Project Structure
- `BackendRoleAssignment/`: Core Spring Boot application.
- `.gitignore`: Configured at root to manage build artifacts and local settings.
- `private.pem` / `public.pem`: RSA keys used for JWT signing and verification during testing.
