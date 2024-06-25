package io.pivio.view.app.feed;

import io.pivio.view.app.feed.serverresponse.FeedItem;
import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

public class FeedModel {

    public String short_name;
    public FeedItem feedItem;

    public FeedModel(String short_name, FeedItem feedItem) {
        this.short_name = short_name;
        this.feedItem = feedItem;
    }

    public String getPrettyDate() {
        return new PrettyTime().format(new DateTime(feedItem.timestamp).toDate());
    }

}
