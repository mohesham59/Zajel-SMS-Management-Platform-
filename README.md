# 📡 Zajel — Twilio SMS Client

A **Java-based SMS management system** built using **Servlets, JSP, PostgreSQL, and Docker**, integrated with **Twilio API** for sending and managing SMS messages.

The system is split into two main modules:
- **`zajel-engine`** → Business logic (DAO, Models, Servlets, Filters, Utils)
- **`zajel-webapp`** → Presentation layer (JSP views, HTML, CSS)

---

## 🏗️ Architecture Overview

```

User (Browser)
│
▼
JSP Views (zajel-webapp)
│
▼
Servlet Controllers (zajel-engine)
│
▼
DAO Layer (JDBC)
│
▼
PostgreSQL Database
│
▼
Twilio API (SMS Delivery)

```

---

## ✨ Features

- 👤 User Authentication (Admin / Customer)
- 📩 Send SMS via Twilio API
- 📜 SMS History tracking per user
- 🔍 Search & filter SMS messages
- 🧑‍💼 Admin dashboard (customers, stats, management)
- 🔐 Role-based access control (via filters)
- 🗄️ PostgreSQL integration via JDBC
- 🐳 Dockerized deployment (App + DB)
- 📦 Clean separation between Engine and Web layers
- 📄 JSP-based MVC-like architecture

---

## 🛠️ Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java 11 |
| Backend | Servlets |
| Frontend | JSP / HTML / CSS |
| Database | PostgreSQL |
| Connectivity | JDBC |
| External API | Twilio SMS API |
| Build Tool | Maven |
| Application Server | Apache Tomcat 9 |
| Deployment | Docker / Docker Compose |

---

## 📁 Project Structure

```

Zajel-TwilioSMSClient/
│
├── zajel-engine/
│   ├── dao/              # Database access layer
│   ├── model/            # Entities (Admin, Customer, Msg...)
│   ├── servlet/         # Controllers (Login, SMS, Admin...)
│   ├── filter/          # Auth & Encoding filters
│   └── util/            # Helpers (Twilio, Hashing, HTML utils)
│
├── zajel-webapp/
│   ├── WEB-INF/
│   │   ├── views/
│   │   │   ├── admin/
│   │   │   ├── auth/
│   │   │   └── customer/
│   │   ├── web.xml
│   │   └── beans.xml
│   ├── index.html
│   ├── style.css
│   └── logo.svg
│
├── Database_Schema.sql
├── Dockerfile
├── docker-compose.yml
└── pom.xml

````

---

## ⚙️ Prerequisites

- Java JDK 11+
- Maven 3.6+
- PostgreSQL 13+
- Docker (optional but recommended)
- Apache Tomcat 9+

---

## 🚀 Running with Docker

### 1. Build & start services

```bash
docker compose up --build
````

---

### 2. Services

| Service    | URL                                            |
| ---------- | ---------------------------------------------- |
| Web App    | [http://localhost:8080](http://localhost:8080) |
| PostgreSQL | localhost:5432                                 |

---

## 🗄️ Database Setup

Database is initialized automatically using:

```sql
Database_Schema.sql
```

It runs on first container startup via:

```
/docker-entrypoint-initdb.d/init.sql
```

---

## 🔐 Environment Variables

```env
DB_URL=jdbc:postgresql://db:5432/zajel_db
DB_USER=root
DB_PASSWORD=password
```

---

## 📡 Twilio Integration

SMS sending is handled via:

```
TwilioHelper.java
```

It wraps Twilio SDK for:

* Sending SMS
* Managing authentication credentials
* Handling API responses

---

## 🔄 Build Without Docker

### Build project

```bash
mvn clean package
```

### Deploy WAR file

Copy:

```
zajel-webapp/target/zajel-webapp-One.war
```

to Tomcat:

```
/webapps/ROOT.war
```

---

## 📊 System Modules

### 👨‍💼 Admin Module

* Manage customers
* View system statistics
* Monitor SMS logs

### 👤 Customer Module

* Send SMS
* View SMS history
* Manage profile

### 🔐 Authentication Module

* Login / Register
* OTP verification (if enabled)

---

## ⚠️ Known Issues

* PostgreSQL port conflict (5432 already in use)
* Ensure local PostgreSQL is stopped when using Docker DB
* DB schema auto-load runs only on first container startup

---

## 🛠️ Troubleshooting

### Port 5432 already in use

```bash
sudo lsof -i :5432
sudo systemctl stop postgresql
```

---

## 📈 Future Improvements

* REST API layer (replace JSP-based MVC)
* React frontend upgrade
* Message queue (Kafka/RabbitMQ)
* SMS retry mechanism
* CI/CD pipeline (GitHub Actions)
* Role-based permission system enhancement

---

## 👨‍💻 Authors

**Mohamed Hesham**  
GitHub: [mohesham59](https://github.com/mohesham59)

**Mahmoud Salah**  
GitHub: [MSalah011](https://github.com/MSalah011)

**Seif Abdelsalam**  
GitHub: [seifabsalam](https://github.com/seifabsalam)

**Medhat Osama**  
GitHub: [Medhat31](https://github.com/Medhat31)

---

## ⭐ Support

If you like this project:

* ⭐ Star the repository
* 🍴 Fork it
* 📢 Share it
