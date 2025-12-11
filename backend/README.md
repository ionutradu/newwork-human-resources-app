# Human Resources Management Application

## Project Documentation for Technical Review

This document provides a technical overview of the Human Resources Management Application, which is a multi-module project comprising a Java Spring Boot backend and an Angular frontend.

-----

## Backend Overview (Spring Boot/Java)

The backend is a Spring Boot application designed using modern engineering principles. It utilizes a MongoDB database and integrates with an advanced AI model for processing employee feedback.

### Key Architectural Notes

* **API-First Design**: The service adheres to **API-First Engineering principles**, with API specifications designed using **OpenAPI 3.x**.
* **AI Integration for Feedback**: The application integrates an LLM (Large Language Model) to process and polish raw employee feedback. The configured model is **Gemini 3 Plus** (powered by `google/gemma-3-27b-it`), enhancing the quality and professionalism of HR communications.
* **Asynchronous Processing**: The service architecture supports **Kafka** for event consumption and processing, enabling asynchronous pipelines for actions like submitting absence requests and receiving polished feedback.
* **Local Development Simplicity**: **Testcontainers** is used in the default profile to provision dependencies like MongoDB seamlessly, ensuring a rapid and consistent testing environment.
* **Retry Mechanism**: The service implements a simple retry mechanism for the external `polishFeedback` service call. This is currently set up for rapid local testing. In a production environment, this would be replaced by a robust, asynchronous solution using an **Exponential Backoff Policy** or a Circuit Breaker pattern (e.g., Resilience4j) to handle transient API failures gracefully and prevent resource exhaustion.
* **Database Migration**: **Mongock** is employed for managing non-SQL database changes and script-based initial data migration.
* **Hardcoded secrets**: Secrets are hardcoded in `application.yaml` for easing local development and testing. They must be moved to an external secured storage provider.

### Technology Stack

| Category | Key Technologies Used | Note |
| :--- | :--- | :--- |
| **Core Framework** | **Spring Boot 3.x** | Core framework used across the service. |
| **Web/API** | **Spring Web / REST**, **Jackson**, **JWT** | RESTful controllers, global exception handling, JSON (de)serialization, and token-based authentication. |
| **Database** | **MongoDB**, **Spring Data MongoDB** | Used for document modeling and managing CRUD operations/aggregation pipelines. |
| **Data & Mapping** | **Lombok**, **MapStruct** | Boilerplate reduction and efficient DTO-entity mapping. |
| **Testing** | **Testcontainers**, **JUnit 5** | Comprehensive integration testing for MongoDB and unit tests. |
| **Security** | **JWT**, **Keycloak** (Architectural) | Token-based authentication. **Keycloak** is an architectural consideration for enterprise-grade AuthN/AuthZ provider integration. |
| **Advanced Data Handling** | **Redis / Redisson** (Architectural) | Used for demonstrating expertise in implementing caching layers and distributed locks. |
| **Relational Expertise** | **Spring Data JPA, Flyway, Hasura** (Architectural) | Demonstrates broader experience with SQL database migrations (**Flyway**), ORM (**Spring Data JPA**), and modern data/GraphQL platforms (**Hasura**). |

### Default Application Users

The initial database migration (`_001_InitialUsersMigration.java`) creates three default users for immediate testing of role-based access control.

| Role | Email | First Name | Last Name | Default Password |
| :--- | :--- | :--- | :--- | :--- |
| **Manager** | `manager@app.com` | John | Doe | `SecurePassword` |
| **Coworker** | `coworker@app.com` | Jane | Smith | `SecurePassword` |
| **Employee** | `employee@app.com` | Alice | Johnson | `SecurePassword` |

-----

### Future Improvements
* **Secrets Management**: Extract secrets (HuggingFace API key, JWT secret) from application.yaml into secured storage. Recommended production solutions include Environment Variables, HashiCorp Vault, AWS Secrets Manager, or Spring Cloud Config.

* **Local Environment Isolation**: Restrict Testcontainers solely to integration tests. For local application runtime, introduce a standard Dockerfile and docker-compose.yaml setup to manage external dependencies (MongoDB, Kafka) independently.

* **Resilient Retry Policy**: Configure the Spring Retry policy for the AI polishFeedback service with an Exponential Backoff Policy to ensure resilience and prevent resource overwhelming in the production environment.

* **Monitoring and Observability**: Integrate robust metrics (e.g., Prometheus/Micrometer), distributed tracing (e.g., OpenTelemetry), and structured logging (e.g., ELK Stack) for comprehensive operational visibility in production.

* **Asynchronous AI Processing**: Implement the AI feedback polishing as an asynchronous process triggered by a Kafka event, preventing the initial HTTP request from being blocked.