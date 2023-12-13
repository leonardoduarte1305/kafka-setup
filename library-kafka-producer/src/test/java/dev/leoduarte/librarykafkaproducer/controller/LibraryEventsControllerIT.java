package dev.leoduarte.librarykafkaproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.leoduarte.librarykafkaproducer.domain.LibraryEvent;
import dev.leoduarte.librarykafkaproducer.util.TestUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// Configuring the embedded Kafka Broker
@EmbeddedKafka(topics = "library-events")

// Overriding the Kafka producer bootstrap servers to the embedded ones
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class LibraryEventsControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;
    private final String BASE_PATH = "/v1/libraryevent";

    @BeforeEach
    void setUp() {
        HashMap<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps(
                "group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        consumer = new DefaultKafkaConsumerFactory<Integer, String>(
                configs, new IntegerDeserializer(), new StringDeserializer())
                .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {
        LibraryEvent libraryEvent = TestUtil.libraryEventRecord();

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> reposnseEntity = restTemplate
                .exchange(BASE_PATH, HttpMethod.POST, request, LibraryEvent.class);


        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);

        assertEquals(HttpStatus.CREATED, reposnseEntity.getStatusCode());
        consumerRecords.forEach(record -> {
            LibraryEvent libraryEventActual = TestUtil.parseLibraryEventRecord(objectMapper, record.value());
            assertEquals(libraryEvent, libraryEventActual);
        });
    }
}