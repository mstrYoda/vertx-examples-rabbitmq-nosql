package com.yoda;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    Router router = Router.router(vertx);

    EventBus bus = vertx.eventBus();

    router.route().handler(BodyHandler.create());

    router.get("/products/:id").handler(ctx -> {
      bus.send("getProduct", ctx.request().getParam("id"), reply -> {
        if (reply.succeeded()) {
          ctx.response().end(Json.encodePrettily(reply.result().body()));
        }
      });
    }).failureHandler(ctx -> ctx.response().end(ctx.failure().getCause().getMessage()));

    router.put("/products/:id").handler(ProductController::handleAddProduct);

    vertx.createHttpServer().requestHandler(router).listen(8888);
  }
}
