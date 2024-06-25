package io.pivio.view.app.feed;

import io.pivio.view.PivioServerConnector;
import io.pivio.view.app.feed.serverresponse.FeedItem;
import io.pivio.view.app.overview.detail.ServiceIdShortName;
import io.pivio.view.configuration.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedService {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    PivioServerConnector pivioServerConnector;

    public List<FeedModel> getChangeset() {
        final Map<String, String> idShortNameMap = getIdShortNameMap();
        final List<FeedItem> changeset = pivioServerConnector.getChangeset();
        final List<FeedModel> result = new ArrayList<>();
        for (FeedItem feedItem : changeset) {
            result.add(new FeedModel(idShortNameMap.containsKey(feedItem.document) ? idShortNameMap.get(feedItem.document) : "Unknown", feedItem));
        }
        return result;
    }

    private Map<String, String> getIdShortNameMap() {
        final List<ServiceIdShortName> allServices = pivioServerConnector.getAllServices();
        final Map<String, String> idShortNameMap = new HashMap<>();
        for (ServiceIdShortName allService : allServices) {
            idShortNameMap.put(allService.id, allService.short_name);
        }
        return idShortNameMap;
    }
}
