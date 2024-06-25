package io.pivio.view.app.feed.serverresponse;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FeedItemTest {

    private FeedItem feedItem;

    @Before
    public void setup() {
        feedItem = new FeedItem();
        feedItem.fields.add(new Field("add", "/test/1", "value"));
        feedItem.fields.add(new Field("add", "/test/2", "value"));
        feedItem.fields.add(new Field("add", "/demo", "value"));
        feedItem.fields.add(new Field("add", "/demo/1", "value"));
        feedItem.fields.add(new Field("add", "/hallo", "value"));
    }

    @Test
    public void testUniquePath() {
        assertThat(feedItem.getUniquePath().size()).isEqualTo(3);
    }

}