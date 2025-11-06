# Employee Leave Management System

A Spring Boot application that manages employee records and their leave requests. The project enforces real-world business rules for leave accrual, validation, and approval workflows.

---

## 🛠 Tech Stack

* **Java 21**
* **Spring Boot 3.5.7**
* **Spring Data JPA (Hibernate)**
* **PostgreSQL**
* **Docker & Docker Compose**
* **Jakarta Validation**

---

## 🚀 Run the Application (Using Docker Compose)

### **1. Clone the repository**

```bash
git clone <repository-url>
cd <project-folder>
```

### **2. Build and start containers**

```bash
docker-compose up --build
```

### **3. Verify running services**

```bash
docker ps
```

---

## 📌 API Endpoints

| Method | Endpoint                            | Description                  |
| ------ | ----------------------------------- | ---------------------------- |
| `POST` | `/api/employees/saveOrUpdate`       | Create or update an employee |
| `GET`  | `/api/employees/{id}/balance`       | Get current leave balance    |
| `POST` | `/api/leaves/submit`                | Submit a leave request       |
| `POST` | `/api/leaves/leaveApprovalOrReject` | Approve or reject leave      |

---

## 📘 Example API Requests

### ✅ **1. Create / Update Employee**

```bash
curl --location 'http://localhost:8080/api/v1/employeeDetails/saveOrUpdate' \
--header 'Content-Type: application/json' \
--data-raw '{
"name": "Tamil",
"email": "tamil@gmail.com",
"joiningDate": "2024-04-18",
"leaveBalance": 30
}'
```

**Response:**

```
Employee Details Created Successfully
```

---

### ✅ **2. Submit Leave Request**

```bash
curl --location 'http://localhost:8080/api/v1/leaveRequest/submit' \
--header 'Content-Type: application/json' \
--data '{
"employeeId": 7,
"startDate": "2025-11-12",
"endDate": "2025-11-13",
"type": "PRIVILEGED",
"status": "PENDING"
}'
```

**Response:**

```
Leave Request Submitted Successfully
```

---

### ✅ **3. Approve or Reject Leave**

```bash
curl --location 'http://localhost:8080/api/v1/leaveRequest/leaveApprovalOrReject' \
--header 'Content-Type: application/json' \
--data '{
"leaveRequestId": 5,
"status": "APPROVED"
}'
```

**Response:**

```
Approved Successfully
```

---

### ✅ **4. Get Employee Leave Balance**

```bash
curl --location 'http://localhost:8080/api/v1/employeeDetails/leaveBalanceCheck/7' \
--header 'Content-Type: application/json'
```

**Response:**

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "joiningDate": "2024-05-10",
  "leaveBalance": 21.00
}
```

---

## 📚 Business Logic

### **1. Leave Accrual**

* Employees earn **1.75 privileged leave days per full month worked**
* Maximum leave balance capped at **30 days**

### **2. Carry Forward**

* Up to **10 unused privileged days** can be carried forward *(placeholder logic)*

### **3. Validation Rules**

* Email must be **unique**
* Privileged leave cannot exceed **remaining leave balance**

### **4. Approval Workflow**

* Only **PENDING** leaves can be approved or rejected
* On approval: privileged leave balance is **deducted**

### **5. Overlap Prevention**

* No overlapping **approved leaves** allowed for the same employee

---

## 📁 Folder Structure

```
src/
├── main/java/employeeLeaveManagementSystem/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── exception/
├── main/resources/
│   ├── application.properties
│   └── docker-compose.yml
```

---
