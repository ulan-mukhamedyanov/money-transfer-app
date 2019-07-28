package com.ulanm.moneytransfer.service;

import com.ulanm.moneytransfer.Application;
import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    public static User getUserById(String id) throws ServiceException {

        if (id == null || id.trim().equals(""))
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("User ID cannot be empty.");
        User user = Application.dao.getUserDAO().getById(id);
        if (user != null) {
            synchronized (user) {
                return user.clone();
            }
        }
        else
            throw new ServiceException()
                    .withStatusCode(404)
                    .withStatusMessage("No user found with ID: " + id);

    }

    public static List<User> getAllUsers() throws ServiceException {
        List<User> users = new ArrayList<>();
        Application.dao.getUserDAO().getAll()
                .forEach(user -> {
                    synchronized (user) {
                        users.add(user.clone());
                    }
                });
        return users;
    }

    public static List<User> findUsersByName(String name) throws ServiceException {
        if (name != null && !name.trim().equals(""))
            return getAllUsers()
                    .parallelStream()
                    .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Name cannot be empty or consist only of whitespaces.");
    }

    public static List<Account> getAccounts(User owner) throws ServiceException {
        final String id;
        synchronized (owner) {
            id = owner.getId();
        }
        List<Account> accounts = new ArrayList<>();
        Application.dao.getAccountDAO().getAll()
                .forEach(account -> {
                    synchronized (account) {
                        if (account.getOwner().getId().equals(id)) {
                            accounts.add(account.clone());
                        }
                    }
                });
        return accounts;
    }

    public static List<Account> getAccounts(String id) throws ServiceException {
        return getAccounts(getUserById(id));
    }

    public static Account createAccount(AccountDTO data) throws ServiceException {

        String name;
        User owner;
        BigDecimal balance;
        Currency currency;
        boolean active;

        if (data.getName() != null && !data.getName().trim().equals("")) {
            name = data.getName();
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Name cannot be empty or consist only of whitespaces.");

        if (data.getOwnerId() != null && !data.getOwnerId().trim().equals("")) {
            owner = UserService.getUserById(data.getOwnerId());
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Owner ID cannot be empty or consist only of whitespaces.");

        if (data.getBalance() != null && !data.getBalance().trim().equals("")) {
            try {
                balance = new BigDecimal(data.getBalance());
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

        if (data.getActive() != null) {
            active = data.getActive();
        } else
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Active flag cannot be null.");

        Account account = Application.dao.getAccountDAO().create();
        Account result;
        boolean success;
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

    public static User updateUser(User user, UserDTO newData) throws ServiceException {

        if (newData.getName() == null || newData.getName().trim().equals("")) {
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Name cannot be empty or consist only of whitespaces.");
        }

        User result;
        boolean success;
        synchronized (user) {
            user.setName(newData.getName());
            success = Application.dao.getUserDAO().submit(user);
            result = user.clone();
        }

        if (success)
            return result;
        else
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");
    }

    public static User updateUser(String id, UserDTO newData) throws ServiceException {
        return updateUser(getUserById(id), newData);
    }

    public static User createUser(UserDTO data) throws ServiceException {
        if (data.getName() == null || data.getName().trim().equals(""))
            throw new ServiceException()
                    .withStatusCode(400)
                    .withStatusMessage("Name cannot be empty or consist only of whitespaces.");

        User user = Application.dao.getUserDAO().create();
        User result;
        boolean success;
        synchronized (user) {
            user.setName(data.getName());
            success = Application.dao.getUserDAO().submit(user);
            result = user.clone();
        }

        if (success)
            return result;
        else
            throw new ServiceException()
                    .withStatusCode(500)
                    .withStatusMessage("An error occurred, please try again.");

    }

    public static void deleteUser(User user) throws ServiceException {
        List<Account> accounts = getAccounts(user);
        boolean success;
        synchronized (user) {
            success = Application.dao.getUserDAO().delete(user);
        }
        if (success)
            AccountService.deactivateAccounts(accounts);
        else
            throw new ServiceException()
                .withStatusCode(500)
                .withStatusMessage("An error occurred, please try again.");
    }

    public static void deleteUser(String id) throws ServiceException {
        deleteUser(getUserById(id));
    }

}
