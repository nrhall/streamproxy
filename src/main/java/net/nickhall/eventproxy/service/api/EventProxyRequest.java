package net.nickhall.eventproxy.service.api;

public class EventProxyRequest {
    String uri;
    int sequence;

    public String getUri() {
        return uri;
    }

    public int getSequence() {
        return sequence;
    }
}
