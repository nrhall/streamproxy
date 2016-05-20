package net.nickhall.streamproxy.service.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("streamproxy")
public interface StreamProxy {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("playlist/cache")
    void cachePlaylist(@Suspended AsyncResponse asyncResponse, StreamProxyRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("playlist/status")
    void getPlaylistStatus(@Suspended AsyncResponse asyncResponse, StreamProxyRequest request);
}
