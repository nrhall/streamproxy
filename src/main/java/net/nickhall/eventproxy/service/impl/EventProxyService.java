package net.nickhall.eventproxy.service.impl;

import net.nickhall.eventproxy.playlist.Segment;
import net.nickhall.eventproxy.service.api.EventProxy;
import net.nickhall.eventproxy.service.api.EventProxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class EventProxyService implements EventProxy {
    private static Logger logger = LoggerFactory.getLogger(EventProxyService.class);

    @Override
    public void cachePlaylist(EventProxyRequest request) {
        logger.info("playlist: {}", request.getUri());
        logger.info("sequence: {}", request.getSequence());
    }

    @Override
    public List<Segment> getPlaylistStatus(EventProxyRequest request) {
        logger.info("playlist: {}", request.getUri());
        return new ArrayList<>();
    }
}
