package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependsOn {

    public List<Internal> internal = new ArrayList<>();
    public List<External> external = new ArrayList<>();
}
