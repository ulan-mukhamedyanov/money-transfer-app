package com.ulanm.moneytransfer;

import com.ulanm.moneytransfer.dao.BundleDAO;
import com.ulanm.moneytransfer.dao.impl.InMemoryBundleDAO;
import com.ulanm.moneytransfer.conrtoller.Controller;
import com.ulanm.moneytransfer.conrtoller.impl.MainController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.ext.web.Router;

public class Application extends AbstractVerticle {

    public static final int PORT_NUMBER = 8080;

    public static final BundleDAO dao = new InMemoryBundleDAO();

    public static void main(String[] args) {
        Launcher.executeCommand("run", Application.class.getName());
    }

    @Override
    public void start(Future<Void> future) {

        Controller controller = new MainController(vertx);
        Router router = controller.initAndGetRouter();

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        PORT_NUMBER,
                        result -> {
                            if (result.succeeded())
                                future.complete();
                            else
                                future.fail(result.cause());
                        }
                );

    }

}
