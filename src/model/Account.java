package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Account - Represents a bank account record.
 */
public class Account {

    private int       accountId;
    private String    accountNumber;
    private String    customerName;
    private String    phone;
    private String    email;
    private String    address;
    private String    accountType;
    private BigDecimal balance;
    private Timestamp createdAt;

    public Account() {}

    public Account(String accountNumber, String customerName, String phone,
                   String email, String address, String accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerName  = customerName;
        this.phone         = phone;
        this.email         = email;
        this.address       = address;
        this.accountType   = accountType;
        this.balance       = balance;
    }

    // ─── Getters & Setters ───────────────────────────────────────────

    public int getAccountId()                     { return accountId; }
    public void setAccountId(int accountId)       { this.accountId = accountId; }

    public String getAccountNumber()              { return accountNumber; }
    public void setAccountNumber(String n)        { this.accountNumber = n; }

    public String getCustomerName()               { return customerName; }
    public void setCustomerName(String n)         { this.customerName = n; }

    public String getPhone()                      { return phone; }
    public void setPhone(String phone)            { this.phone = phone; }

    public String getEmail()                      { return email; }
    public void setEmail(String email)            { this.email = email; }

    public String getAddress()                    { return address; }
    public void setAddress(String address)        { this.address = address; }

    public String getAccountType()                { return accountType; }
    public void setAccountType(String t)          { this.accountType = t; }

    public BigDecimal getBalance()                { return balance; }
    public void setBalance(BigDecimal balance)    { this.balance = balance; }

    public Timestamp getCreatedAt()               { return createdAt; }
    public void setCreatedAt(Timestamp t)         { this.createdAt = t; }

    @Override
    public String toString() {
        return accountNumber + " - " + customerName;
    }
}
