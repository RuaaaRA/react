<p align="center">
  <img src="https://cdn-icons-png.flaticon.com/512/906/906175.png" alt="LMS Logo" width="120"/>
</p>

<h1 align="center">📚 Learning Management System (LMS)</h1>
<p align="center">
  A full-featured Spring Boot backend API for managing learning environments: courses, quizzes, lessons, enrollments, and more.
</p>

---

## 🚀 Project Overview

This is a Java Spring Boot-based Learning Management System (LMS) designed for educational platforms. It supports user management, course creation, assessments (quizzes & assignments), student enrollments, lesson content, attendance tracking via OTP, and detailed performance tracking. 

> Built with **clean architecture**, **JWT + OAuth2 security**, and **PostgreSQL**, and documented using **Swagger**.

---

## 📦 Technologies Used

- Java 21+
- Spring Boot
- Spring Security (JWT + OAuth2)
- Spring Data JPA
- PostgreSQL
- Swagger (springdoc-openapi)
- Maven
- VS Code
- Postman

---

## ⚙️ Prerequisites

Before you run this project, make sure you have the following installed:

- **Java 21** or higher
- **PostgreSQL** (running locally)
- **Maven**
- (Optional) **Postman** for testing the API
- (Optional) **Swagger UI** for exploring endpoints

---

## 📁 Project Structure

This repo is located at:  
🔗 [https://github.com/S25-SWER313/project-step-1-syr](https://github.com/S25-SWER313/project-step-1-syr)

To run the project:

```bash
cd project-step-1-syr/step1
mvn spring-boot:run
```

Make sure Java version is 21 or higher before running.

---

## 🛠️ application.yml Setup

Create a `src/main/resources/application.yml` file with the following contents:

```yaml
spring:
  application:
    name: step1

  datasource:
    url: jdbc:postgresql://localhost:5432/DataBase
    username: postgres       # <-- Change this to your PostgreSQL username
    password: 1234           # <-- Change this to your PostgreSQL password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop  # Warning: Resets DB every run
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQLDialect

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
            scope:
              - email
              - profile
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
            user-name-attribute: sub

application:
  security:
    jwt:
      secret-key: YOUR_SECRET_KEY
      access-token-expiration: 86400000
      refresh-token-expiration: 604800000

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

openapi: 3.0.0  
info:
  title: "LMS API"
  description: "API documentation for the Learning Management System (LMS)."
  version: "1.0.0"
```

🔴 **Note:** You must change `username` and `password` to match your local setup.

---

## 📄 API Documentation

Once the app is running, you can visit:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- API Docs JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## ✅ Features Summary

### 👥 User Management
- Register, Login with JWT or Google OAuth2
- Roles: Admin, Instructor, Student
- Profile update and secure role-based access

### 📚 Courses
- Course creation and search
- Lesson management (with videos, PDFs, audio)
- Enrollment system 


### 📝 Quizzes & Assignments
- Create assessments and Quizes
- File-based assignment submissions
- Auto grading for Quizes

### 📈 Student Performance
- View quiz scores and grades
- Attendance and lesson completion tracking

### 🔔 Notifications
- System-generated alerts when a course is modified.
- Track student progress and course completion.
---

## 📌 Project Status

> 🔨 **Currently in Stage 1 Development**  
- Backend APIs are being built and tested  
- Frontend and Microservices are **not implemented yet**

---

## 🖼️ Screenshots

### 🔐 Google OAuth2 Login
![OAuth2 Login](https://github.com/S25-SWER313/project-step-1-syr/blob/main/login.png?raw=true)
[Notification](https://github.com/S25-SWER313/project-step-1-syr/blob/main/notification%20(2).jpg)
[Notification](https://github.com/S25-SWER313/project-step-1-syr/blob/main/notification%20(3).jpg)
[Notification](https://github.com/S25-SWER313/project-step-1-syr/blob/main/notification.jpg)
---


---

## 📧 Contact

Feel free to fork the repo, raise issues, or contribute. For help, reach out via GitHub or email.

---

## 🔮 What's Next?

- [ ] Add Frontend (React/Angular/Vue)
- [ ] Convert to Microservices Architecture
- [ ] Add Unit & Integration Tests
- [ ] Add Docker support

--- Swagger UI : http://localhost:8080/swagger-ui/index.html#/

> Made with ❤️ using Spring Boot & PostgreSQL
