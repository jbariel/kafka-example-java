package com.jbariel.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

    private final static Logger log = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) {
        log.info("Hello from the Kafka Consumer");

        System.exit(0);
    }
}