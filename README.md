# 💰 MotorPH Payroll System (Group 8)

## 📖 Overview
MotorPH Payroll System is a **Java-based payroll management system** designed to automate employee salary calculations, deductions, attendance tracking, and leave management. The project follows **Advanced Object-Oriented Programming (AOOP)** principles and integrates with **PostgreSQL 16** using **JDK 23** to securely store and manage payroll data.

---

## 🔧 Prerequisites
Before running this project, ensure you have installed:

- **Java Development Kit (JDK 23)** → [Download JDK 23](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
- **PostgreSQL 16** → [Download PostgreSQL 16](https://www.postgresql.org/download/)
- **Apache NetBeans (Recommended IDE)** → [Download NetBeans](https://netbeans.apache.org/download/index.html)
---

## 🛠 Database Setup: Restoring `postgres.dump`

### **Step 1: Download `postgres.dump`**
Download the database dump file via the following link:  
📥 **[Download postgres.dump](https://drive.google.com/file/d/1WwFq37e_yk4N4PvOtZx0H8D9JFq9JuEk/view?usp=sharing)**  

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
& "C:\Program Files\PostgreSQL\16\bin\pg_restore.exe" -U postgres -d postgres "postgres.dump"
```
### 💡 Notes:
- This assumes that **PostgreSQL** is installed in the **default location** (`C:\Program Files\PostgreSQL\16\`).
- If prompted, **enter your PostgreSQL password**.
- The database will be restored under the name **"postgres"** (you can modify this if needed).
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

### 🔧 Configure the `db.properties` File
In NetBeans, navigate to: 

**MotorPH_AOOP> Other Sources > src/main/resources > config**

Ensure your `db.properties` file matches your PostgreSQL setup:

```java

db.url=jdbc:postgresql://localhost:5432/postgres
db.username=postgres
db.password=[yourpassword]
```
---
### 📁 Project Documentation

- Homework 3 | Unified Plan for MotorPH Payroll System Expansion

<div align="center">
  <img src="https://github.com/user-attachments/assets/b227bbe0-348e-4661-829f-6fdf4121bde1" alt="469kxyxh-400" width="500"/>
</div>


- Homework 7 | Unit Testing: https://tinyurl.com/5f2t58x7

![5f2t58x7-400](https://github.com/user-attachments/assets/02f20e5f-195e-46be-820e-7064e74e0444)

