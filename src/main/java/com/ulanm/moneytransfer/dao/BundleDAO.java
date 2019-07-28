package com.ulanm.moneytransfer.dao;

public interface BundleDAO {

    UserDAO getUserDAO();

    AccountDAO getAccountDAO();

    TransactionDAO getTransactionDAO();

    void generateTestData();

}
