# 💰 MotorPH Payroll System (Group 1)

## 📖 Overview
MotorPH Payroll System is a **Java-based payroll management system** designed to automate employee salary calculations, deductions, and attendance. The project follows **Object-Oriented Programming (OOP)** principles and integrates with **PostgreSQL 16** using **JDK 23** to securely store and manage payroll data.

---

## 🔧 Prerequisites
Before running this project, ensure you have installed:

- **Java Development Kit (JDK 23)** → [Download JDK 23](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
- **PostgreSQL 16** → [Download PostgreSQL 16](https://www.postgresql.org/download/)
---

## 🛠 Database Setup: Restoring `postgres.dump`

### **Step 1: Download `postgres.dump`**
Download the database dump file via the following link:  
📥 **[Download postgres.dump](https://drive.google.com/file/d/1Z8sjzk5R2BPrzGBSkDAOO6i1lBkVk3Xo/view?usp=sharing)**  

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
cd Downloads
```
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
    2. In the left panel, **right-click** on **Login/Group Roles**.
    3. Select **Create** → **Login/Group Role**.
    4. In the **Name** field, enter:  
       ```
       admin
       ```
    5. Click **Save**.

### 🔧 Configure the `DatabaseConnection` Java Class 
In your chosen IDE, navigate to: 

**MotorPH_OOP> Source Packages > com.payroll.util > DatabaseConnection.java**

Ensure that the **database URL, username, and password** match your **PostgreSQL credentials**:

```java

private final String url = "jdbc:postgresql://localhost:5432/postgres"; // 
private final String username = "postgres"; 
private final String password = "[yourpassword]"; 
```
