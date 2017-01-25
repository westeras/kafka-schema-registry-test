package com.avalon.coe;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by adam on 1/24/17.
 */
public class Producer {

    private KafkaProducer<String, GenericRecord> producer;

    public Producer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        this.producer = new KafkaProducer<>(props);
    }

    public KafkaProducer getProducer() {
        return producer;
    }
}
