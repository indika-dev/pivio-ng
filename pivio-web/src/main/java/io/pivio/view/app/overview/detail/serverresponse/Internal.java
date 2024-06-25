package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Internal {

    public String service_name = "";
    public String short_name = "";
    public String port = "";
    public String why = "";
    public String label = "";

}
