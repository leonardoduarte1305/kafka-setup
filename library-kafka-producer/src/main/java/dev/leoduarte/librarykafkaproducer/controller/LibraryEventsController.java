package dev.leoduarte.librarykafkaproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.leoduarte.librarykafkaproducer.domain.LibraryEvent;
import dev.leoduarte.librarykafkaproducer.domain.LibraryEventType;
import dev.leoduarte.librarykafkaproducer.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws
            JsonProcessingException {

        libraryEventsProducer.postLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventsProducer.sendLibraryEventSyncWithTimeout(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.libraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        if (!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }

    // ===================================================================================
    @PostMapping("/v1/libraryevent/record")
    public ResponseEntity<LibraryEvent> sendLibraryEventAsyncWithRecord(@RequestBody LibraryEvent libraryEvent) throws
            JsonProcessingException {

        libraryEventsProducer.sendLibraryEventAsyncWithRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/v1/libraryevent/sync")
    public ResponseEntity<LibraryEvent> sendLibraryEventSync(@RequestBody LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        libraryEventsProducer.sendLibraryEventSync(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/v1/libraryevent/sync/timeout")
    public ResponseEntity<LibraryEvent> sendLibraryEventSyncWithTimeout(@RequestBody @Valid LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        libraryEventsProducer.sendLibraryEventSyncWithTimeout(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
