package com.ulanm.moneytransfer.model.impl;

import com.ulanm.moneytransfer.model.GenericModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public class Account implements GenericModel {

    public static final Account EXTERNAL = new Account("00000000-0000-0000-0000-000000000000");

    static {
        EXTERNAL.setActive(true);
        EXTERNAL.setName("Account for deposit and withdrawal");
    }

    private final String id;

    private User owner;

    private String name;

    private LocalDateTime creationDateTime;

    private BigDecimal balance;

    private Currency currency;

    private boolean active;

    public Account() {
        this(UUID.randomUUID().toString());
    }

    public Account(String id) {
        this.id = id;
        creationDateTime = LocalDateTime.now();
        active = false;
    }

    @Override
    public String getId() {
        return id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(this.id, account.id) &&
                Objects.equals(this.owner, account.owner) &&
                Objects.equals(this.name, account.name) &&
                Objects.equals(this.creationDateTime, account.creationDateTime) &&
                Objects.equals(this.currency, account.currency) &&
                this.active == account.active;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss d-MMMM-yyyy");
        return "Account {\n" +
                "\tID: " + id + ",\n" +
                "\tName: " + name + ",\n" +
                "\tOwned by: " + owner.getName() + ",\n" +
                "\tCurrent balance: " + balance.toPlainString() + " " + currency.getCurrencyCode() + ",\n" +
                "\tCreated at: " + creationDateTime.format(formatter) + "\n" +
                "}\n";
    }

    @Override
    public Account clone() {
        try {super.clone();}
        catch (CloneNotSupportedException ignored) {}
        Account clone = new Account(this.id);
        clone.owner = this.owner;
        clone.name = this.name;
        clone.creationDateTime = this.creationDateTime;
        clone.balance = this.balance;
        clone.currency = this.currency;
        clone.active = this.active;
        return clone;
    }

}
