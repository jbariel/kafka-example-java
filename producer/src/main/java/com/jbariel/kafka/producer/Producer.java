package com.jbariel.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {

    private final static Logger log = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        log.info("Hello from the Kafka Producer");

        System.exit(0);
    }
}