# Enterprise Warehouse Management System (WMS)

A robust, enterprise-grade backend application for managing warehouse operations, inventory tracking, and logistics. Built with a modern Java tech stack and adhering to strict software engineering best practices.

## 🚀 Tech Stack
- **Framework:** Java 17, Spring Boot 3
- **Database:** PostgreSQL
- **ORM:** Hibernate / Spring Data JPA
- **Utilities:** Lombok, Maven

## 📈 Development Roadmap & Progress

### ✅ Week 1: Foundation & Data Modeling (Completed)
- Set up a professional, strict Git workflow using feature branches.
- Configured PostgreSQL database connection and properties.
- Designed core JPA Entities: `Warehouse`, `StorageBin`, `Product`, and `InventoryItem`.
- Established database schema relationships using `@OneToMany` and `@ManyToOne` annotations.
- Implemented Spring Data `JpaRepository` interfaces for data persistence.
- Built the foundational REST API layer (`@RestController`) to handle incoming JSON requests and tested them successfully via Postman.

### ⏳ Week 2: Business Logic & Transactions (Upcoming)
- Service layer implementation.
- Complex putaway algorithms and transactional integrity.
- Custom exception handling and validation.

### ⏳ Week 3: Security & Advanced Features (Upcoming)
- Spring Security integration (JWT Authentication).
- Role-Based Access Control (RBAC) for Admins vs. Workers.

### ⏳ Week 4: Cloud Deployment & CI/CD (Upcoming)
- Dockerization of the application.
- GitHub Actions for automated testing.
- Deployment to AWS/Render.

## 🛠️ Local Setup
1. Ensure PostgreSQL is running locally on port `5432` with a database named `wms_db`.
2. Clone the repository.
3. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The API will be accessible at `http://localhost:8080`.
