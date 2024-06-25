package io.pivio.view.app.overview.list.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pivio.view.app.overview.detail.serverresponse.Context;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Overview implements Comparable {

    public String name;
    public String short_name;
    public String description;
    public String lastUpload;
    public String lastUpdate;
    public Context context;
    public String id;
    public String owner;
    public String type;

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((Overview) o).name);
    }
}
