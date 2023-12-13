package dev.leoduarte.librarykafkaconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableKafka
public class LibraryEventsConsumer {

    @KafkaListener(topics = "${leoduarte.customconfig.newtopicname}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.error("ConsumerRecord: {}", consumerRecord);
    }
}
