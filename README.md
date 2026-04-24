# 🏦 Mini Banking System

A desktop-based banking application built with **Java Swing** (UI), **Core Java** (backend logic), and **MySQL** (database), connected via **JDBC**.

---

## ✨ Features

| Feature | Description |
|---|---|
| Login System | Admin/User login validated against the database |
| Create Account | Open new bank accounts with full customer details |
| View Accounts | List, search, edit, and delete accounts |
| Deposit | Add money to any account with full transaction logging |
| Withdraw | Withdraw with insufficient-balance protection |
| Check Balance | Instant balance lookup with account info card |
| Transaction History | Full history with colour-coded DEPOSIT / WITHDRAWAL rows |

---

## 🛠 Tech Stack

- **Frontend / UI**: Java Swing
- **Backend Logic**: Core Java
- **Database**: MySQL 8.x
- **Connectivity**: JDBC (MySQL Connector/J 8.x)

---

## 📁 Project Structure

```
MiniBankingSystem/
├── src/
│   ├── db/
│   │   └── DBConnection.java          ← DB credentials go here
│   ├── model/
│   │   ├── Account.java
│   │   └── Transaction.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── AccountDAO.java
│   │   └── TransactionDAO.java
│   ├── ui/
│   │   ├── LoginFrame.java
│   │   ├── DashboardFrame.java
│   │   ├── CreateAccountFrame.java
│   │   ├── ViewAccountsFrame.java
│   │   ├── DepositFrame.java
│   │   ├── WithdrawFrame.java
│   │   ├── BalanceFrame.java
│   │   └── TransactionHistoryFrame.java
│   └── Main.java
├── database/
│   └── minibankdb.sql
└── README.md
```

---

## 🔧 Database Setup

### 1. Install MySQL
Make sure MySQL is running on your machine.

### 2. Create the database and tables

**Using MySQL CLI:**
```sql
mysql -u root -p
source /path/to/MiniBankingSystem/database/minibankdb.sql;
```

Or open the file in **MySQL Workbench** and execute it.

### 3. Verify
```sql
USE MiniBankDB;
SHOW TABLES;
SELECT * FROM users;
```

---

## 🔑 Default Admin Login

| Field    | Value      |
|----------|------------|
| Username | `admin`    |
| Password | `admin123` |

---

## ⚙️ JDBC Connector Setup

### Download
Download **MySQL Connector/J** (version 8.x) from:
👉 https://dev.mysql.com/downloads/connector/j/

Choose "Platform Independent" → download the ZIP → extract the JAR (e.g., `mysql-connector-j-8.x.x.jar`).

### Add to Classpath

**IntelliJ IDEA:**
1. File → Project Structure → Libraries → `+` → Java
2. Select the connector JAR → OK → Apply

**Eclipse:**
1. Right-click project → Build Path → Configure Build Path
2. Libraries tab → Add External JARs → select the connector JAR

**VS Code (with Language Support for Java):**
Add the JAR path to `java.project.referencedLibraries` in `settings.json`.

---

## 🔐 Change MySQL Credentials

Open `src/db/DBConnection.java` and update lines 16–18:

```java
private static final String DB_URL  = "jdbc:mysql://localhost:3306/MiniBankDB?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root";       // ← your MySQL username
private static final String DB_PASS = "root123";    // ← your MySQL password
```

---

## ▶️ How to Run

### Option A – Command Line (Mac / Windows / Linux)

```bash
# 1. Compile (from the MiniBankingSystem/ directory)
javac -cp ".:lib/mysql-connector-j-8.x.x.jar" -d out \
      src/db/*.java src/model/*.java src/dao/*.java src/ui/*.java src/Main.java

# 2. Run
java -cp ".:out:lib/mysql-connector-j-8.x.x.jar" Main

# On Windows use semicolons instead of colons:
javac -cp ".;lib\mysql-connector-j-8.x.x.jar" -d out ...
java  -cp ".;out;lib\mysql-connector-j-8.x.x.jar" Main
```

> Tip: Place the connector JAR in a `lib/` folder inside the project.

### Option B – IntelliJ IDEA (recommended)

1. Open → select the `MiniBankingSystem` folder as project root
2. Mark `src/` as **Sources Root**
3. Add MySQL connector JAR to Libraries (see above)
4. Right-click `Main.java` → **Run 'Main.main()'**

### Option C – Eclipse

1. New Java Project → point to folder
2. Add `src` as source folder
3. Add connector JAR to Build Path
4. Run `Main.java`

---

## 📸 Application Flow

```
Main.java
  └─► LoginFrame         (validate credentials)
        └─► DashboardFrame (menu hub)
              ├─► CreateAccountFrame
              ├─► ViewAccountsFrame  (edit / delete)
              ├─► DepositFrame
              ├─► WithdrawFrame
              ├─► BalanceFrame
              └─► TransactionHistoryFrame
```

---

## 📝 Notes

- Passwords are stored as plain text for simplicity. In production, use BCrypt hashing.
- All monetary values use `DECIMAL(12,2)` and Java `BigDecimal` to avoid floating-point errors.
- Deposit and withdrawal operations use JDBC transactions (`setAutoCommit(false)` + `commit` / `rollback`) to ensure data integrity.
- All SQL queries use **PreparedStatement** to prevent SQL injection.
# minibankingsystem
