package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Runtime {

    public String cpu;
    public String ram;
    public String disk;
    public String host_type;
    public String network_zone;
}
