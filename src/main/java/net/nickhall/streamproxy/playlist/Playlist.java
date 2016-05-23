package net.nickhall.streamproxy.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

public class Playlist {
    private static Logger logger = LoggerFactory.getLogger(Playlist.class);
    private PlaylistType type = PlaylistType.LIVE;
    private boolean ended = false;
    private LinkedList<Segment> segments = new LinkedList<>();
    private boolean seenMagic = false;
    private int version;
    private int sequence;
    private int targetDuration;
    private String url;

    public static class Builder {
        private String urlString;
        private URL url = null;
        private InputStream inputStream;

        public Builder withUrl(URL url) {
            this.urlString = url.toString();
            this.url = url;
            return this;
        }

        public Builder withUrl(String urlString) {
            this.urlString = urlString;
            return this;
        }

        public Builder withInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Playlist build() throws PlaylistException {
            Playlist playlist = new Playlist(urlString);

            if (url != null) {
                try (InputStream inputStream = url.openStream()) {
                    playlist.parse(inputStream);
                } catch (IOException e) {
                    throw new PlaylistException(e);
                }
            } else if (inputStream != null) {
                playlist.parse(inputStream);
            }

            return playlist;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private Playlist(String url) throws PlaylistException {
        this.url = url;
    }

    private void parse(InputStream inputStream) throws PlaylistException {
        try {
            // get a buffered reader so we can use a Stream
            BufferedReader playlistStreamReader = new BufferedReader(new InputStreamReader(inputStream));
            playlistStreamReader.lines()
                    .forEach(l -> {
                        // parse any lines beginning with #EXT; these are markers referring to the next segment
                        // went with a simple split() approach here instead of anything more complicated
                        // see http://stackoverflow.com/questions/6983856/why-is-stringtokenizer-deprecated
                        // and http://stackoverflow.com/questions/691184/scanner-vs-stringtokenizer-vs-string-split
                        String[] s = l.split(":");

                        // file must begin with #EXTM3U
                        if (!seenMagic && !s[0].equals("#EXTM3U")) {
                            throw new RuntimeException("no magic at start of streamproxy");
                        }

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
                                targetDuration = Integer.parseInt(s[1]);
                                break;
                            case "#EXT-X-MEDIA-SEQUENCE":
                                sequence = Integer.parseInt(s[1]);
                                break;
                            case "#EXT-X-ENDLIST":
                                ended = true;
                                break;
                            case "#EXTINF":
                                String[] s2 = s[1].split(",");
                                double duration = Double.parseDouble(s2[0]);
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
                                    segments.getLast().setSegmentURL(s[0]);
                                }
                                break;
                        }
                    });
        } catch (RuntimeException e) {
            throw new PlaylistException(e);
        }
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
