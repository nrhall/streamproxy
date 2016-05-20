package net.nickhall.streamproxy.service.impl;

import io.dropwizard.Configuration;

public class StreamProxyConfiguration extends Configuration {
    private int threads;
    private String cacheDir;

    public int getThreads() {
        return threads;
    }

    public String getCacheDir() {
        return cacheDir;
    }
}
