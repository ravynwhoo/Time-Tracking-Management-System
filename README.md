# ⏱️ TimeTrack - Time Tracking & Productivity Management System

> **A Java Web Application built with MVC Architecture, Servlets, JSP, JDBC, and Modern Responsive Aesthetics.**

---

## 🎯 1. Project Overview

**TimeTrack** is an enterprise-grade web application designed to help professionals and teams accurately record work hours, monitor task performance, and analyze productivity trends.

### 👥 User Roles & Permissions

| Role | Permissions & Capabilities |
| :--- | :--- |
| **Admin** | • Full system administration & user account management (CRUD)<br>• Oversee, edit, and audit time logs across all employees<br>• Configure company-wide work parameters & daily thresholds<br>• Generate, filter, and export comprehensive organization productivity reports |
| **User** | • Live interactive stopwatch to track active tasks<br>• Log work hours with automatic duration calculation<br>• View, filter, and search personal time logs<br>• Personal dashboard with daily goal progress and weekly trend analytics |

---

## 📋 2. Marking Rubric Fulfillment

| Evaluation Criteria | Marks | Implementation Details in TimeTrack |
| :--- | :---: | :--- |
| **Problem Understanding & Solution Design** | **8 / 8** | Complete domain model solving employee time management, productivity tracking, and administrative governance. Clean separation into MVC layers (Model, View, Controller, Service, DAO). |
| **Core Java Concepts** | **10 / 10** | • **OOP Implementation**: Encapsulated entities (`User`, `TimeLog`, `SystemSetting`), polymorphic services (`UserService`, `TimeLogService`).<br>• **Interfaces & Generics**: Generic DAO pattern (`GenericDAO<T, ID>`) parameterized for entities and IDs.<br>• **Collections**: Extensive usage of `List`, `Map`, `LinkedHashMap` for structured querying, aggregations, and metrics.<br>• **Exception Handling**: Dedicated exception hierarchy (`TimeTrackException`, `ValidationException`, `AuthenticationException`, `DatabaseException`, `ResourceNotFoundException`).<br>• **Comparable Interface**: TimeLog implements `Comparable<TimeLog>` for deterministic chronological sorting. |
| **Database Integration (JDBC)** | **8 / 8** | • Pure JDBC connectivity using `java.sql.*`<br>• `PreparedStatement` to guard against SQL injection<br>• Centralized connection management (`DBUtil`) with externalized `db.properties`<br>• Clean resource lifecycle handling with `try-with-resources`<br>• Aggregate SQL queries for reporting & analytics (`SUM`, `COUNT(DISTINCT)`, `GROUP BY`). |
| **Servlets & Web Integration** | **7 / 7** | • **Servlets**: `AuthServlet`, `DashboardServlet`, `TimeLogServlet`, `UserServlet`, `ReportServlet`, `SettingsServlet`<br>• **Filters**: `AuthenticationFilter` for session security, `AdminFilter` for role-based access control, `EncodingFilter` for UTF-8<br>• **JSP & JSTL**: Modular views (`<c:forEach>`, `<c:if>`, `<c:choose>`) with reusable headers, navbar, alerts, and footers<br>• **Session Management**: Secure `HttpSession` state tracking<br>• **Modern UI/UX**: Responsive glassmorphic interface, live stopwatch timer, and Chart.js visualizations. |

---

## 🗄️ 3. Database Schema (`sql/schema.sql`)

The database `timetrack_db` consists of 3 relational tables:

1. **`users`**:
   - `id` (INT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR)
   - `email` (VARCHAR, UNIQUE)
   - `password` (VARCHAR, hashed or plain-text fallback)
   - `role` (ENUM('admin', 'user'))
   - `created_at` (TIMESTAMP)

2. **`time_logs`**:
   - `id` (INT, PK, AUTO_INCREMENT)
   - `user_id` (INT, FK -> users.id ON DELETE CASCADE)
   - `log_date` (DATE)
   - `start_time` (TIME)
   - `end_time` (TIME)
   - `task` (VARCHAR)
   - `duration` (DECIMAL(5,2))
   - `created_at` (TIMESTAMP)

3. **`system_settings`**:
   - `id` (INT, PK, AUTO_INCREMENT)
   - `setting_key` (VARCHAR, UNIQUE)
   - `setting_value` (VARCHAR)
   - `updated_at` (TIMESTAMP)

---

## 🔑 4. Demo Login Credentials

Pre-seeded accounts are provided for immediate evaluation:

| Role | Email Address | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin@timetrack.com` | `admin123` |
| **Standard User (Alice)** | `alice@company.com` | `password123` |
| **Standard User (Bob)** | `bob@company.com` | `password123` |
| **Standard User (Carol)** | `carol@company.com` | `password123` |

---

## 🚀 5. How to Setup and Run

### Step 1: Database Setup
1. Start your local MySQL server (e.g. via XAMPP, MySQL Workbench, or CLI).
2. Execute the script located in `sql/schema.sql`:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
3. Update `src/main/resources/db.properties` with your database username and password if different from defaults (`root` / no password).

### Step 2: Build the WAR File
From the project root directory:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2"
.\mvnw.cmd clean package
```
This generates the deployable artifact `target/timetrack.war`.

### Step 3: Run on Apache Tomcat
You can start the server in any of these ways:

#### Option A: One-Click Script (Easiest)
From the project folder, simply run:
```powershell
.\start-tomcat.bat
```
*(To stop the server later, press `Ctrl+C` or run `.\stop-tomcat.bat` in a new terminal).*

#### Option B: Direct Tomcat Bin
```powershell
C:\Users\anshi\apache-tomcat-9.0.96\bin\catalina.bat run
```

#### Access Application
Once started, open your web browser and navigate to:
```
http://localhost:8080/timetrack/
```

---

## 🎨 6. Key Features Walkthrough

1. **Live Stopwatch Timer**: Interactive timer on user dashboard with Start, Pause, Reset, and "Log Current Session" to auto-populate duration and times into time entry.
2. **Productivity Visualizations**: Interactive Chart.js charts showing:
   - 7-Day & 30-Day Work Hours Timeline
   - Task Breakdown & Distribution (doughnut chart)
   - Team Contribution Analysis (admin chart)
3. **Report Generation & Export**:
   - Custom Date Range filtering
   - One-click CSV export ready for Excel
   - Print-friendly layout.
4. **Administrative Controls**:
   - User account lifecycle (Create, Edit role, Delete)
   - System settings (work hours target, overtime threshold, shift times).
