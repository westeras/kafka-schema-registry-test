# Testing Steps with Docker

## Prerequisite
* Docker for Mac

## Steps
```bash
docker-compose up -d zookeeper kafka
sleep 15
docker-compose up -d schemaregistry
sleep 30
curl http://localhost:8081/subjects
docker-compose exec kafka bash -c "echo -e '{\"f1\":\"value1\"}\n{\"f1\":\"value2\"}\n{\"f1\":\"value3\"}' | kafka-avro-console-producer --broker-list kafka:9092 --key-serializer io.confluent.kafka.serializers.KafkaAvroSerializer --value-serializer io.confluent.kafka.serializers.KafkaAvroSerializer --property schema.registry.url=http://schemaregistry:8081 --topic test_topic --prop value.schema='{\"type\":\"record\",\"name\":\"myrecord\",\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}'"
curl http://localhost:8081/subjects

docker-compose exec kafka bash -c "kafka-avro-console-consumer --new-consumer --bootstrap-server localhost:9092 --topic test_topic --from-beginning --max-messages 3"

docker-compose exec kafka bash -c "kafka-avro-console-consumer --new-consumer --bootstrap-server localhost:9092 --topic test_topic --from-beginning --max-messages 3 --property schema.registry.url=http://schemaregistry:8081"

curl http://localhost:8081/schemas/ids/1
docker-compose down -v
```

## References
* http://stackoverflow.com/questions/31204201/apache-kafka-with-avro-and-schema-repo-where-in-the-message-does-the-schema-id

