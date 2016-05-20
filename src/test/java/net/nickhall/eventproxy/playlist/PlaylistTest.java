package net.nickhall.eventproxy.playlist;

import net.nickhall.eventproxy.playlist.Playlist;
import net.nickhall.eventproxy.playlist.PlaylistException;
import net.nickhall.eventproxy.playlist.PlaylistType;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlaylistTest {
    @Test
    public void testVod() throws Exception {
        Playlist vod = new Playlist(getClass().getClassLoader().getResource("playlist-vod.m3u8"));
        assertEquals(PlaylistType.VOD, vod.getType());
    }

    @Test(expected = PlaylistException.class)
    public void testVodNoMagic() throws Exception {
        Playlist vod = new Playlist(getClass().getClassLoader().getResource("playlist-vod-nomagic.m3u8"));
    }

    @Test
    public void testLive() throws Exception {
        Playlist live = new Playlist(getClass().getClassLoader().getResource("playlist-live.m3u8"));
        assertEquals(PlaylistType.LIVE, live.getType());
    }

    @Test
    public void testEvent() throws Exception {
        Playlist event = new Playlist(getClass().getClassLoader().getResource("playlist-event.m3u8"));
        assertEquals(PlaylistType.EVENT, event.getType());

        Playlist eventEnded = new Playlist(getClass().getClassLoader().getResource("playlist-event-ended.m3u8"));
        assertEquals(PlaylistType.EVENT, eventEnded.getType());
        assertTrue(eventEnded.hasEnded());
    }
}
