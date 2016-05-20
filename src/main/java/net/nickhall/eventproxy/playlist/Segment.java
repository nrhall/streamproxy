package net.nickhall.eventproxy.playlist;

public class Segment {
    private final String title;
    private final String duration;
    private String file;

    public Segment(String title, String duration) {
        this.title = title;
        this.duration = duration;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
