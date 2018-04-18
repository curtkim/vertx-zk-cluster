package ex;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

public class Main {
  public static void main(String[] args){
    ClusterManager mgr = new ZookeeperClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        System.out.println("success");
        Vertx vertx = res.result();
        DeploymentOptions deployOption = new DeploymentOptions().setInstances(1);
        //vertx.deployVerticle(ex.Connection.class, deployOption);
        vertx.deployVerticle("ex.Connection", deployOption);
      } else {
        System.out.println("fail");
        // failed!
      }
    });
  }
}
