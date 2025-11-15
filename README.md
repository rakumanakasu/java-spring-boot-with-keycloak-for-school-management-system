A secure REST API built with Spring Boot using Keycloak for authentication and role-based authorization.
The application uses JWT tokens, custom Role Mappers, User Management, and supports integration with frontend frameworks such as Flutter, React, and Next.js.

🚀 Features
🔐 Authentication & Authorization

Keycloak OpenID Connect (OIDC)

Login using Keycloak token endpoint

Role-based access control (ADMIN, TEACHER, STUDENT)

JWT validation via Spring Security

Custom role converter (KeycloakJwtAuthenticationConverter)

CORS security configuration

👥 User Management

Manage Students, Teachers, Employees

Assign users to classes

CRUD operations for all entities

📝 Attendance & Classroom Module

Create & Update Attendance

Attendance filtered by:

Student

Class

Date

Retrieve classes with or without attendance

🖼 File Handling

Upload user photos

Auto-save into /uploads folder

Secure access

🗄 Database

MySQL (production)

H2 (optional for testing)

🛠 Technologies Used
Technology	Description
Java 17+	Language
Spring Boot	Backend framework
Spring Security	Authentication/Authorization
Keycloak	Identity & Access Management
MySQL	Database
JPA/Hibernate	ORM
Lombok	Boilerplate reduction
Maven	Dependency management
📦 Project Structure
src/main/java/com/dara/su79
│
├── configurations
│   ├── SecurityConfig.java
│   ├── KeycloakConfig.java
│   ├── KeycloakJwtAuthenticationConverter.java
│
├── controllers
│
├── services
│
├── repositories
│
├── models
│
├── dto
│
└── SchoolManagementApplication.java

🔑 Keycloak Configuration
1️⃣ Create a Realm

Example:

su79-school-management-realm

2️⃣ Create Client
Client ID: su79-school-management
Access Type: public or confidential
Valid Redirect URIs: http://localhost:3000/*  (or Flutter)
Web Origins: *

3️⃣ Create Roles
ADMIN
TEACHER
STUDENT

4️⃣ Add Realm Roles

Keycloak → Realm Roles → Add Role

5️⃣ Assign Roles to Users

Users → (Your User) → Role Mappings → Assign

🔒 SecurityConfig Overview

The project uses:

JWT authentication

Custom Keycloak role extraction

CORS allowed origins

Public/secured endpoints

Example:

http.csrf(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/auth/**").permitAll()
        .requestMatchers("/uploads/**").permitAll()
        .anyRequest().authenticated()
    )
    .oauth2ResourceServer(oauth -> oauth
        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
    );

▶️ Running the Project
1. Clone the repository
git clone https://github.com/your-username/spring-boot-keycloak.git
cd spring-boot-keycloak

2. Configure application.properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/school_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/su79-school-management-realm

3. Run the API
mvn spring-boot:run

4. Test Login

Use Postman or Flutter:

POST http://localhost:8080/realms/su79-school-management-realm/protocol/openid-connect/token

📡 API Endpoints (Summary)
🔐 Authentication
Method	Endpoint
POST	/api/v1/auth/login
POST	/api/v1/auth/register
👨‍🎓 Students
GET    /api/v1/students
POST   /api/v1/students
PUT    /api/v1/students/{id}
DELETE /api/v1/students/{id}

🏫 Classrooms
GET    /api/v1/classrooms
POST   /api/v1/classrooms
PUT    /api/v1/classrooms/{id}
DELETE /api/v1/classrooms/{id}

📅 Attendance
GET    /api/v1/attendances/class/{classId}
GET    /api/v1/attendances/student/{studentId}
GET    /api/v1/attendances/date/{date}
POST   /api/v1/attendances
PUT    /api/v1/attendances/{id}
DELETE /api/v1/attendances/{id}

🧪 Testing
mvn test

📄 License

This project is licensed under the MIT License.
