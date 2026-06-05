<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=shark&height=360&color=gradient&customColorList=6,12,20,24,30&text=Santosh%20Nesargi&fontColor=ffffff&desc=Web%20Designer%20•%20Backend%20Developer%20•%20SaaS%20Builder&descAlign=50&fontAlign=50&fontAlignY=40&animation=twinkling&stroke=00FFA3&strokeWidth=1.5" width="100%" />
</p>
# 🎓 School Management System

A web-based School Management System developed using **HTML, CSS, JavaScript, JSP, Java Servlets, JDBC, and MySQL**. The system provides role-based access for Admin, Teacher, and Student users to manage academic and administrative activities efficiently.

---

## 📖 Project Description

The School Management System is designed to automate and simplify school operations. It provides separate dashboards and functionalities for Admin, Teacher, and Student users.

The system allows:

* Student record management
* Authentication and authorization
* Attendance management
* Marks management
* Class timetable management
* Exam timetable management

---

## 🚀 Technologies Used

### Frontend

* HTML5
* CSS3
* JavaScript
* JSP

### Backend

* Java Servlets
* JDBC

### Database

* MySQL

### Server

* Apache Tomcat

### Development Environment

* Eclipse IDE

---

## 🏗️ System Architecture

The application follows a **3-Tier Architecture**.

### 1. Presentation Layer

Responsible for user interaction.

Technologies:

* HTML
* CSS
* JavaScript
* JSP

Pages:

* Login Page
* Registration Page
* Admin Dashboard
* Teacher Dashboard
* Student Dashboard

### 2. Business Logic Layer

Responsible for processing requests and business operations.

Technologies:

* Java Servlets
* JDBC

Modules:

* Authentication
* Student Management
* Teacher Management
* Session Management

### 3. Database Layer

Responsible for storing and retrieving data.

Technology:

* MySQL

Stores:

* User Details
* Student Information
* Teacher Information
* Attendance Records
* Marks Records
* Timetable Information

---

## 🔄 System Workflow

```text
User
 │
 ▼
Login Page
 │
 ▼
LoginServlet
 │
 ▼
JDBC Connection
 │
 ▼
MySQL Database
 │
 ▼
Authentication
 │
 ├────────── Admin Dashboard
 │
 ├────────── Teacher Dashboard
 │
 └────────── Student Dashboard
```

---

## 👨‍💼 Admin Module

Admin has complete control over the system.

### Features

#### Student Management

* Add Student
* Update Student
* Delete Student
* View Student Details
* Search Student
* View Students by Class

#### Class Timetable Management

* Add Timetable
* Update Timetable
* Delete Timetable
* View Timetable

#### Exam Timetable Management

* Add Exam Timetable
* Update Exam Timetable
* Delete Exam Timetable
* View Exam Timetable

#### Authentication

* Login
* Logout

---

## 👨‍🏫 Teacher Module

Teachers can manage student academic information.

### Features

#### Student Information

* View Student Details

#### Marks Management

* Add Marks
* Update Marks
* Delete Marks
* View Marks

#### Attendance Management

* Add Attendance
* View Attendance
* Attendance Summary

#### Timetable Access

* View Class Timetable
* View Exam Timetable

#### Authentication

* Login
* Logout

---

## 👨‍🎓 Student Module

Students can access their academic information using their credentials.

### Features

#### Profile

* View Profile

#### Academic Information

* View Marks
* View Attendance
* View Attendance Summary

#### Timetables

* View Class Timetable
* View Exam Timetable

#### Authentication

* Login
* Logout

---

## 📂 Project Structure

```text
School-Management-System
│
├── src/main/java
│   └── Students
│       ├── Jdbc.java
│       ├── LoginServlet.java
│       ├── LogoutServlet.java
│       ├── RegisterServlet.java
│       ├── server.java
│       ├── StudentServlet.java
│       └── TeacherServlet.java
│
├── src/main/webapp
│   ├── META-INF
│   ├── WEB-INF
│   ├── admin.jsp
│   ├── teacher.jsp
│   ├── student.jsp
│   ├── login.jsp
│   ├── register.jsp
│   ├── index.jsp
│   │
│   ├── admin.css
│   ├── teacher.css
│   ├── student.css
│   ├── login.css
│   ├── register.css
│   └── style.css
│
├── build
│
└── Deployment Descriptor
```

---

## 🗄️ Database Tables

* Admin
* Teacher
* Student
* Attendance
* Marks
* ClassTimetable
* ExamTimetable

---

## 🔐 Authentication & Security

* User Login Authentication
* Session Management
* Secure Logout
* Role-Based Access Control
* Input Validation

---

## ✨ Key Features

✅ Role-Based Login System

✅ Admin Dashboard

✅ Teacher Dashboard

✅ Student Dashboard

✅ Student Management

✅ Attendance Management

✅ Marks Management

✅ Class Timetable Management

✅ Exam Timetable Management

✅ Search Functionality

✅ Session Handling

---

## ⚙️ Installation Guide

### 1. Clone Repository

```bash
git clone https://github.com/your-username/school-management-system.git
```

### 2. Create Database

```sql
CREATE DATABASE school_management;
```

### 3. Import SQL File

Import the database script into MySQL.

### 4. Configure Database

Update JDBC credentials in:

```java
Jdbc.java
```

### 5. Configure Apache Tomcat

* Install Apache Tomcat
* Add Tomcat Server in Eclipse
* Deploy the project

### 6. Run the Application

```text
http://localhost:8080/Details1
```

---

## 🎯 Future Enhancements

* Online Fee Management
* Parent Portal
* Email Notifications
* SMS Notifications
* Report Card Generation
* Online Examination System
* Mobile Application Support

---

## 👨‍💻 Author

**Santosh Nesargi**
LinkedIn[:]https://www.linkedin.com/in/santosh-nesargi

School Management System Project


---

## 📜 License

This project is developed for educational and learning purposes.

