package com.ulanm.moneytransfer.conrtoller.impl;

import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.Account;
import com.ulanm.moneytransfer.model.impl.AccountDTO;
import com.ulanm.moneytransfer.model.impl.User;
import com.ulanm.moneytransfer.model.impl.UserDTO;
import com.ulanm.moneytransfer.conrtoller.Controller;
import com.ulanm.moneytransfer.service.UserService;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class UserController implements Controller {

    private Vertx vertx;

    public UserController(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Router initAndGetRouter() {

        Router router = Router.router(vertx);

        router.post("/create").handler(this::createUser);
        router.post("/create-account").handler(this::createAccount);
        router.get("/all").handler(this::getAllUsers);
        router.get("/find").handler(this::findUser);
        router.get("/find/:name").handler(this::findUser);
        router.get("/info").handler(this::getUser);
        router.get("/info/:id").handler(this::getUser);
        router.get("/accounts").handler(this::getAccounts);
        router.get("/accounts/:id").handler(this::getAccounts);
        router.put("/edit").handler(this::updateUser);
        router.put("/edit/:id").handler(this::updateUser);
        router.delete("/delete").handler(this::deleteUser);
        router.delete("/delete/:id").handler(this::deleteUser);

        return router;

    }

    private void createUser(RoutingContext context) {
        final UserDTO userDTO;
        try {
            userDTO = Json.decodeValue(context.getBodyAsString(), UserDTO.class);
            User user = UserService.createUser(userDTO);
            context.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(user));
        }
        catch (DecodeException e) {
            context.response()
                    .setStatusCode(400)
                    .setStatusMessage("Wrong JSON format.")
                    .end();
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void getAllUsers(RoutingContext context) {
        try {
            List<User> users = UserService.getAllUsers();
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(users));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void getUser(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            User user = UserService.getUserById(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(user));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void updateUser(RoutingContext context) {
        final String id = context.request().getParam("id");
        final UserDTO userDTO;
        try {
            userDTO = Json.decodeValue(context.getBodyAsString(), UserDTO.class);
            User user = UserService.updateUser(id, userDTO);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(user));
        }
        catch (DecodeException e) {
            context.response()
                    .setStatusCode(400)
                    .setStatusMessage("Wrong JSON format.")
                    .end();
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void deleteUser(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            UserService.deleteUser(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end();
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void getAccounts(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            List<Account> accounts = UserService.getAccounts(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(accounts));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void createAccount(RoutingContext context) {
        final AccountDTO accountDTO;
        try {
            accountDTO = Json.decodeValue(context.getBodyAsString(), AccountDTO.class);
            Account account = UserService.createAccount(accountDTO);
            context.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(account));
        }
        catch (DecodeException e) {
            context.response()
                    .setStatusCode(400)
                    .setStatusMessage("Wrong JSON format.")
                    .end();
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void findUser(RoutingContext context) {
        final String name = context.request().getParam("name");
        try {
            List<User> users = UserService.findUsersByName(name);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(users));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

}
