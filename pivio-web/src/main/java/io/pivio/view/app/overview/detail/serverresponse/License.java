package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class License {
    public String fullName;
    public String url;

    public License(String fullName, String url) {
        this.fullName = fullName;
        this.url = url;
    }

    public License() {
    }
}
