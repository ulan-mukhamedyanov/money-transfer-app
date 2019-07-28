package com.ulanm.moneytransfer.conrtoller.impl;

import com.ulanm.moneytransfer.conrtoller.Controller;
import com.ulanm.moneytransfer.exception.ServiceException;
import com.ulanm.moneytransfer.model.impl.Transaction;
import com.ulanm.moneytransfer.service.TransferService;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class TransactionController implements Controller {

    private Vertx vertx;

    public TransactionController(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Router initAndGetRouter() {
        Router router = Router.router(vertx);
        router.get("/info").handler(this::getTransaction);
        router.get("/info/:id").handler(this::getTransaction);
        return router;
    }

    private void getTransaction(RoutingContext context) {
        final String id = context.request().getParam("id");
        try {
            Transaction transaction = TransferService.getTransactionById(id);
            context.response()
                    .setStatusCode(200)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transaction));
        }
        catch (ServiceException e) {
            context.response()
                    .setStatusCode(e.getStatusCode())
                    .setStatusMessage(e.getStatusMessage())
                    .end();
        }
    }

}
