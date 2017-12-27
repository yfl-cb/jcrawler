package ono.kamiya.sy.jcrawler.util;

public enum EventType {
    INFO ("INFO"),
    DEBUG ("DEBUG"),
    ERROR ("ERROR"),
    CRITICAL ("CRITICAL"),
    FAILURE ("FAILURE");

    private final String eventType;
    EventType(String type) { this.eventType = type; }

    String str() { return this.eventType; }
}
