package com.ulanm.moneytransfer.model.impl;

import com.ulanm.moneytransfer.model.GenericModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public class Transaction implements GenericModel {

    private final String id;

    private Account sourceAccount;

    private Account destinationAccount;

    private BigDecimal amount;

    private Currency currency;

    private String comment;

    private TransactionStatus status;

    private LocalDateTime creationDateTime;

    private LocalDateTime executionDateTime;

    public Transaction() {
        this(UUID.randomUUID().toString());
    }

    public Transaction(String id) {
        this.id = id;
        creationDateTime = LocalDateTime.now();
        status = TransactionStatus.CREATED;
    }

    @Override
    public String getId() {
        return id;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public LocalDateTime getExecutionDateTime() {
        return executionDateTime;
    }

    public void setExecutionDateTime(LocalDateTime executionDateTime) {
        this.executionDateTime = executionDateTime;
    }

    public enum TransactionStatus {
        CREATED("created"),
        EXECUTED("executed"),
        FAILED("failed");

        private String status;

        TransactionStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return Objects.equals(this.id, transaction.id) &&
                Objects.equals(this.sourceAccount, transaction.sourceAccount) &&
                Objects.equals(this.destinationAccount, transaction.destinationAccount) &&
                Objects.equals(this.amount, transaction.amount) &&
                Objects.equals(this.currency, transaction.currency) &&
                Objects.equals(this.comment, transaction.comment) &&
                Objects.equals(this.creationDateTime, transaction.creationDateTime) &&
                Objects.equals(this.executionDateTime, transaction.executionDateTime) &&
                Objects.equals(this.status, transaction.status);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss d-MMMM-yyyy");
        return "Transaction {\n" +
                "\tID: " + id + ",\n" +
                "\tFrom: " + sourceAccount.getName() + " of " + sourceAccount.getOwner().getName() + ",\n" +
                "\tTo: " + destinationAccount.getName() + " of " + destinationAccount.getOwner().getName() + ",\n" +
                "\tAmount: " + amount.toPlainString() + " " + currency.getCurrencyCode() + ",\n" +
                "\tComment: " + comment + ",\n" +
                "\tStatus: " + status.toString() + ",\n" +
                "\tCreated at: " + creationDateTime.format(formatter) + "\n" +
                "\tExecuted at: " + (executionDateTime == null ? "never" : creationDateTime.format(formatter)) + "\n" +
                "}\n";
    }

    @Override
    public Transaction clone() {
        try {super.clone();}
        catch (CloneNotSupportedException ignored) {}
        Transaction clone = new Transaction(this.id);
        clone.sourceAccount = this.sourceAccount;
        clone.destinationAccount = this.destinationAccount;
        clone.amount = this.amount;
        clone.currency = this.currency;
        clone.comment = this.comment;
        clone.status = this.status;
        clone.creationDateTime = this.creationDateTime;
        clone.executionDateTime = this.executionDateTime;
        return clone;
    }

}
