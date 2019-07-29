package com.ulanm.moneytransfer.dao.impl;

import com.ulanm.moneytransfer.dao.AccountDAO;
import com.ulanm.moneytransfer.dao.BundleDAO;
import com.ulanm.moneytransfer.dao.TransactionDAO;
import com.ulanm.moneytransfer.dao.UserDAO;
import com.ulanm.moneytransfer.model.impl.Account;
import com.ulanm.moneytransfer.model.impl.Transaction;
import com.ulanm.moneytransfer.model.impl.User;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryBundleDAO implements BundleDAO {

    private final UserDAO userDAO = new UserInMemoryDAO();
    private final AccountDAO accountDAO = new AccountInMemoryDAO();
    private final TransactionDAO transactionDAO = new TransactionInMemoryDAO();

    private static DecimalFormat format = new DecimalFormat("#.00");
    private static List<String> testUserIds = Collections.synchronizedList(new ArrayList<>());
    private static List<String> testAccountIds = Collections.synchronizedList(new ArrayList<>());
    private static List<String> testTransactionIds = Collections.synchronizedList(new ArrayList<>());

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    @Override
    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    @Override
    public void generateTestData() {
        generateTestUsers();
        generateTestAccounts();
        generateTestTransactions();
    }

    private void generateTestUsers() {
        final String[] names = {
                "test_user_1",
                "test_user_2",
                "test_user_3",
                "test_user_update",
                "test_user_delete",
                "test_user_info",
                "John Doe",
                "Kate Willson"
        };
        for (String id : testUserIds) {
            if (userDAO.getById(id) != null)
                userDAO.delete(userDAO.getById(id));
        }
        testUserIds.clear();
        for (String name : names) {
            User user = userDAO.create();
            synchronized (user) {
                user.setName(name);
                testUserIds.add(user.getId());
                userDAO.submit(user);
            }
        }
    }

    private void generateTestAccounts() {
        Random random = new Random();
        for (String id : testAccountIds) {
            if (accountDAO.getById(id) != null)
                accountDAO.delete(accountDAO.getById(id));
        }
        testAccountIds.clear();
        List<User> users = userDAO.getAll();
        for (User user : users) {
            final int accounts = random.nextInt(3) + 1;
            for (int i = 0; i < accounts; i++) {
                Account newAccount = accountDAO.create();
                String amount = format.format(random.nextDouble() * 10000);
                synchronized (newAccount) {
                    newAccount.setOwner(user);
                    newAccount.setName("account_" + i);
                    newAccount.setCurrency(Currency.getInstance("USD"));
                    newAccount.setBalance(new BigDecimal(amount));
                    newAccount.setActive(true);
                    accountDAO.submit(newAccount);
                    testAccountIds.add(newAccount.getId());
                }
            }
        }
    }

    private void generateTestTransactions() {
        Random random = new Random();
        for (String id : testTransactionIds) {
            if (transactionDAO.getById(id) != null)
                transactionDAO.delete(transactionDAO.getById(id));
        }
        testTransactionIds.clear();
        List<Account> accounts = accountDAO.getAll();
        for (Account account : accounts) {
            int transactions = random.nextInt(10) + 1;
            for (int i = 0; i < transactions; i++) {
                Transaction transaction = transactionDAO.create();
                Account to;
                do {
                    to = accounts.get(random.nextInt(accounts.size()));
                } while (to.getId().equals(account.getId()));
                Transaction.TransactionStatus status = random.nextDouble() > 0.3 ?
                        Transaction.TransactionStatus.EXECUTED :
                        Transaction.TransactionStatus.FAILED;
                String amount = format.format(random.nextDouble() * 10000);
                synchronized (transaction) {
                    transaction.setSourceAccount(account);
                    transaction.setDestinationAccount(to);
                    transaction.setStatus(status);
                    transaction.setExecutionDateTime(LocalDateTime.now());
                    transaction.setComment("money transfer #" + i);
                    transaction.setCurrency(Currency.getInstance("USD"));
                    transaction.setAmount(new BigDecimal(amount));
                    transactionDAO.submit(transaction);
                    testTransactionIds.add(transaction.getId());
                }
            }
        }
    }

}
