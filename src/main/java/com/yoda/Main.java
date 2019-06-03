package com.yoda;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;

public class Main {
  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    //Launcher.executeCommand("run", MainVerticle.class.getName());
    DeploymentOptions options = new DeploymentOptions().setInstances(16);
    vertx.deployVerticle("com.yoda.MainVerticle");
    vertx.deployVerticle("com.yoda.ProductController");
  }
}
