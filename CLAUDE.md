# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WebToDoStarter is a Spring Boot web application for task management with user authentication. It follows a layered architecture with Controllers, Services, DAOs, and Entities.

## Architecture

- **Entity Layer**: Plain POJOs for data models (Task, TaskType, User)
- **DAO Layer**: Data access using Spring JDBC with manual SQL
- **Service Layer**: Business logic interfaces and implementations
- **Controller Layer**: Web controllers for REST endpoints and Thymeleaf views
- **Configuration**: Security config with Spring Security, MVC configuration
- **Database**: H2 in-memory database with schema.sql and data.sql initialization

## Development Commands

### Build and Run
```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the Spring Boot application
./gradlew test           # Run all tests
```

### Testing
```bash
./gradlew test --tests TaskServiceImplTest                    # Run specific test class
./gradlew test --tests TaskServiceImplUnitTest               # Run unit tests
./gradlew test --tests "*.TaskService*"                      # Run tests matching pattern
```

### Database Access
- H2 Console available at http://localhost:8081/h2-console when running
- JDBC URL: `jdbc:h2:mem:test`
- Username: `sa`, Password: (empty)

## Key Implementation Details

### Data Access Pattern
- Uses Spring JDBC with DAO pattern, not JPA/Hibernate
- Manual SQL queries in TaskDaoImpl and UserDao
- Database schema initialized via schema.sql and data.sql

### Security Configuration
- Spring Security configured in SecurityConfig
- Custom authentication and authorization logic

### Web Layer
- Thymeleaf templates in src/main/resources/templates/
- Static resources in src/main/resources/static/
- Controllers use both REST endpoints and view rendering

### Project Structure
- Main package: `com.example.demo`
- Modular organization: app/, config/, dao/, entity/, service/
- Test configuration in src/test/resources/application-unit.yml