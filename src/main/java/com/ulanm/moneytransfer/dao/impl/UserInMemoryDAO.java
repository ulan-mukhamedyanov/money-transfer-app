package com.ulanm.moneytransfer.dao.impl;

import com.ulanm.moneytransfer.dao.UserDAO;
import com.ulanm.moneytransfer.model.impl.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserInMemoryDAO implements UserDAO {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public List<User> getAll() {
        return Collections.synchronizedList(new ArrayList<>(users.values()));
    }

    @Override
    public User getById(String id) {
        return users.get(id);
    }

    @Override
    public boolean submit(User entity) {
        synchronized (users) {
            if (users.get(entity.getId()) == entity)
                return true;
            if (users.get(entity.getId()) == null)
                return false;
            users.put(entity.getId(), entity);
            return users.get(entity.getId()) == entity;
        }
    }

    @Override
    public boolean delete(User entity) {
        synchronized (users) {
            users.remove(entity.getId());
            return users.get(entity.getId()) == null;
        }
    }

    @Override
    public User create() {
        User user;
        String id;
        synchronized (users) {
            do id = UUID.randomUUID().toString();
            while (users.get(id) != null);
            user = new User(id);
            users.put(id, user);
        }
        return user;
    }

}
