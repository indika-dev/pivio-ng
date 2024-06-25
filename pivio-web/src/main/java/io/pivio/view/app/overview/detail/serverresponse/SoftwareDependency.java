package io.pivio.view.app.overview.detail.serverresponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SoftwareDependency implements Comparable {

    public String name;
    public String version;
    public List<License> licenses = new ArrayList<>();

    public SoftwareDependency() {
    }

    public SoftwareDependency(String name, String version, List<License> licenses) {
        this.name = name;
        this.version = version;
        this.licenses = licenses;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((SoftwareDependency)o).name);
    }
}