package net.nickhall.eventproxy.service.api;

import net.nickhall.eventproxy.playlist.Segment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("eventproxy")
public interface EventProxy {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("playlist/cache")
    void cachePlaylist(EventProxyRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("playlist/status")
    List<Segment> getPlaylistStatus(EventProxyRequest request);
}
