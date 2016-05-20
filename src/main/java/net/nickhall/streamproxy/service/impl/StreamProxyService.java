package net.nickhall.streamproxy.service.impl;

import net.nickhall.streamproxy.engine.PlaylistCachingEngine;
import net.nickhall.streamproxy.playlist.Playlist;
import net.nickhall.streamproxy.service.api.StreamProxy;
import net.nickhall.streamproxy.service.api.StreamProxyRequest;
import net.nickhall.streamproxy.util.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class StreamProxyService implements StreamProxy {
    private final static Logger logger = LoggerFactory.getLogger(StreamProxyService.class);
    private final HttpAsyncClient httpAsyncClient;
    private final PlaylistCachingEngine engine;

    public StreamProxyService(PlaylistCachingEngine engine, HttpAsyncClient httpAsyncClient) {
        this.engine = engine;

        // start the async client
        this.httpAsyncClient = httpAsyncClient;
    }

    @Override
    public void cachePlaylist(@Suspended final AsyncResponse asyncResponse, StreamProxyRequest request) {
        logger.info("playlist cache request: uri={} sequence={}", request.getURL(), request.getSequence());

        try {
            // create the request/request producer and execute
            HttpGet playlistRequest = new HttpGet(request.getURL());
            httpAsyncClient.execute(playlistRequest, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    int status = httpResponse.getStatusLine().getStatusCode();
                    if (status != Status.OK.getStatusCode()) {
                        // handle authentication and other client errors by passing back the status code
                        if (status >= 400 && status < 500) {
                            asyncResponse.resume(new WebApplicationException(status));
                        } else {
                            asyncResponse.resume(Response.serverError().build());
                        }
                    } else {
                        // grab the playlist and parse it

                        // check content type is correct
                        if (!httpResponse.getEntity().getContentType().getValue().equals(Constants.APPLICATION_VND_APPLE_MPEGURL)) {
                            logger.error("incorrect content type for remote playlist: {}", request.getURL());
                            asyncResponse.resume(Response.status(Status.BAD_REQUEST));
                        } else {
                            try {
                                Playlist playlist = new Playlist(httpResponse.getEntity().getContent(), request.getURL());

                                // ask the caching engine to cache the playlist
                                if (request.getCacheKey() == null) {
                                    asyncResponse.resume(Response.serverError().build());
                                } else {
                                    engine.cache(playlist, request.getCacheKey());
                                    asyncResponse.resume(Response.ok().build());
                                }
                            } catch (Exception e) {
                                logger.error("error getting content for playlist", e);
                                asyncResponse.resume(Response.status(Status.BAD_REQUEST));
                            }
                        }
                    }
                }

                @Override
                public void failed(Exception e) {
                    // TODO: an exception here could be for a number of reasons - client or server, would be
                    // TODO: worth doing some testing to see what gets returned under different circumstances
                    asyncResponse.resume(Response.serverError().build());
                }

                @Override
                public void cancelled() {
                    asyncResponse.resume(Response.serverError().build());
                }
            });
        } catch (Exception e) {
            logger.error("exception while fetching playlist", e);
            asyncResponse.resume(Response.serverError().build());
        }
    }

    @Override
    public void getPlaylistStatus(@Suspended final AsyncResponse asyncResponse, StreamProxyRequest request) {
        logger.info("playlist status request: uri={}", request.getURL());

        // ask the engine for a response
        Playlist playlist = engine.getStatus(request.getCacheKey());
        asyncResponse.resume(playlist);
    }
}
