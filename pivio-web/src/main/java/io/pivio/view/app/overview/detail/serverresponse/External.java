package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class External {

    public String target = "";
    public String transport_protocol = "";
    public String via = "";
    public String why = "";

}
