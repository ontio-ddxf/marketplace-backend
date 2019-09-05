package com.ontology.service;

import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class EventReceiver {

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private ProducerService producerService;


    @KafkaListener(topics = {Constant.KAFKA_TOPIC_EVENT}, groupId = Constant.KAFKA_GROUP_EVENT)
    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("event-receive:{}",Constant.KAFKA_GROUP_EVENT);
        String value = (String) record.value();
        producerService.parseAndSendOne(value);
        ack.acknowledge();
    }
}
