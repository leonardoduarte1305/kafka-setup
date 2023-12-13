package dev.leoduarte.librarykafkaproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.leoduarte.librarykafkaproducer.domain.Book;
import dev.leoduarte.librarykafkaproducer.domain.LibraryEvent;
import dev.leoduarte.librarykafkaproducer.producer.LibraryEventsProducer;
import dev.leoduarte.librarykafkaproducer.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LibraryEventsController.class)
class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void sendLibraryEventSyncWithTimeout() throws Exception {
        when(libraryEventsProducer.sendLibraryEventSyncWithTimeout(isA(LibraryEvent.class)))
                .thenReturn(null);

        String body = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/v1/libraryevent/sync/timeout")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

    }

    @Test
    void sendLibraryEventSyncWithTimeout_InvalidBook() throws Exception {
        when(libraryEventsProducer.sendLibraryEventSyncWithTimeout(isA(LibraryEvent.class)))
                .thenReturn(null);

        String body = objectMapper.writeValueAsString(TestUtil.bookRecordWithInvalidValues());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/v1/libraryevent/sync/timeout")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        String expectedErrorMessage = "book - must not be null";
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }

    @Test
    void updateLibraryEventSyncWithTimeout_UsingIdNull() throws Exception {
        when(libraryEventsProducer.sendLibraryEventSyncWithTimeout(isA(LibraryEvent.class)))
                .thenReturn(null);

        String body = objectMapper.writeValueAsString(TestUtil.newLibraryEventRecordWithLibraryEventId());
        RequestBuilder request = MockMvcRequestBuilders
                .put("/v1/libraryevent")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        String expectedErrorMessage = "Only UPDATE event type is supported";
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }

    @Test
    void sendLibraryEventSyncWithTimeout_UsingEventTypeNew() throws Exception {
        when(libraryEventsProducer.sendLibraryEventSyncWithTimeout(isA(LibraryEvent.class)))
                .thenReturn(null);

        String body = objectMapper.writeValueAsString(new Book(123, null, "Kafka Using Spring Boot"));
        RequestBuilder request = MockMvcRequestBuilders
                .put("/v1/libraryevent")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        String expectedErrorMessage = "book - must not be null";
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }
}