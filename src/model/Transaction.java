package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Transaction - Represents a single banking transaction record.
 */
public class Transaction {

    private int        transactionId;
    private String     accountNumber;
    private String     transactionType;
    private BigDecimal amount;
    private Timestamp  transactionDate;
    private BigDecimal balanceAfter;

    public Transaction() {}

    public Transaction(String accountNumber, String transactionType,
                       BigDecimal amount, BigDecimal balanceAfter) {
        this.accountNumber   = accountNumber;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.balanceAfter    = balanceAfter;
    }

    // ─── Getters & Setters ───────────────────────────────────────────

    public int getTransactionId()                    { return transactionId; }
    public void setTransactionId(int id)             { this.transactionId = id; }

    public String getAccountNumber()                 { return accountNumber; }
    public void setAccountNumber(String n)           { this.accountNumber = n; }

    public String getTransactionType()               { return transactionType; }
    public void setTransactionType(String t)         { this.transactionType = t; }

    public BigDecimal getAmount()                    { return amount; }
    public void setAmount(BigDecimal amount)         { this.amount = amount; }

    public Timestamp getTransactionDate()            { return transactionDate; }
    public void setTransactionDate(Timestamp t)      { this.transactionDate = t; }

    public BigDecimal getBalanceAfter()              { return balanceAfter; }
    public void setBalanceAfter(BigDecimal b)        { this.balanceAfter = b; }
}
