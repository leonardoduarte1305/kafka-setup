package dev.leoduarte.librarykafkaproducer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LibraryEventType {
    NEW("NEW"),
    UPDATE("UPDATE");

    private final String eventType;
}
