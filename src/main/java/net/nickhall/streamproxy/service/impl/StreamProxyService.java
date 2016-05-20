package net.nickhall.streamproxy.service.impl;

import net.nickhall.streamproxy.playlist.Segment;
import net.nickhall.streamproxy.service.api.StreamProxy;
import net.nickhall.streamproxy.service.api.StreamProxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class StreamProxyService implements StreamProxy {
    private static Logger logger = LoggerFactory.getLogger(StreamProxyService.class);

    @Override
    public void cachePlaylist(StreamProxyRequest request) {
        logger.info("playlist: {}", request.getUri());
        logger.info("sequence: {}", request.getSequence());
    }

    @Override
    public List<Segment> getPlaylistStatus(StreamProxyRequest request) {
        logger.info("playlist: {}", request.getUri());
        return new ArrayList<>();
    }
}
