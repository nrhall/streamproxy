package net.nickhall.streamproxy.playlist;

public class Segment {
    private final int sequence;
    private final String title;
    private final double duration;
    private String url;
    private boolean downloaded;
    private String dateTime;

    public Segment(int sequence, String title, double duration) {
        this.sequence = sequence;
        this.title = title;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public double getDuration() {
        return duration;
    }

    public String getSegmentUrl() {
        return url;
    }

    public String getUrl(String playlistUrl) {
        String[] segmentUrlSplit = url.split("/");
        if (segmentUrlSplit[0].equals(".")) {
            // this means the segment URL is relative to the playlist
            String[] playlistUrlSplit = playlistUrl.split("/");

            // remove the playlist name from the playlistUrl and retain the parent URL, then add the
            // segment URL minus the '.' (first component)
            String[] merge = new String[playlistUrlSplit.length - 1 + segmentUrlSplit.length - 1];
            System.arraycopy(playlistUrlSplit, 0, merge, 0, playlistUrlSplit.length - 1);
            System.arraycopy(segmentUrlSplit, 1, merge, playlistUrlSplit.length - 1, segmentUrlSplit.length - 1);
            return String.join("/", merge);
        }
        else {
            // segment URL is absolute
            return url;
        }
    }

    public void setSegmentURL(String url) {
        this.url = url;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getSequence() {
        return sequence;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "title='" + title + '\'' +
                ", duration='" + duration + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
