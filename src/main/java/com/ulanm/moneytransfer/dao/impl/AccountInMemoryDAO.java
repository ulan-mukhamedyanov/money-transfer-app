package com.ulanm.moneytransfer.dao.impl;

import com.ulanm.moneytransfer.dao.AccountDAO;
import com.ulanm.moneytransfer.model.impl.Account;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountInMemoryDAO implements AccountDAO {

    private static final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public List<Account> getAll() {
        return Collections.synchronizedList(new ArrayList<>(accounts.values()));
    }

    @Override
    public Account getById(String id) {
        return accounts.get(id);
    }

    @Override
    public boolean submit(Account entity) {
        synchronized (accounts) {
            if (accounts.get(entity.getId()) == entity)
                return true;
            if (accounts.get(entity.getId()) == null)
                return false;
            accounts.put(entity.getId(), entity);
            return accounts.get(entity.getId()) == entity;
        }
    }

    @Override
    public boolean delete(Account entity) {
        synchronized (accounts) {
            accounts.remove(entity.getId());
            return accounts.get(entity.getId()) == null;
        }
    }

    @Override
    public Account create() {
        Account account;
        String id;
        synchronized (accounts) {
            do id = UUID.randomUUID().toString();
            while (accounts.get(id) != null);
            account = new Account(id);
            accounts.put(id, account);
        }
        return account;
    }

}
