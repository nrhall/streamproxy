package net.nickhall.streamproxy.playlist;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SegmentTest {
    @Test
    public void testSegment() {
        Segment segment = new Segment(1, "test", 10.0);
        assertEquals(1, segment.getSequence());
        assertEquals("test", segment.getTitle());
        assertEquals(10.0, segment.getDuration(), 0.0);
    }

    @Test
    public void testRelativeUrl() {
        Segment segment = new Segment(1, "test", 10.0);
        segment.setSegmentURL("./relative");
        String url = segment.getUrl("http://www.example.com/test/playlist.m3u8");
        assertEquals("http://www.example.com/test/relative", url);
    }

    @Test
    public void testAbsoluteUrl() {
        Segment segment = new Segment(1, "test", 10.0);
        segment.setSegmentURL("http://www.example.com/test/absolute");
        String url = segment.getUrl("http://www.example.com/test/playlist.m3u8");
        assertEquals("http://www.example.com/test/absolute", url);
    }
}
