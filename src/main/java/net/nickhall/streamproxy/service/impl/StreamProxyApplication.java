package net.nickhall.streamproxy.service.impl;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.nickhall.streamproxy.engine.PlaylistCachingEngine;
import no.digipost.dropwizard.TypeSafeConfigBundle;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.nio.file.Paths;

public class StreamProxyApplication extends Application<StreamProxyConfiguration> {
    @Override
    public void initialize(Bootstrap<StreamProxyConfiguration> bootstrap) {
        bootstrap.addBundle(new TypeSafeConfigBundle());
    }

    @Override
    public void run(StreamProxyConfiguration streamProxyConfiguration, Environment environment) throws Exception {
        // create and start the async HTTP client
        final CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();

        // create the playlist caching engine
        final PlaylistCachingEngine playlistCachingEngine = new PlaylistCachingEngine(
                httpAsyncClient,
                streamProxyConfiguration.getThreads(),
                Paths.get(streamProxyConfiguration.getCacheDir())
        );

        // create the proxy service
        final StreamProxyService streamProxyService = new StreamProxyService(playlistCachingEngine, httpAsyncClient);
        environment.jersey().register(streamProxyService);
    }

    public static void main(String[] args) throws Exception {
        new StreamProxyApplication().run(args);
    }
}
