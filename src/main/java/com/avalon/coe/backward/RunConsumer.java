package com.avalon.coe.backward;

import com.avalon.coe.Consumer;
import io.confluent.kafka.serializers.KafkaAvroDecoder;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.utils.VerifiableProperties;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by adam on 1/24/17.
 */
public class RunConsumer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RunConsumer.class);

    private final ConsumerConnector consumer;
    private String topic;
    private Map<String, List<KafkaStream<Object, Object>>> consumerMap;

    public RunConsumer(String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181");
        props.put("group.id", "group1");
        props.put("schema.registry.url", "http://localhost:8081");

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);

        VerifiableProperties vProps = new VerifiableProperties(props);
        KafkaAvroDecoder keyDecoder = new KafkaAvroDecoder(vProps);
        KafkaAvroDecoder valueDecoder = new KafkaAvroDecoder(vProps);

        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

        this.consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
    }

    public void run() {
        List<KafkaStream<Object, Object>> streams = consumerMap.get(topic);

        ExecutorService executor = Executors.newFixedThreadPool(1);

        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new Consumer(stream, threadNumber));
            threadNumber++;
        }
    }

    public static void main(String[] args) {
        RunConsumer example = new RunConsumer("backward");
        LOGGER.info("running example");
        example.run();
    }
}
