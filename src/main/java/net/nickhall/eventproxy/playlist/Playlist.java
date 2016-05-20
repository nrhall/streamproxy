package net.nickhall.eventproxy.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Playlist {

    private static Logger logger = LoggerFactory.getLogger(Playlist.class);
    private PlaylistType type = PlaylistType.LIVE;
    private boolean ended = false;
    private URL playlistURL;
    private LinkedList<Segment> segmentList = new LinkedList<>();
    private boolean seenMagic = false;
    private int version;
    private String sequence;
    private String targetDuration;

    public Playlist(URL playlistURL) throws PlaylistException {
        this.playlistURL = playlistURL;
        parse();
    }

    public PlaylistType getType() {
        return type;
    }

    public boolean hasEnded() {
        return ended;
    }

    private void parse() throws PlaylistException {
        try {
            // open the stream and get a buffered reader so we can use a Stream
            InputStream playlistStream = playlistURL.openStream();
            BufferedReader playlistStreamReader = new BufferedReader(new InputStreamReader(playlistStream));

            playlistStreamReader.lines()
                    .forEach(l -> {
                        // parse any lines beginning with #EXT; these are markers referring to the next segment
                        StringTokenizer st = new StringTokenizer(l, ":,");
                        String next = null;

                        try {
                            next = st.nextToken();
                            logger.debug("next tag: {}", next);

                            // file must begin with #EXTM3U
                            if (!seenMagic && !next.equals("#EXTM3U")) {
                                throw new RuntimeException("no magic at start of eventproxy");
                            } else {
                                switch (next) {
                                    case "#EXTM3U":
                                        seenMagic = true;
                                        break;
                                    case "#EXT-X-VERSION":
                                        version = Integer.parseInt(st.nextToken());
                                        break;
                                    case "#EXT-X-PLAYLIST-TYPE":
                                        type = PlaylistType.valueOf(st.nextToken());
                                        break;
                                    case "#EXT-X-TARGETDURATION":
                                        targetDuration = st.nextToken();
                                        break;
                                    case "#EXT-X-MEDIA-SEQUENCE":
                                        sequence = st.nextToken();
                                        break;
                                    case "#EXT-X-ENDLIST":
                                        ended = true;
                                        break;
                                    case "#EXTINF":
                                        String duration = st.nextToken();
                                        String title = st.nextToken();
                                        segmentList.addLast(new Segment(title, duration));
                                        break;
                                    default:
                                        if (next.startsWith("#")) {
                                            if (next.startsWith("#EXT")) {
                                                logger.error("unknown tag: {}", next);
                                            }
                                        } else {
                                            segmentList.getLast().setFile(next);
                                        }
                                }
                            }
                        } catch (NoSuchElementException e) {
                            //
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            throw new PlaylistException(e);
        }
    }
}
