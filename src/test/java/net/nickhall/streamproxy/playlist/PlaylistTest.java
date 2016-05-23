package net.nickhall.streamproxy.playlist;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlaylistTest {
    @Test
    public void testVod() throws Exception {
        Playlist vod = Playlist.newBuilder()
                .withUrl(getClass().getClassLoader().getResource("playlist-vod.m3u8"))
                .build();
        assertEquals(PlaylistType.VOD, vod.getType());
    }

    @Test(expected = PlaylistException.class)
    public void testVodNoMagic() throws Exception {
        Playlist vod = Playlist.newBuilder()
                .withUrl(getClass().getClassLoader().getResource("playlist-vod-nomagic.m3u8"))
                .build();
    }

    @Test
    public void testLive() throws Exception {
        Playlist live = Playlist.newBuilder()
                .withUrl(getClass().getClassLoader().getResource("playlist-live.m3u8"))
                .build();
        assertEquals(PlaylistType.LIVE, live.getType());
    }

    @Test
    public void testEventNotEnded() throws Exception {
        Playlist event = Playlist.newBuilder()
                .withUrl(getClass().getClassLoader().getResource("playlist-event.m3u8"))
                .build();
        assertEquals(PlaylistType.EVENT, event.getType());
        assertFalse(event.hasEnded());
    }

    @Test
    public void testEventEnded() throws Exception {
        Playlist eventEnded = Playlist.newBuilder()
                .withUrl(getClass().getClassLoader().getResource("playlist-event-ended.m3u8"))
                .build();
        assertEquals(PlaylistType.EVENT, eventEnded.getType());
        assertTrue(eventEnded.hasEnded());
    }
}
