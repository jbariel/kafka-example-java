package com.jbariel.kafka.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private static final LongSerializer KEY_SER = new LongSerializer();
    private static final StringSerializer VAL_SER = new StringSerializer();

    protected static String environmentOrDefault(String envKey, String defaultVal) {
        String val = StringUtils.trimToNull(System.getenv(envKey));
        return (null == val) ? defaultVal : val;
    }

    private static final String KAFKA_BROKERS = environmentOrDefault("KAFKA_BROKER_URIS", "localhost:9092");
    private static final String PRODUCER_ID = environmentOrDefault("PRODUCER_ID", "kafka-example-java");
    private static final String TOPIC_NAME = environmentOrDefault("TOPIC_NAME", "kafka-example-java-default-topic");

    private static final Map<String, Object> PRODUCER_CONFIG = new HashMap<>();

    static {
        PRODUCER_CONFIG.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        PRODUCER_CONFIG.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUCER_ID);
    }

    public static void main(String[] args) {
        log.info("Starting the Kafka Producer...");

        final ProducerWrapper producer = new ProducerWrapper(new KafkaProducer<>(PRODUCER_CONFIG, KEY_SER, VAL_SER));
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(producer::sendTime, 2, 1, TimeUnit.SECONDS);

        log.debug("Reading console...");
        log.info("\tType 'exit' to quit");
        log.info("\tOther typed messages will broadcast");
        log.info("What would you like to say?");
        try (BufferedReader in = new BufferedReader(new InputStreamReader((System.in)))) {
            while (true) {
                String msg = StringUtils.trimToNull(in.readLine());
                if (null != msg) {
                    producer.send(msg);
                    if ("exit".equalsIgnoreCase(msg)) {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }

        future.cancel(false);
        producer.close();
        System.exit(0);
    }

    static class ProducerWrapper {

        private final KafkaProducer<Long, String> producer;
        private final Callback sendCallback = new Callback() {

            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                log.info("\tSent message to topic '" + metadata.topic() + "' with offset: " + metadata.offset());
            }
        };

        public ProducerWrapper(KafkaProducer<Long, String> producer) {
            this.producer = producer;
        }

        public void sendTime() {
            send(ZonedDateTime.now().toString());
        }

        public void send(String message) {
            log.info("Sending message: " + message);
            producer.send(new ProducerRecord<Long, String>(TOPIC_NAME, System.currentTimeMillis(), message),
                    sendCallback);
        }

        public void close() {
            producer.close(Duration.ofSeconds(2L));
        }
    }
}