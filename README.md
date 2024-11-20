# Spring Boot 3 (Kotlin) - Blog API

This project is a sample Blog API developed with Spring Boot 3 and Kotlin. It allows users to create, update, delete,
and list blog posts. The API is designed following RESTful principles and includes user management, authentication, and
secure token storage.

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

### Features

- **CRUD Operations**: Create, Read, Update, Delete operations for blog posts and users.
- **Authentication & Authorization**: Ensures users can log in and perform actions based on their permissions.
- **JWT Token Storage**: Secure storage of JWT tokens in Redis for efficient session management.
- **PostgreSQL Database**: Uses PostgreSQL as the primary database for persistent data storage.
- **DTO & Mapping**: Uses Data Transfer Objects (DTOs) for efficient data transfer and mapping between entities.
- **Exception Handling**: Provides user-friendly error messages and comprehensive error handling.
- **Validation**: Ensures secure API usage with data validation.

### Requirements

- **Java 21 or higher**
- **Kotlin**: Project is written in Kotlin alongside Spring Boot.
- **Maven**: Used to manage project dependencies.
- **PostgreSQL**: Required as the primary database for storing application data.
- **Redis**: Used for storing JWT tokens securely and efficiently.
- **Docker & Docker Compose**: To run the application and its dependencies as containers.

### Roadmap

- [X] **Authentication**
    - Implement JWT-based authentication.
    - Add role-based authorization for users (e.g., admin, user).

- [X] **Registration & Login**
    - Create endpoints for user registration and login.
    - Implement password hashing and salting for secure storage.

- [X] **Password Reset**
    - Create endpoints for password reset.
    - Add email notifications for password reset requests.

- [ ] **User Management**
    - Create endpoints for user registration and profile management.
    - Add user roles and permissions.

- [ ] **Category Management**
    - Implement CRUD operations for categories.
    - Allow categorization of posts.

- [ ] **Tag Management**
    - Implement CRUD operations for tags.
    - Enable tagging posts with multiple tags.

- [ ] **Post Management**
    - Create, update, delete, and view posts.
    - Add support for rich text formatting in posts.
    - Integrate posts with categories and tags.

- [ ] **Comment Management**
    - Implement CRUD operations for comments on posts.
    - Add moderation features for comments (e.g., approve, delete).

- [ ] **Application Settings Management**
    - Create endpoints to manage application settings (e.g., site title, SEO settings).
    - Add support for configurable project settings through the API.

### Swagger Documentation

Once the application is running, you can access the Swagger UI for API documentation at:
[http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

### Postman Collection

A Postman collection is available in the repository to simplify testing the API. Import the collection from:

```
src/main/resources/Blog API.postman_collection.json
```

## Run with Docker Compose

To start the application along with PostgreSQL and Redis, you can use Docker Compose:

```bash
docker-compose up --build -d
```

## Run with standalone PostgreSQL and Redis

### Install dependencies

```bash
mvn clean install
```

### Run project

```bash
mvn spring-boot:run 
```

### Build project

```bash
mvn clean package
```

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
