package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    public List<Provides> provides = new ArrayList<>();
    public DependsOn depends_on = new DependsOn();

}
