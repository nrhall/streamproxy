package net.nickhall.streamproxy.engine;

public class PlaylistCachingStatus {
    private int segmentCount;
    private boolean ended;
    private PlaylistState state;

    public PlaylistCachingStatus(int segmentCount, boolean ended, PlaylistState state) {
        this.segmentCount = segmentCount;
        this.ended = ended;
        this.state = state;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public boolean isEnded() {
        return ended;
    }

    public PlaylistState getState() {
        return state;
    }
}
