package ex

import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.NetSocket
import io.vertx.core.parsetools.RecordParser
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import org.apache.kafka.clients.producer.ProducerConfig

class Connection extends AbstractVerticle {

  static String PUSH_TOPIC = "driver.push"
  static String INFO_TOPIC = "driver.info"

  Map<NetSocket, String> socketIdMap = new HashMap<>()
  Map<String, String> idHandlerIdMap = new HashMap<>()

  void connect(String id, NetSocket socket){
    socketIdMap.put(socket, id)
    idHandlerIdMap.put(id, socket.writeHandlerID())
  }

  void start() {

    EventBus eb = vertx.eventBus()

    Properties config = new Properties()
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(ProducerConfig.ACKS_CONFIG, "1")
    KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config, String.class, String.class)


    Map<String, String> config1 = new HashMap<>()
    config1.put("bootstrap.servers", "localhost:9092")
    config1.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    config1.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    config1.put("group.id", "my_group")
    //config1.put("auto.offset.reset", "earliest")
    config1.put("enable.auto.commit", "false")

    // use consumer for interacting with Apache Kafka
    KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config1)
    consumer.handler({record ->
      System.out.println("Processing key=" + record.key() + ",value=" + record.value() +
          ",partition=" + record.partition() + ",offset=" + record.offset())
      if( idHandlerIdMap.containsKey(record.key())) {
        String handlerId = idHandlerIdMap.get(record.key())
        eb.send(handlerId, Buffer.buffer(record.value()))
      }

    })
    consumer.subscribe(PUSH_TOPIC)


    NetServerOptions netOpt = NetServerOptions.newInstance().setTcpKeepAlive(true)
    vertx.createNetServer(netOpt).connectHandler({ NetSocket socket ->
      println "${socket.remoteAddress()} ${socket.writeHandlerID()}"
      RecordParser parser = RecordParser.newDelimited("\r\n", { buffer ->
        println buffer.toString()
        List<String> params = Arrays.asList(buffer.toString().split(" "))
        println params

        switch (params[0].toUpperCase()) {
          case 'CONNECT':
            String id = params[1]
            connect(id, socket)
            break
          case 'PING':
            socket.write('PONG')
            break
          case 'INFO':
            String id = socketIdMap.get(socket)
            //println "$id ${params[1]}"
            producer.write(KafkaProducerRecord.create(INFO_TOPIC, id, params[1]))
            break
        }
      })

      socket.handler(parser)

      socket.closeHandler({v ->
        println 'disconnect'
        //disconnect(socket)
      })
    }).listen(8089, '0.0.0.0')
    println("8089 listen")
  }
}
