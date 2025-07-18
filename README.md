# 💰 MotorPH Payroll System (Group 8)

## 📖 Overview
MotorPH Payroll System is a **Java-based payroll management system** designed to automate employee salary calculations, deductions, attendance tracking, and leave management. The project follows **Advanced Object-Oriented Programming (AOOP)** principles and integrates with **PostgreSQL 16** using **JDK 23** to securely store and manage payroll data.

---

## 👥 Development Team

* Shekinah Jabez Florentino
* Lenie Joice Mendoza
* Maricar Punzalan
* Mary Jullianne Maycacayan
* Nicolas Lugtu
* Paulo Martin Molina

**Under the guidance of:**
Sir Glenn Baluyot

---

## 🔧 Prerequisites
Before running this project, ensure you have installed:

- **Java Development Kit (JDK 23)** → [Download JDK 23](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
- **PostgreSQL 16** → [Download PostgreSQL 16](https://www.postgresql.org/download/)
- **Apache NetBeans (Recommended IDE)** → [Download NetBeans](https://netbeans.apache.org/download/index.html)
---

## 🛠 Database Setup: Restoring `motorph_db.dump`

### **Step 1: Download `motorph_db.dump`**
Download the database dump file via the following link:  
📥 **[Download motorph_db.dump](https://drive.google.com/file/d/1a-K7YDQOH1LX3Znp4D1qyRnVP1DQad40/view?usp=sharing)**  

Do not move the file from your **Downloads** directory.

---

### **Step 2: Open PowerShell as Administrator** (Windows Users Only)

1. **Click** on the **Start Menu**  
2. **Type** `PowerShell`  
3. **Right-click** on **Windows PowerShell** and select **Run as Administrator**  
4. **Confirm the User Account Control (UAC) prompt**  

> **⚠️ Note:** Running PowerShell as Administrator is required to avoid permission issues when restoring the database.

---

### **Step 3: Navigate to the Downloads Folder**
In PowerShell, run:
```powershell
cd $HOME\Downloads
```

---

### **Step 4: Restore Database Using PowerShell**  
Run the following command to restore the database:  

```powershell
& "C:\Program Files\PostgreSQL\16\bin\pg_restore.exe" -U postgres -d postgres "motorph_db.dump"
```
### 💡 Notes:
- This assumes that **PostgreSQL** is installed in the **default location** (`C:\Program Files\PostgreSQL\16\`).
- If prompted, **enter your PostgreSQL password**.
- The database will be restored under the name **"motorph_db"** (you can modify this if needed).
- This assumes that the **admin role** is set up in PostgreSQL, as shown in **pgAdmin** under **Login/Group Roles**.
  - **If the `admin` role does not exist, create it manually in pgAdmin**:
    1. Open **pgAdmin**.
    2. In the left panel, expand your server.
    3. Connect to **PostgreSQL 16**.
    4. Scroll down, then **right-click** on **Login/Group Roles**.
    5. Select **Create** → **Login/Group Role**.
    6. In the **Name** field, enter:  
       ```
       admin
       ```
    7. Click **Save**.
---

### **Alternative Database Setup (For Existing PostgreSQL Users)**

If you already have PostgreSQL installed and prefer to use its built-in Query Tool:

1.  **Download the SQL file** from the following link:
    📥 **[Download motorph_db.sql](https://drive.google.com/file/d/1Z-0NXCIZyAGZOnCGvu7lEDkZii87_LLG/view?usp=sharing)**

2.  Open **pgAdmin** (or your preferred PostgreSQL client).

3.  Connect to your PostgreSQL 16 server.

4.  Right-click on **Databases** and select **Create** → **Database...**. Name the new database `motorph_db`.

5.  Right-click on the newly created `motorph_db` and select **Query Tool**.

6.  In the Query Tool window:
    * Click on the **"Open file" icon** (usually looks like an open folder) in the toolbar.
    * Navigate to your **Downloads** folder (or wherever you saved `motorph_db.sql`).
    * Select the `motorph_db.sql` file and click **"Open"**. The SQL commands will now appear in the Query Editor pane.
    * To execute the contents, click on the **"Execute/Refresh" icon** (usually a lightning bolt or play button). This will run all the SQL commands in the file, creating the necessary tables and inserting data into your `motorph_db` database.

### 🔧 Configure the `db.properties` File
In NetBeans, navigate to: 

**MotorPH_AOOP> Other Sources > src/main/resources > config**

Ensure your `db.properties` file matches your PostgreSQL setup:

```java

db.url=jdbc:postgresql://localhost:5432/motorph_db
db.username=postgres
db.password=[yourpassword]
```
---
### 📁 Project Documentation

<h3 align="center">Homework 3 | Unified Plan for MotorPH Payroll System Expansion</h3>

<div align="center">
  <a href="https://docs.google.com/spreadsheets/d/1HDd5QNA5HaLYVDqjb4cPjdZvc39bwnxxZYSiUCOSfws/edit?usp=sharing" target="_blank">
    <img src="https://github.com/user-attachments/assets/b227bbe0-348e-4661-829f-6fdf4121bde1" alt="469kxyxh-200" width="300"/>
  </a>
</div>


<h3 align="center">Homework 7 | Unit Testing</h3>

<div align="center">
  <a href="https://docs.google.com/spreadsheets/d/1F3L0Za7bEWXZnBvyUVt2QsKkvnZ3Y8Q8ryHLwzJaAoU/edit?usp=sharing" target="_blank">
    <img src="https://github.com/user-attachments/assets/02f20e5f-195e-46be-820e-7064e74e0444" alt="5f2t58x7-400" width="300" height="300"/>
  </a>
</div>

## 📁 Project Documentation

### 🧾 Homework 3 – Unified Plan for MotorPH Payroll System Expansion

This document outlines the strategic roadmap for scaling the MotorPH Payroll System. It includes proposed infrastructure changes, resource allocation, and branch-level coordination to ensure a seamless rollout.

🔗 [View Homework 3 - Unified Plan Spreadsheet](https://docs.google.com/spreadsheets/d/1HDd5QNA5HaLYVDqjb4cPjdZvc39bwnxxZYSiUCOSfws/edit?usp=sharing)

<p align="center">
  <img src="https://github.com/user-attachments/assets/b227bbe0-348e-4661-829f-6fdf4121bde1" alt="Unified Plan Preview" width="300"/>
</p>

---

### 🧪 Homework 7 – Unit Testing

This document contains detailed unit test cases used to validate key modules in the MotorPH Payroll System. It ensures that all components perform correctly and catch errors early during the development cycle.

🔗 [View Homework 7 - Unit Testing Spreadsheet](https://docs.google.com/spreadsheets/d/1F3L0Za7bEWXZnBvyUVt2QsKkvnZ3Y8Q8ryHLwzJaAoU/edit?usp=sharing)

<p align="center">
  <img src="https://github.com/user-attachments/assets/02f20e5f-195e-46be-820e-7064e74e0444" alt="Unit Testing Preview" width="300" height="300"/>
</p>

---

### 📄 Expanded MotorPH Payroll System – Software Requirements Specification (SRS)

This document serves as the official Software Requirements Specification (SRS) for the expanded MotorPH Payroll System. It details the purpose, scope, system features, non-functional requirements, user roles, assumptions, and diagrams supporting the system design.

Key contents include:

- 📌 **System Features**: Employee self-service, leave application, payroll processing, report generation, role-based access, and IT admin tools  
- ⚙️ **Non-functional Requirements**: Security, performance, accuracy, reliability, and maintainability  
- 👥 **User Roles**: Employees, HR, Finance, and IT Admins, each with tailored functionality  
- 📊 **Diagrams**: Use Case, Class, and Entity Relationship Diagram  
- 📚 **Glossary**: Definitions of technical terms and acronyms used throughout the system

Prepared by:
- Lenie Joice Mendoza  
- Maricar Punzalan  
- Mary Jullianne Maycacayan  
- Nicolas Lugtu  
- Paulo Martin Molina  
- Shekinah Jabez Florentino

🔗 [View SRS Document](https://docs.google.com/document/d/1vB5lh3BgKx0Eh3Q6CYR_sYkr_jsyKrs6eeQdrjF639U/edit?usp=sharing)



