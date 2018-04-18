package ex;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main1 {
  public static void main(String[] args){
    Vertx vertx = Vertx.vertx();
    DeploymentOptions deployOption = new DeploymentOptions().setInstances(1);
    vertx.deployVerticle("ex.Connection", deployOption);
  }
}
