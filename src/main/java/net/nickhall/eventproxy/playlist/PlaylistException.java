package net.nickhall.eventproxy.playlist;

public class PlaylistException extends Exception {
    public PlaylistException(String message) {
        super(message);
    }

    public PlaylistException(Exception e) {
        super(e);
    }
}
