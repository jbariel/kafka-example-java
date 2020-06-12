package com.jbariel.kafka.consumer;

import java.util.Map;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

    private final static Logger log = LoggerFactory.getLogger(Consumer.class);

    private static final LongDeserializer KEY_DESER = new LongDeserializer();
    private static final StringDeserializer VAL_DESER = new StringDeserializer();

    protected static String environmentOrDefault(String envKey, String defaultVal) {
        String val = StringUtils.trimToNull(System.getenv(envKey));
        return (null == val) ? defaultVal : val;
    }

    private static final String KAFKA_BROKERS = environmentOrDefault("KAFKA_BROKER_URIS", "localhost:9092");
    private static final String CONSUMER_ID = environmentOrDefault("CONSUMER_ID", "kafka-example-java");
    private static final String TOPIC_NAME = environmentOrDefault("TOPIC_NAME", "kafka-example-java-default-topic");

    private static final Map<String, Object> CONSUMER_CONFIG = new HashMap<>();

    static {
        CONSUMER_CONFIG.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        CONSUMER_CONFIG.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_ID);
        CONSUMER_CONFIG.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        CONSUMER_CONFIG.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        CONSUMER_CONFIG.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    public static void main(String[] args) {
        log.info("Starting the Kafka Consumer...");
        final KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(CONSUMER_CONFIG, KEY_DESER, VAL_DESER);
        log.debug("Subscribing to topic: " + TOPIC_NAME);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        try {
            log.debug("Polling...");
            while (true) {
                consumer.poll(Duration.ofSeconds(10)).forEach(record -> log
                        .info(String.format("[%d] %d :: %s", record.offset(), record.key(), record.value())));
                consumer.commitAsync();
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            consumer.close();
        }
        System.exit(0);
    }

}