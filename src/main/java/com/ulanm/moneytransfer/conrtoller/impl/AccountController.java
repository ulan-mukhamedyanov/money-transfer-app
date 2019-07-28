package com.ulanm.moneytransfer.conrtoller.impl;

import com.ulanm.moneytransfer.conrtoller.Controller;
import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.*;
import com.ulanm.moneytransfer.service.AccountService;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class AccountController implements Controller {

    private Vertx vertx;

    public AccountController(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Router initAndGetRouter() {

        Router router = Router.router(vertx);

        router.get("/info").handler(this::getAccount);
        router.get("/info/:id").handler(this::getAccount);
        router.get("/transactions").handler(this::getTransactions);
        router.get("/transactions/:id").handler(this::getTransactions);
        router.put("/edit").handler(this::updateAccount);
        router.put("/edit/:id").handler(this::updateAccount);
        router.put("/activate").handler(this::activate);
        router.put("/activate/:id").handler(this::activate);
        router.put("/deactivate").handler(this::deactivate);
        router.put("/deactivate/:id").handler(this::deactivate);
        router.delete("/delete").handler(this::deleteAccount);
        router.delete("/delete/:id").handler(this::deleteAccount);
        router.post("/deposit").handler(this::deposit);
        router.post("/withdraw").handler(this::withdraw);
        router.post("/transfer").handler(this::transfer);

        return router;
    }

    private void getAccount(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            Account account = AccountService.getAccountById(id);
            final String content;
            synchronized (account) {
                content = Json.encodePrettily(account);
            }
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(content);
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void updateAccount(RoutingContext context) {
        final String id = context.request().getParam("id");
        final AccountDTO accountDTO;
        try {
            accountDTO = Json.decodeValue(context.getBodyAsString(), AccountDTO.class);
            Account account = AccountService.updateAccount(id, accountDTO);
            final String content;
            synchronized (account) {
                content = Json.encodePrettily(account);
            }
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(content);
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

    private void deleteAccount(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            AccountService.deleteAccount(id);
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

    private void deposit(RoutingContext context) {
        final DepositWithdrawDTO data;
        try {
            data = Json.decodeValue(context.getBodyAsString(), DepositWithdrawDTO.class);
            Transaction transaction = AccountService.deposit(data);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transaction));
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

    private void withdraw(RoutingContext context) {
        final DepositWithdrawDTO data;
        try {
            data = Json.decodeValue(context.getBodyAsString(), DepositWithdrawDTO.class);
            Transaction transaction = AccountService.withdraw(data);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transaction));
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

    private void getTransactions(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            List<Transaction> transactions = AccountService.getTransactions(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transactions));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void transfer(RoutingContext context) {
        final TransactionDTO data;
        try {
            data = Json.decodeValue(context.getBodyAsString(), TransactionDTO.class);
            Transaction transaction = AccountService.transfer(data);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transaction));
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

    private void activate(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            Account account = AccountService.activateAccount(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(account));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

    private void deactivate(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            Account account = AccountService.deactivateAccount(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(account));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

}
