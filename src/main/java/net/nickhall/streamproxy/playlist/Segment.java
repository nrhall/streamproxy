package net.nickhall.streamproxy.playlist;

public class Segment {
    private final int sequence;
    private final String title;
    private final String duration;
    private String url;
    private boolean downloaded;
    private String dateTime;

    public Segment(int sequence, String title, String duration) {
        this.sequence = sequence;
        this.title = title;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

    public void setURL(String url) {
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
