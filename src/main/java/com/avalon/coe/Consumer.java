package com.avalon.coe;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by adam on 1/25/17.
 */
public class Consumer implements Runnable {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private KafkaStream m_stream;
    private int thread_number;

    public Consumer(KafkaStream stream, int thread_number) {
        this.m_stream = stream;
        this.thread_number = thread_number;
    }

    @Override
    public void run() {
        ConsumerIterator it = m_stream.iterator();
        LOGGER.info("opening iterator");
        while (it.hasNext()) {
            MessageAndMetadata messageAndMetadata = it.next();
            try {
                String key = (String) messageAndMetadata.key();
                GenericRecord value = (GenericRecord) messageAndMetadata.message();

                Schema schema = new Schema.Parser().parse(new File("./src/main/resources/user_new.avsc"));
                File file = new File("user.avro");
                DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
                DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
                dataFileWriter.create(schema, file);
                dataFileWriter.append(value);
                dataFileWriter.close();

                LOGGER.info("key: " + key);
                LOGGER.info("value: " + value);

            } catch (SerializationException e) {
                // may need to do something with it
                LOGGER.error("throwing SerializationException", e);
            } catch (IOException e) {
                LOGGER.error("throwing IOException", e);
            }
        }
        LOGGER.info("Shutting down Thread: " + thread_number);
    }
}
