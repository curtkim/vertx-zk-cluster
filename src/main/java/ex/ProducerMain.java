package ex;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerMain {
  public static void main(String[] argv){

    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    //If the request fails, the producer can automatically retry,
    props.put("retries", 0);
    //Reduce the no of requests less than 0
    props.put("linger.ms", 1);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<>(props);

    for(int i = 0; i < 10; i++)
      producer.send(new ProducerRecord<>("driver.push", "1", Integer.toString(i)));
    producer.close();
  }
}
