package com.ulanm.moneytransfer.service;

import com.ulanm.moneytransfer.Application;
import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.Account;
import com.ulanm.moneytransfer.model.impl.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class TransferService {

    public static Transaction getTransactionById(String id) throws ServiceException {

        if (id == null || id.trim().equals(""))
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Transaction ID cannot be empty.");
        Transaction transaction = Application.dao.getTransactionDAO().getById(id);
        if (transaction != null) {
            synchronized (transaction) {
                return transaction.clone();
            }
        }
        else
            throw new ServiceException()
                    .withStatusCode(404)
                    .withStatusMessage("No transaction found with ID: " + id);

    }

    public static Transaction execute(Transaction transaction) throws ServiceException {

        Currency currency = transaction.getCurrency();
        Account source = transaction.getSourceAccount();
        Account destination = transaction.getDestinationAccount();
        BigDecimal amount = transaction.getAmount();

        if (source.equals(destination))
            throw new ServiceException()
                    .withStatusCode(403)
                    .withStatusMessage("Impossible to transfer to the same account.");

        if (!source.isActive() || !destination.isActive())
            throw new ServiceException()
                    .withStatusCode(403)
                    .withStatusMessage("Account is not active.");

        boolean sourceExternal = source.getId().equals(Account.EXTERNAL.getId());
        boolean destinationExternal = destination.getId().equals(Account.EXTERNAL.getId());

        List<Account> tempList = Arrays.stream(new Account[] {source, destination})
                .sorted(Comparator.comparing(Account::getId))
                .collect(Collectors.toList());
        Account first = tempList.get(0);
        Account second = tempList.get(1);

        boolean success;

        synchronized (first) {
            synchronized (second) {
                if (!sourceExternal) {
                    if (!source.getCurrency().equals(currency))
                        throw new ServiceException()
                                .withStatusCode(400)
                                .withStatusMessage("Currencies do not match. Auto-conversion is not available.");
                    if (source.getBalance().compareTo(amount) < 0) {
                        throw new ServiceException()
                                .withStatusCode(403)
                                .withStatusMessage("Insufficient funds.");
                    }
                }
                if (!destinationExternal) {
                    if (!destination.getCurrency().equals(currency))
                        throw new ServiceException()
                                .withStatusCode(400)
                                .withStatusMessage("Currencies do not match. Auto-conversion is not available.");
                }

                success = true;
                if (!sourceExternal) {
                    source.setBalance(source.getBalance().subtract(amount));
                    success = Application.dao.getAccountDAO().submit(source);
                }
                if (!destinationExternal) {
                    destination.setBalance(destination.getBalance().add(amount));
                    success = success && Application.dao.getAccountDAO().submit(destination);
                }

            }
        }

        Transaction result;
        synchronized (transaction) {
            if (success) {
                transaction.setStatus(Transaction.TransactionStatus.EXECUTED);
                transaction.setExecutionDateTime(LocalDateTime.now());
            }
            else {
                transaction.setStatus(Transaction.TransactionStatus.FAILED);
            }
            Application.dao.getTransactionDAO().submit(transaction);
            result = transaction.clone();
        }

        return result;

    }

}
