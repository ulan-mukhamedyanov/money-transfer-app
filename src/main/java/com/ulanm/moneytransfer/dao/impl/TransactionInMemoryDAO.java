package com.ulanm.moneytransfer.dao.impl;

import com.ulanm.moneytransfer.dao.TransactionDAO;
import com.ulanm.moneytransfer.model.impl.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionInMemoryDAO implements TransactionDAO {

    private static final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    @Override
    public List<Transaction> getAll() {
        return Collections.synchronizedList(new ArrayList<>(transactions.values()));
    }

    @Override
    public Transaction getById(String id) {
        return transactions.get(id);
    }

    @Override
    public boolean submit(Transaction entity) {
        synchronized (transactions) {
            if (transactions.get(entity.getId()) == entity)
                return true;
            if (transactions.get(entity.getId()) == null)
                return false;
            transactions.put(entity.getId(), entity);
            return transactions.get(entity.getId()) == entity;
        }
    }

    @Override
    public boolean delete(Transaction entity) {
        synchronized (transactions) {
            transactions.remove(entity.getId());
            return transactions.get(entity.getId()) == null;
        }
    }

    @Override
    public Transaction create() {
        Transaction transaction;
        String id;
        synchronized (transactions) {
            do id = UUID.randomUUID().toString();
            while (transactions.get(id) != null);
            transaction = new Transaction(id);
            transactions.put(id, transaction);
        }
        return transaction;
    }

}
