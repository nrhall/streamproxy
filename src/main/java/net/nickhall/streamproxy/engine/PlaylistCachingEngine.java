package net.nickhall.streamproxy.engine;

import net.nickhall.streamproxy.playlist.Playlist;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistCachingEngine {
    private final static Logger logger = LoggerFactory.getLogger(PlaylistCachingEngine.class);
    private final HttpAsyncClient httpAsyncClient;
    private final ExecutorService executor;
    private final Path cachePath;

    public PlaylistCachingEngine(HttpAsyncClient httpAsyncClient, int threads, Path cachePath) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.httpAsyncClient = httpAsyncClient;
        this.cachePath = cachePath;
    }

    public void cache(Playlist playlist, String cacheKey) {
        Path cachePath = this.cachePath.resolve(cacheKey);
        // determine where the cachePath will be stored
        if (Files.exists(cachePath)) {
            // error - cachePath directory already exists
            throw new IllegalStateException("cachePath directory already exists");
        } else {
            // create the cachePath directory
            try {
                Files.createDirectory(cachePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // create tasks for each download and submit to the executor
            playlist.getSegments()
                    .stream()
                    .forEach(segment -> CompletableFuture
                            .supplyAsync(() -> {
                                // check for a relative url
                                String url;
                                if (segment.getUrl().startsWith(".")) {
                                    url = Playlist.mergeUrls(playlist.getUrl(), segment.getUrl());
                                } else {
                                    url = segment.getUrl();
                                }

                                logger.info("cachePath segment: {}", url);

                                // create the request/request producer and execute
                                HttpGet request = new HttpGet(url);
                                httpAsyncClient.execute(request, new FutureCallback<HttpResponse>() {
                                    @Override
                                    public void completed(HttpResponse httpResponse) {
                                        int status = httpResponse.getStatusLine().getStatusCode();
                                        if (status == Response.Status.OK.getStatusCode()) {
                                            // stream the entity to the cache file
                                            try {
                                                String cachedFilename = Integer.toString(segment.getSequence());
                                                OutputStream outputStream =
                                                        Files.newOutputStream(cachePath.resolve(cachedFilename));
                                                httpResponse.getEntity().writeTo(outputStream);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            throw new RuntimeException("non ok status from server");
                                        }
                                    }

                                    @Override
                                    public void failed(Exception e) {
                                        throw new RuntimeException(e);
                                    }

                                    @Override
                                    public void cancelled() {
                                        throw new RuntimeException("request cancelled");
                                    }

                                });
                                return segment;
                            }, executor)
                            .thenAccept(s -> {
                                logger.info("cachePath segment complete: {}", segment.getUrl());
                                s.setDownloaded(true);
                            }));
        }
    }

    public PlaylistCachingStatus getStatus(String url) {
        return null;
    }
}
