package com.yoda;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

public class ProductController extends AbstractVerticle {

  private static HashMap<Integer, String> products = new HashMap();

  public static void handleGetProduct(RoutingContext routingContext) {
    int id = Integer.parseInt(routingContext.request().getParam("id"));

    String product = products.get(id);

    routingContext.response().end(product == null ? "" : product);
  }

  public static void handleAddProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("id");
    HttpServerResponse response = routingContext.response();

    if (productID == null) {
      routingContext.response().setStatusCode(400);
      return;
    }

    JsonObject product = routingContext.getBodyAsJson();
    if (product == null) routingContext.response().setStatusCode(400);

    products.put(product.getInteger("id"), product.getString("name"));
    response.end();
  }

  @Override
  public void start() {
    EventBus bus = vertx.eventBus();

    bus.consumer("getProduct", msg -> {
      int x = Integer.parseInt(msg.body().toString());

      bus.publish("/publishEvent", x);

      msg.reply("test " + x);
    });

  }
}
