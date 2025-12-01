---

# **Employee Leave Management System – Week 2 Upgrade**

A secure, event-driven microservices-based system for managing employee leave requests.
This week’s enhancement introduces authentication with JWT, Kafka-based event publishing, a new Notification Service, and Redis caching.

---

# 🧱 **Project Structure**

```
/leave-service                → Main service (JWT, Kafka Producer, Redis Cache)
/notification-service         → Kafka consumer (no DB, no auth)
/docker-compose.yml           → Single orchestration file
typing-proof-week2.png        → Touch typing proof
english-video-week2.txt       → English video link
```

---

# 🛠 **Tech Stack**

### **Backend**

* Java 21
* Spring Boot 3.5.x
* Spring Security + JWT
* Spring Data JPA
* Apache Kafka (KRaft mode)
* Redis (caching)

### **Infra**

* Docker & Docker Compose
* PostgreSQL 15
* Kafka 3.7 (KRaft)
* Redis 7-alpine

---

# 🔐 **Authentication & Roles (JWT)**

Three hardcoded users:

| Username | Password | Role     |
| -------- | -------- | -------- |
| emp1     | password | EMPLOYEE |
| emp2     | password | EMPLOYEE |
| mgr1     | password | MANAGER  |

After login:

```json
{
  "jwt": "xxxxx.yyyyy.zzzzz",
  "userId": 101,
  "roles": ["EMPLOYEE"]
}
```

### **Access Rules**

* **EMPLOYEE** → create leave request, view own leave balance
* **MANAGER** → approve / reject any leave request

JWT is stateless (no server-side sessions).

---

# 📡 **Event-Driven Architecture**

### **Kafka Topic:** `leave-events`

On manager approval, the Leave Service publishes:

```json
{
  "eventType": "LEAVE_APPROVED",
  "employeeId": 101,
  "startDate": "2025-11-20",
  "endDate": "2025-11-22",
  "totalDays": 3,
  "approvedAt": "2025-11-14T12:00:00Z"
}
```

### **Notification Service**

* Independent Spring Boot app
* Listens to `leave-events`
* Logs:
  **Notification: Leave approved for employee {id}**
* No DB, no HTTP, no auth

---

# ⚡ **Redis Caching**

**Key format:**

```
balance:{employeeId}
```

**Used for:**

* Fast leave balance lookup

**Cache invalidation:**

* On leave approval → recalculate balance, update Redis

**Logs:**

* Cache HIT / MISS logged at INFO level

---

# 🐳 **Run Using Docker Compose**

### **1. Clone the repository**

```bash
git clone https://github.com/Krishnaprasad628/employee-leave-system
cd employee-leave-system
```

### **2. Start all services**

```bash
docker-compose up --build
```

### **3. Services included**

| Service              | Description                           |
| -------------------- | ------------------------------------- |
| leave-service        | main app (JWT, Kafka producer, Redis) |
| notification-service | Kafka consumer                        |
| db                   | PostgreSQL                            |
| redis                | leave balance caching                 |
| kafka                | KRaft mode                            |

> Spring Boot apps auto-retry until Kafka/Redis/Postgres are ready.

---

# 📌 **Core API Endpoints (Leave-Service)**

### **Authentication**

| Method | Endpoint      | Description |
| ------ | ------------- | ----------- |
| `POST` | `/auth/login` | returns JWT |

### **Employee / Leave Actions**

| Method | Endpoint              | Description                |
| ------ | --------------------- | -------------------------- |
| `GET`  | `/api/balance/{id}`   | get leave balance (cached) |
| `POST` | `/api/leaves/apply`   | submit leave               |
| `POST` | `/api/leaves/approve` | approve/reject leave       |

---

# 🔄 **Event Flow (End-to-End)**

1. Manager approves a leave request
2. Leave Service updates DB and Redis
3. Leave Service publishes **LeaveEvent** to Kafka
4. Notification Service consumes event
5. A console log simulates SMS/email

---

# 📚 **Caching Strategy**

* First request → checks DB → stores in Redis
* Next request → reads from Redis (HIT)
* After approval → cache key `balance:{employeeId}` is refreshed immediately
* Prevents stale data & reduces DB load

---

# 🐳 **Docker Compose Overview**

All services run on a shared internal Docker network:

```
leave-service
notification-service
postgres (db)
redis
kafka (KRaft mode)
```

Kafka auto-creates topics using:

```
KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true
```

---

# 🌐 **Example Login Request**

```bash
curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "username": "emp1",
  "password": "password"
}'
```

---

# 📁 **Folder Structure**

```
leave-service/
   src/main/java/...
   Dockerfile

notification-service/
   src/main/java/...
   Dockerfile

docker-compose.yml
README.md
typing-proof-week2.png
english-video-week2.txt
```

---

# 🧪 **Testing the System**

1. Login → get JWT
2. Apply leave as EMPLOYEE
3. Approve leave as MANAGER
4. Kafka event published
5. Notification Service logs:

   ```
   Notification: Leave approved for employee 101
   ```
6. Redis cache updated

---

