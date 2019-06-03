package com.yoda;

import com.rabbitmq.client.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.Arrays;

public class RabbitVerticle extends AbstractVerticle {
  RabbitMQOptions config = new RabbitMQOptions();
  RabbitMQClient client;

  @Override
  public void start() {
    config.setUser("guest");
    config.setPassword("guest");
    config.setVirtualHost("/");
    config.setAddresses(Arrays.asList(Address.parseAddresses("localhost:5672")));
    client = RabbitMQClient.create(vertx, config);

    client.start(result -> {
      System.out.println("start : " + Json.encodePrettily(result.succeeded()));

      client.queueDeclare("vertx-q", true, false, false,
        z -> System.out.println(Json.encodePrettily(z.succeeded())));
    });

    EventBus bus = vertx.eventBus();
    bus.consumer("/publishEvent", this::publish);
  }

  private <T> void publish(Message<T> tMessage) {
    client.basicPublish("", "vertx-q", (JsonObject) tMessage.body(), pubResult -> {
      System.out.println(client.isConnected());
      System.out.println(client.isOpenChannel());

      if (pubResult.succeeded()) {
        System.out.println("Message published !");
        System.out.println(client.isConnected());
        System.out.println(client.isOpenChannel());
      } else {
        pubResult.cause().printStackTrace();
      }
    });
  }
}
