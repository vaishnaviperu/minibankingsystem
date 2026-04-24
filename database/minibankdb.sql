-- ============================================
-- Mini Banking System - Database Setup Script
-- ============================================

CREATE DATABASE IF NOT EXISTS MiniBankDB;
USE MiniBankDB;

-- ============================================
-- Table: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id   INT PRIMARY KEY AUTO_INCREMENT,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20)
);

-- ============================================
-- Table: accounts
-- ============================================
CREATE TABLE IF NOT EXISTS accounts (
    account_id     INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_name  VARCHAR(100) NOT NULL,
    phone          VARCHAR(15),
    email          VARCHAR(100),
    address        VARCHAR(255),
    account_type   VARCHAR(30),
    balance        DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table: transactions
-- ============================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id   INT PRIMARY KEY AUTO_INCREMENT,
    account_number   VARCHAR(20),
    transaction_type VARCHAR(20),
    amount           DECIMAL(12,2),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance_after    DECIMAL(12,2),
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- ============================================
-- Sample Admin User
-- username: admin | password: admin123
-- ============================================
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;

-- ============================================
-- Sample Accounts (optional seed data)
-- ============================================
INSERT IGNORE INTO accounts (account_number, customer_name, phone, email, address, account_type, balance)
VALUES
  ('ACC1001', 'Alice Johnson', '9876543210', 'alice@example.com', '12 Park Ave, Chennai', 'Savings', 15000.00),
  ('ACC1002', 'Bob Smith',    '9123456780', 'bob@example.com',   '45 MG Road, Chennai',  'Current', 50000.00);

-- Sample transactions for seed accounts
INSERT IGNORE INTO transactions (account_number, transaction_type, amount, balance_after)
VALUES
  ('ACC1001', 'DEPOSIT',    15000.00, 15000.00),
  ('ACC1002', 'DEPOSIT',    50000.00, 50000.00);
