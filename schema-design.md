# Database Schema Design

## MySQL Tables

### Doctor

| Column    | Type         | Constraints                 |
|-----------|--------------|-----------------------------|
| id        | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| name      | VARCHAR(100) | NOT NULL                    |
| specialty | VARCHAR(50)  | NOT NULL                    |
| email     | VARCHAR(255) | NOT NULL, UNIQUE            |
| password  | VARCHAR(255) | NOT NULL                    |
| phone     | VARCHAR(10)  | NOT NULL                    |

### Doctor_Available_Times

| Column          | Type         | Constraints              |
|-----------------|--------------|--------------------------|
| doctor_id       | BIGINT       | FOREIGN KEY → Doctor(id) |
| available_times | VARCHAR(255) |                          |

### Patient

| Column   | Type         | Constraints                 |
|----------|--------------|-----------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| name     | VARCHAR(255) | NOT NULL                    |
| email    | VARCHAR(255) | NOT NULL, UNIQUE            |
| password | VARCHAR(255) | NOT NULL                    |
| phone    | VARCHAR(10)  | NOT NULL                    |
| address  | VARCHAR(255) |                             |

### Appointment

| Column           | Type     | Constraints                 |
|------------------|----------|-----------------------------|
| id               | BIGINT   | PRIMARY KEY, AUTO_INCREMENT |
| doctor_id        | BIGINT   | FOREIGN KEY → Doctor(id)    |
| patient_id       | BIGINT   | FOREIGN KEY → Patient(id)   |
| appointment_time | DATETIME | NOT NULL                    |
| status           | INT      | NOT NULL                    |

### Admin

| Column   | Type         | Constraints                 |
|----------|--------------|-----------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(255) | NOT NULL, UNIQUE            |
| password | VARCHAR(255) | NOT NULL                    |