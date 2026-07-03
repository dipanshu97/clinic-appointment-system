# 🏥 Clinic Appointment Management System

> A secure, scalable backend system for managing clinic appointments built with Java, Spring Boot, REST APIs, Redis caching, and Spring Security.

----

## 📌 Overview

A production-grade backend system that handles the complete lifecycle of clinic appointments — from patient and doctor registration to appointment booking, confirmation, rescheduling, and cancellation.

**Key Highlights:**
- Spring Security for role-based access control (Admin, Doctor, Patient)
- Redis caching for fast doctor availability lookups
- Chain of Responsibility pattern for appointment validation
- Builder pattern for clean object construction
- Custom error codes and global exception handling
- JUnit + Mockito unit tests

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                   REST Client                    │
│              (Postman / Frontend)                │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│              Spring Security Layer               │
│         (Role-based: Admin/Doctor/Patient)       │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│                  Controllers                     │
│    PatientController | DoctorController          │
│         AppointmentController                    │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│                   Services                       │
│  PatientService | DoctorService                  │
│         AppointmentService                       │
│  (Chain of Responsibility Validation Pipeline)   │
└──────┬──────────────────────────────┬────────────┘
       │                              │
┌──────▼──────┐                ┌──────▼──────┐
│  MySQL DB   │                │ Redis Cache │
│  (JPA/ORM) │                │  (5 min TTL)│
└─────────────┘                └─────────────┘
```

---

## ✨ Key Features

### 🔒 Security
- **Spring Security** with role-based access control
- Three roles: ADMIN, DOCTOR, PATIENT
- BCrypt password encoding
- Stateless session management

### ✅ Appointment Validation (Chain of Responsibility)
- Future time validation
- Doctor availability check
- Conflict detection (30-min window)
- Token number generation

### ⚡ Redis Caching
- Doctor profiles cached for 5 minutes
- Patient profiles cached for 5 minutes
- Available doctors list cached
- Cache invalidation on updates

### 🛡️ Error Handling
- Custom error codes (PATIENT_001, DOCTOR_001, etc.)
- Global exception handler
- Validation error responses

---

## 🧱 Design Patterns

| Pattern | Where Used |
|---|---|
| **Chain of Responsibility** | Appointment validation pipeline |
| **Builder Pattern** | Appointment object construction |
| **Factory Pattern** | Token number generation |

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security |
| Database | MySQL 8.x (JPA/Hibernate) |
| Caching | Redis |
| Testing | JUnit 5, Mockito |
| Build | Maven |

---

## 📁 Project Structure

```
clinic-appointment-system/
├── src/main/java/com/clinic/
│   ├── controller/
│   │   ├── PatientController.java
│   │   ├── DoctorController.java
│   │   └── AppointmentController.java
│   ├── service/
│   │   ├── PatientService.java
│   │   ├── DoctorService.java
│   │   └── AppointmentService.java
│   ├── repository/
│   ├── model/
│   ├── exception/
│   │   ├── ClinicException.java
│   │   └── GlobalExceptionHandler.java
│   ├── security/
│   │   └── SecurityConfig.java
│   ├── cache/
│   │   └── RedisService.java
│   └── enums/
│       ├── AppointmentStatus.java
│       └── Specialization.java
└── src/test/
    └── AppointmentServiceTest.java
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17
- Maven
- MySQL 8.x
- Redis

### Setup

```bash
# 1. Clone the repo
git clone https://github.com/dipanshu97/clinic-appointment-system.git
cd clinic-appointment-system

# 2. Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinic_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.data.redis.host=localhost
spring.data.redis.port=6379

# 3. Build and run
mvn clean install
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Patients
| Method | Endpoint | Access |
|---|---|---|
| POST | /api/public/patients/register | Public |
| GET | /api/patients/{id} | Authenticated |
| GET | /api/admin/patients | Admin |
| PUT | /api/patients/{id} | Authenticated |
| DELETE | /api/admin/patients/{id} | Admin |

### Doctors
| Method | Endpoint | Access |
|---|---|---|
| POST | /api/admin/doctors/register | Admin |
| GET | /api/public/doctors | Public |
| GET | /api/public/doctors/available | Public |
| GET | /api/public/doctors/specialization/{spec} | Public |
| PATCH | /api/doctor/doctors/{id}/availability | Doctor/Admin |

### Appointments
| Method | Endpoint | Access |
|---|---|---|
| POST | /api/appointments/book | Authenticated |
| PATCH | /api/appointments/{id}/confirm | Authenticated |
| PATCH | /api/appointments/{id}/cancel | Authenticated |
| PATCH | /api/appointments/{id}/reschedule | Authenticated |
| PATCH | /api/appointments/{id}/complete | Doctor/Admin |
| GET | /api/appointments/patient/{id} | Authenticated |
| GET | /api/appointments/doctor/{id} | Authenticated |

---

## 🧪 Testing

```bash
mvn test
```

Test coverage includes:
- Appointment booking success flow
- Past time validation
- Duplicate appointment detection
- Cancel already cancelled appointment

---

## 📬 Contact

**Deepanshu Gupta** — Java Backend Developer
- 📧 dipanshu.raj989@gmail.com
- 💼 [LinkedIn](https://linkedin.com/in/dev-deepanshu-gupta)
- 🐙 [GitHub](https://github.com/dipanshu97)
