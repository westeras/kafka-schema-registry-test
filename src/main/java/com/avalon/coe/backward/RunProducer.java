package com.avalon.coe.backward;

import com.avalon.coe.Producer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by adam on 1/24/17.
 */
public class RunProducer extends Producer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RunProducer.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            LOGGER.error("must pass avro schema file name as argument");
            System.exit(-1);
        }
        RunProducer producer = new RunProducer();
        producer.sendData(args[0]);
    }

    private void sendData(String avroSchema) throws IOException {
        String key = "backward";
        String topic = "backward";
        Schema schema = new Schema.Parser().parse(new File("./src/main/resources/" + avroSchema));

        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("user_id", "a568523");
        avroRecord.put("event_id", "abc12345");
        avroRecord.put("event_timestamp", "1485294737");
        if (avroSchema.equals("user_new.avsc")) {
            avroRecord.put("event_type", "page_hit");
        }
        LOGGER.info("created GenericRecord: " + avroRecord.toString());

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, key, avroRecord);
        LOGGER.info("sending record to topic: " + topic);
        getProducer().send(record);
        LOGGER.info("record sent");
        getProducer().close();
    }
}
