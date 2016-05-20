package net.nickhall.streamproxy.service.api;

public class StreamProxyRequest {
    private String url;
    private int sequence;
    private String cacheKey;

    public String getURL() {
        return url;
    }

    public int getSequence() {
        return sequence;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}
