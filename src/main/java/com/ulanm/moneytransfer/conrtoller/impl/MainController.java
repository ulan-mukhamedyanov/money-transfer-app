package com.ulanm.moneytransfer.conrtoller.impl;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ulanm.moneytransfer.Application;
import com.ulanm.moneytransfer.conrtoller.Controller;
import com.ulanm.moneytransfer.serializer.BigDecimalSerializer;
import com.ulanm.moneytransfer.serializer.CurrencySerializer;
import com.ulanm.moneytransfer.serializer.DateTimeSerializer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.math.BigDecimal;
import java.time.*;
import java.util.Currency;

public class MainController implements Controller {

    private Vertx vertx;

    public MainController(Vertx vertx) {
        this.vertx = vertx;

        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new DateTimeSerializer());
        module.addSerializer(LocalDate.class, new DateTimeSerializer());
        module.addSerializer(LocalTime.class, new DateTimeSerializer());
        module.addSerializer(OffsetDateTime.class, new DateTimeSerializer());
        module.addSerializer(OffsetTime.class, new DateTimeSerializer());
        module.addSerializer(ZonedDateTime.class, new DateTimeSerializer());
        module.addSerializer(Currency.class, new CurrencySerializer());
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        Json.mapper.registerModule(module);
        Json.prettyMapper.registerModule(module);
    }

    @Override
    public Router initAndGetRouter() {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Money Transfer App</h1>");
        });

        Controller accountController = new AccountController(vertx);
        router.mountSubRouter("/account", accountController.initAndGetRouter());

        Controller transactionController = new TransactionController(vertx);
        router.mountSubRouter("/transaction", transactionController.initAndGetRouter());

        Controller userController = new UserController(vertx);
        router.mountSubRouter("/user", userController.initAndGetRouter());

        router.post("/test").handler(this::test);

        return router;
    }

    private void test(RoutingContext context) {
        Application.dao.generateTestData();
        context.response()
                .setStatusCode(201)
                .end();
    }

}
