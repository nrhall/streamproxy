package net.nickhall.streamproxy.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Playlist {
    private static Logger logger = LoggerFactory.getLogger(Playlist.class);
    private PlaylistType type = PlaylistType.LIVE;
    private boolean ended = false;
    private LinkedList<Segment> segments = new LinkedList<>();
    private boolean seenMagic = false;
    private int version;
    private int sequence;
    private String targetDuration;
    private String url;

    public Playlist(URL url) throws PlaylistException {
        this.url = url.toString();
        try {
            parse(url.openStream());
        } catch (IOException e) {
            throw new PlaylistException(e);
        }
    }

    public Playlist(InputStream playlistStream, String url) throws PlaylistException {
        this.url = url;
        parse(playlistStream);
    }

    public PlaylistType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public LinkedList<Segment> getSegments() {
        return segments;
    }

    public boolean hasEnded() {
        return ended;
    }

    private void parse(InputStream playlistStream) throws PlaylistException {
        try {
            // get a buffered reader so we can use a Stream
            BufferedReader playlistStreamReader = new BufferedReader(new InputStreamReader(playlistStream));

            playlistStreamReader.lines()
                    .forEach(l -> {
                        // parse any lines beginning with #EXT; these are markers referring to the next segment
                        String[] s = l.split(":");
                        try {
                            // file must begin with #EXTM3U
                            if (!seenMagic && !s[0].equals("#EXTM3U")) {
                                throw new RuntimeException("no magic at start of streamproxy");
                            } else {
                                switch (s[0]) {
                                    case "#EXTM3U":
                                        seenMagic = true;
                                        break;
                                    case "#EXT-X-VERSION":
                                        version = Integer.parseInt(s[1]);
                                        break;
                                    case "#EXT-X-PLAYLIST-TYPE":
                                        type = PlaylistType.valueOf(s[1]);
                                        break;
                                    case "#EXT-X-TARGETDURATION":
                                        targetDuration = s[1];
                                        break;
                                    case "#EXT-X-MEDIA-SEQUENCE":
                                        sequence = Integer.parseInt(s[1]);
                                        break;
                                    case "#EXT-X-ENDLIST":
                                        ended = true;
                                        break;
                                    case "#EXTINF":
                                        String[] s2 = s[1].split(",");
                                        String duration = s2[0];
                                        String title = "";
                                        if (s2.length == 2) {
                                            title = s2[1];
                                        }
                                        segments.addLast(new Segment(sequence++, title, duration));
                                        break;
                                    case "#EXT-X-PROGRAM-DATE-TIME":
                                        segments.getLast().setDateTime(s[1]);
                                        break;
                                    default:
                                        if (s[0].startsWith("#")) {
                                            if (s[0].startsWith("#EXT")) {
                                                logger.error("unknown tag: {}", s[0]);
                                            }
                                        } else {
                                            segments.getLast().setURL(s[0]);
                                        }
                                }
                            }
                        } catch (NoSuchElementException e) {
                            //
                        }
                    });
        } catch (RuntimeException e) {
            throw new PlaylistException(e);
        }
    }

    public static String mergeUrls(String playlistUrl, String segmentUrl) {
        String[] playlistUrlSplit = playlistUrl.split("/");
        String[] segmentUrlSplit = segmentUrl.split("/");
        String[] merge = new String[playlistUrlSplit.length - 1 + segmentUrlSplit.length - 1];
        System.arraycopy(playlistUrlSplit, 0, merge, 0, playlistUrlSplit.length - 1);
        System.arraycopy(segmentUrlSplit, 1, merge, playlistUrlSplit.length - 1, segmentUrlSplit.length - 1);
        return String.join("/", merge);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "type=" + type +
                ", ended=" + ended +
                ", segments=" + segments +
                ", seenMagic=" + seenMagic +
                ", version=" + version +
                ", sequence='" + sequence + '\'' +
                ", targetDuration='" + targetDuration + '\'' +
                '}';
    }
}
