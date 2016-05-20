package net.nickhall.streamproxy.service.api;

import net.nickhall.streamproxy.playlist.Segment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("streamproxy")
public interface StreamProxy {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("playlist/cache")
    void cachePlaylist(StreamProxyRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("playlist/status")
    List<Segment> getPlaylistStatus(StreamProxyRequest request);
}
