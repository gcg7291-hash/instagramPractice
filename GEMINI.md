# GEMINI.md

## Project Overview

This project is a web application built with Spring Boot, aiming to replicate some of the core functionalities of Instagram.

**Key Technologies:**

*   **Backend:** Spring Boot, Spring Security, Spring Data JPA
*   **Database:** MySQL
*   **Frontend:** Thymeleaf
*   **Language:** Java 21
*   **Build Tool:** Gradle

**Architecture:**

The application follows a standard Model-View-Controller (MVC) architecture.
*   `com.example.instagram.controller`: Contains controllers for handling web requests.
*   `com.example.instagram.service`: Contains business logic.
*   `com.example.instagram.repository`: Contains data access objects (repositories) for interacting with the database.
*   `com.example.instagram.entity`: Contains JPA entities representing the database schema.
*   `com.example.instagram.dto`: Contains Data Transfer Objects for transferring data between layers.
*   `resources/templates`: Contains Thymeleaf templates for the UI.

## Building and Running

### Prerequisites

*   Java 21
*   MySQL Server

### Running the Application

1.  **Configure the database:**
    *   Open `src/main/resources/application.properties` and configure the database connection details:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/your-database
        spring.datasource.username=your-username
        spring.datasource.password=your-password
        ```

2.  **Run the application:**
    *   You can run the application using the Gradle wrapper:
        ```bash
        ./gradlew bootRun
        ```
    *   Alternatively, you can run the `InstagramApplication` class from your IDE.

## Development Conventions

*   **Code Style:** The project uses the standard Java code style.
*   **Dependency Management:** Dependencies are managed using Gradle.
*   **Testing:** The project is set up to use JUnit 5 for testing.
