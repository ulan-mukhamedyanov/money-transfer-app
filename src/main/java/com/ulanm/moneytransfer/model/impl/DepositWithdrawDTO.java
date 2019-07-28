package com.ulanm.moneytransfer.model.impl;

public class DepositWithdrawDTO {

    private String targetAccountId;

    private String amount;


    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
