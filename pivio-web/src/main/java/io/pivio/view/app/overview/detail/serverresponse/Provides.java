package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Provides {

    public String description;
    public String service_name;
    public String protocol;
    public String port;
    public String transport_protocol;
    public List<String> public_dns = new ArrayList<>();
}
