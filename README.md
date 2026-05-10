# Library Management System

A simple library management website built with Spring Boot, JPA, H2 database, and Thymeleaf for UI.

## Features

- Issue books to users
- Return books with fine calculation
- Search books by title or author
- Admin panel for managing books, users, and issues

## Technologies

- Java 21
- Spring Boot 3.3.0
- Spring Data MongoDB
- Thymeleaf
- Bootstrap for UI

## Setup

1. Ensure Java 21 is installed.
2. Run `mvn clean install` to build the project.
3. Run `mvn spring-boot:run` to start the application.
4. Access the application at http://localhost:9091

## Login

- Student: username `student`, password `student`
- Admin: username `admin`, password `admin`

## Database

The application uses MongoDB Atlas. The connection string is configured in `src/main/resources/application.properties`.

Example connection string:

```
mongodb+srv://ash:9480@cluster0.5hu7rml.mongodb.net/?appName=Cluster0
```

Database name: `librarydb`

## API Endpoints

The application provides RESTful APIs, but primarily uses web pages.

For learning purposes, you can extend with REST controllers.
