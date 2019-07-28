package com.ulanm.moneytransfer.service;

import com.ulanm.moneytransfer.Application;
import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class AccountService {

    public static Account getAccountById(String id) throws ServiceException {
        if (id == null || id.trim().equals(""))
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Account ID cannot be empty.");
        Account account = Application.dao.getAccountDAO().getById(id);
        if (account != null) {
            synchronized (account) {
                return account.clone();
            }
        }
        else
            throw new ServiceException()
                    .withStatusCode(404)
                    .withStatusMessage("No account found with ID: " + id);
    }

    public static Account activateAccount(Account account) throws ServiceException {
        boolean success;
        Account result;
        synchronized (account) {
            account.setActive(true);
            success = Application.dao.getAccountDAO().submit(account);
            result = account.clone();
        }
        if (success)
            return result;
        else
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");
    }

    public static Account deactivateAccount(Account account) throws ServiceException {
        boolean success;
        Account result;
        synchronized (account) {
            account.setActive(false);
            success = Application.dao.getAccountDAO().submit(account);
            result = account.clone();
        }
        if (success)
            return result;
        else
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");
    }

    public static Account activateAccount(String id) throws ServiceException {
        return activateAccount(getAccountById(id));
    }

    public static Account deactivateAccount(String id) throws ServiceException {
        return deactivateAccount(getAccountById(id));
    }

    public static Account updateAccount(Account account, AccountDTO newData) throws ServiceException {

        String name;
        User owner;
        BigDecimal balance;
        Currency currency;
        boolean active;

        if (newData.getName() != null && !newData.getName().trim().equals("")) {
            name = newData.getName();
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Name cannot be empty or consist only of whitespaces.");

        if (newData.getOwnerId() != null && !newData.getOwnerId().trim().equals("")) {
            owner = UserService.getUserById(newData.getOwnerId());
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Owner ID cannot be empty or consist only of whitespaces.");

        if (newData.getBalance() != null && !newData.getBalance().trim().equals("")) {
            try {
                balance = new BigDecimal(newData.getBalance());
            }
            catch (NumberFormatException e) {
                throw new ServiceException()
                        .withStatusCode(400)
                        .withStatusMessage("Wrong balance format.");
            }
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Balance cannot be empty or consist only of whitespaces.");

        if (newData.getCurrency() != null && !newData.getCurrency().trim().equals("")) {
            try {
                currency = Currency.getInstance(newData.getCurrency());
            }
            catch (IllegalArgumentException e) {
                throw new ServiceException()
                        .withStatusCode(400)
                        .withStatusMessage("Wrong currency code.");
            }
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Currency cannot be empty or consist only of whitespaces.");

        if (newData.getActive() != null) {
            active = newData.getActive();
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Active flag cannot be null.");

        boolean success;
        Account result;
        synchronized (account) {
            account.setOwner(owner);
            account.setName(name);
            account.setCurrency(currency);
            account.setBalance(balance);
            account.setActive(active);
            success = Application.dao.getAccountDAO().submit(account);
            result = account.clone();
        }
        if (success)
            return result;
        else
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");

    }

    public static Account updateAccount(String id, AccountDTO newData) throws ServiceException {
        return updateAccount(getAccountById(id), newData);
    }

    public static void deleteAccount(Account account) throws ServiceException {
        boolean success;
        synchronized (account) {
            success = Application.dao.getAccountDAO().delete(account);
        }
        if (!success)
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");
    }

    public static void deleteAccount(String id) throws ServiceException {
        deleteAccount(getAccountById(id));
    }

    public static List<Account> activateAccounts(List<Account> accounts) throws ServiceException {
        for (Account account : accounts)
            activateAccount(account);
        return accounts;
    }

    public static List<Account> deactivateAccounts(List<Account> accounts) throws ServiceException {
        for (Account account : accounts)
            deactivateAccount(account);
        return accounts;
    }

    public static Transaction withdraw(DepositWithdrawDTO data) throws ServiceException {

        Account target = getAccountById(data.getTargetAccountId());
        TransactionDTO transaction = new TransactionDTO();
        transaction.setSourceAccountId(target.getId());
        transaction.setDestinationAccountId(Account.EXTERNAL.getId());
        transaction.setAmount(data.getAmount());
        transaction.setCurrency(target.getCurrency().getCurrencyCode());
        transaction.setComment("Withdrawal");
        return transfer(transaction);

    }

    public static Transaction deposit(DepositWithdrawDTO data) throws ServiceException {

        Account target = getAccountById(data.getTargetAccountId());
        TransactionDTO transaction = new TransactionDTO();
        transaction.setSourceAccountId(Account.EXTERNAL.getId());
        transaction.setDestinationAccountId(target.getId());
        transaction.setAmount(data.getAmount());
        transaction.setCurrency(target.getCurrency().getCurrencyCode());
        transaction.setComment("Deposit");
        return transfer(transaction);

    }

    public static Transaction transfer(TransactionDTO data) throws ServiceException {

        Account source;
        Account destination;

        if (data.getSourceAccountId().equals(Account.EXTERNAL.getId()))
            source = Account.EXTERNAL;
        else
            source = getAccountById(data.getSourceAccountId());

        if (data.getDestinationAccountId().equals(Account.EXTERNAL.getId()))
            destination = Account.EXTERNAL;
        else
            destination = getAccountById(data.getDestinationAccountId());

        BigDecimal amount;
        Currency currency;

        if (data.getAmount() != null && !data.getAmount().trim().equals("")) {
            try {
                amount = new BigDecimal(data.getAmount());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException()
                            .withStatusCode(403)
                            .withStatusMessage("Amount cannot be less or equal to 0.");
                }
            }
            catch (NumberFormatException e) {
                throw new ServiceException()
                        .withStatusCode(400)
                        .withStatusMessage("Wrong amount format.");
            }
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Amount cannot be empty or consist only of whitespaces.");

        if (data.getCurrency() != null && !data.getCurrency().trim().equals("")) {
            try {
                currency = Currency.getInstance(data.getCurrency());
            }
            catch (IllegalArgumentException e) {
                throw new ServiceException()
                        .withStatusCode(400)
                        .withStatusMessage("Wrong currency code.");
            }
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Currency cannot be empty or consist only of whitespaces.");


        Transaction transaction = Application.dao.getTransactionDAO().create();
        Transaction result;
        synchronized (transaction) {
            transaction.setSourceAccount(source);
            transaction.setDestinationAccount(destination);
            transaction.setCurrency(currency);
            transaction.setComment(data.getComment());
            transaction.setAmount(amount);
            TransferService.execute(transaction);
            result = transaction.clone();
        }

        return result;
    }

    public static List<Transaction> getTransactions(Account account) throws ServiceException {
        final String id;
        synchronized (account) {
            id = account.getId();
        }
        List<Transaction> transactions = new ArrayList<>();
        Application.dao.getTransactionDAO().getAll()
                .forEach(transaction -> {
                    synchronized (transaction) {
                        if (
                                transaction.getSourceAccount().getId().equals(id) ||
                                transaction.getDestinationAccount().getId().equals(id)
                        ) {
                            transactions.add(transaction.clone());
                        }
                    }
                });
        return transactions
                .stream()
                .sorted(Comparator.comparing(Transaction::getCreationDateTime))
                .collect(Collectors.toList());
    }

    public static List<Transaction> getTransactions(String id) throws ServiceException {
        return getTransactions(getAccountById(id));
    }

}
