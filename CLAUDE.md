# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Java Backend (Maven)
- **Build all modules**: `./mvnw clean install`
- **Build specific module**: `./mvnw clean install -pl <module-name>`
- **Run a service**: `./mvnw spring-boot:run -pl <module-name>`
- **Run tests**: `./mvnw test`
- **Run specific test**: `./mvnw test -Dtest=ClassName`
- **Run specific test method**: `./mvnw test -Dtest=ClassName#methodName`
- **Lint (Checkstyle)**: `./mvnw checkstyle:check`
- **Update dependencies**: `./mvnw versions:display-dependency-updates`

### Frontend (React/Vite)
- **Install dependencies**: `cd web && npm install`
- **Run dev server**: `cd web && npm run dev`
- **Build**: `cd web && npm run build`
- **Lint**: `cd web && npm run lint`
- **Test**: `cd web && npm run test`

### Docker
- **Start all services**: `docker-compose up -d`
- **Stop services**: `docker-compose down`
- **Build specific image**: `docker build -t port-buddy-<service> -f Dockerfile-<service> .`

## Code Architecture

PortBuddy is a distributed tunneling service (similar to ngrok) built with Java 25, Spring Boot 3.5, and React.

### Module Overview
- **`cli/`**: GraalVM-native CLI (Picocli) that connects to the server via WebSockets to tunnel local traffic.
- **`server/`**: Central management service (Auth, Tunnel Registry, Payments, Domains). Uses Flyway for DB migrations.
- **`gateway/`**: Spring Cloud Gateway (WebFlux) for routing, SSL termination, and security.
- **`net-proxy/`**: Specialized proxy for high-performance TCP/UDP traffic.
- **`common/`**: Shared DTOs, tunnel message protocols (`WsTunnelMessage`), and utilities.
- **`web/`**: React dashboard for managing tunnels and accounts.
- **`eureka/`**: Service discovery for the microservices.
- **`ssl-service/`**: Automates SSL certificate issuance and management.

### Key Technical Patterns
- **Tunneling**: Uses WebSockets (`BinaryWsFrame`) for bi-directional communication between CLI and Server.
- **Database**: PostgreSQL with Flyway migrations located in `server/src/main/resources/db/migration`.
- **Security**: JWT-based authentication across microservices.
- **Configuration**: Service-specific settings are in `src/main/resources/application.yml` within each module.
