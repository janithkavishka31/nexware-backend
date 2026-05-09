# NexWare Distribution Platform â€” Backend

Enterprise electronics distribution and warehouse management API built as a **Spring Boot 3.4** modular monolith (Spring Modulith) on **Java 21**, with **PostgreSQL**, **Flyway**, optional **Redis** and **Kafka**.

## Architecture & Modules
- **Core Platform**: Spring Boot 3.4.5, Java 21, Spring Modulith 1.3.4
- **Database**: PostgreSQL with Flyway schema versioning
- **Authentication**: Stateless JWT with SHA-256 refresh token rotation
- **Domain Modules**: Products, Warehouses, Inventory, Stock Reservations, Purchase Orders, Suppliers, and Audit Logs
