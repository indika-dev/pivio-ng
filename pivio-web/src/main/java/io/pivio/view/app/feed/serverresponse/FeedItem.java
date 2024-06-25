package io.pivio.view.app.feed.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedItem {

    public String document;
    public int order;
    public Date timestamp;
    public List<Field> fields = new ArrayList<>();

    public FeedItem() {
    }

    public Map<String, List<Field>> getUniquePath() {
        Map<String, List<Field>> result = new HashMap<>();
        for (Field field : fields) {
            String uniquePath = field.path.split("/")[1];
            if (!result.containsKey(uniquePath)) {
                result.put(uniquePath, new ArrayList<>());
            }
            result.get(uniquePath).add(field);
        }
        return result;
    }

}
