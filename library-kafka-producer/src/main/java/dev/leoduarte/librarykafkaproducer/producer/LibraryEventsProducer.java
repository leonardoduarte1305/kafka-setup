package dev.leoduarte.librarykafkaproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.leoduarte.librarykafkaproducer.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventsProducer {

    @Value("${leoduarte.customconfig.newtopicname}")
    private String topicName;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CompletableFuture<SendResult<Integer, String>> postLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        // When the .send method is called 2 things happens
        // 1 - A blocking call that retrieves the Kafka metadata (only in the first call)
        // 2 - An asynchronous call to publish the message on Kafka topic
        var completableFuture = kafkaTemplate.send(topicName, key, value);

        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSuccess(key, value, sendResult);
                    }
                });
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsyncWithRecord(LibraryEvent libraryEvent) throws
            JsonProcessingException {

        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer, String> record = buildProducerRecord(key, value);


        // When the .send method is called 2 things happens
        // 1 - A blocking call that retrieves the Kafka metadata (only in the first call)
        // 2 - An asynchronous call to publish the message on Kafka topic
        var completableFuture = kafkaTemplate.send(record);

        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSuccess(key, value, sendResult);
                    }
                });
    }

    public SendResult<Integer, String> sendLibraryEventSync(LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException {

        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        // When the .send method is called 2 things happens
        // 1 - A blocking call that retrieves the Kafka metadata (only in the first call)
        // 2 - Blocks and wait the message to being published on Kafka Topic
        SendResult<Integer, String> result = kafkaTemplate
                .send(topicName, key, value)
                .get();
        handleSuccess(key, value, result);

        return result;
    }

    public SendResult<Integer, String> sendLibraryEventSyncWithTimeout(LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        // When the .send method is called 2 things happens
        // 1 - A blocking call that retrieves the Kafka metadata (only in the first call)
        // 2 - Blocks and wait (at most the timeout) the message to being published on Kafka Topic
        SendResult<Integer, String> result = kafkaTemplate
                .send(topicName, key, value)
                .get(2, TimeUnit.SECONDS);

        handleSuccess(key, value, result);

        return result;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        // If we pass null where the ProducerRecord expects the partition number the Kafka Partitioner does the job for us
        return new ProducerRecord<>(topicName, null, key, value, recordHeaders);
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("Error message on key: {}, value: {}", key, value, throwable);
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for the key: {}, and the value: {}, the partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }
}
