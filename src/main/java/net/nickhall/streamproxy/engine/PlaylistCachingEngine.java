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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistCachingEngine {
    private final static Logger logger = LoggerFactory.getLogger(PlaylistCachingEngine.class);
    private final HttpAsyncClient httpAsyncClient;
    private final ExecutorService executor;
    private final Path cacheBasePath;
    private final Map<String, Playlist> cache = new ConcurrentHashMap<>();

    public PlaylistCachingEngine(HttpAsyncClient httpAsyncClient, int threads, Path cacheBasePath) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.httpAsyncClient = httpAsyncClient;
        this.cacheBasePath = cacheBasePath;
    }

    public void cachePlaylist(Playlist playlist, String cacheKey) {
        Path cachePath = this.cacheBasePath.resolve(cacheKey);

        // determine where the cache will be stored
        if (Files.exists(cachePath)) {
            // error - cache directory already exists
            throw new IllegalStateException("cache directory already exists");
        } else {
            // create the cache directory
            try {
                Files.createDirectory(cachePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // cache the playlist
            cache.put(cacheKey, playlist);

            // create tasks for each download and submit to the executor
            playlist.getSegments().stream()
                    .forEach(segment -> {
                        // handle the case where the segment URL might be relative
                        String url = segment.getUrl(playlist.getUrl());

                        CompletableFuture
                                .supplyAsync(() -> {
                                    logger.info("cache segment: {}", url);

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
                                    logger.info("cache segment complete: {}", url);
                                    s.setDownloaded(true);
                                });
                    });
        }
    }

    public Playlist getStatus(String cacheKey) {
        return cache.get(cacheKey);
    }
}
