package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Context {

    public String belongs_to_bounded_context;
    public String visibility;

    @Override
    public String toString() {
        return "Context{" +
                "belongs_to_bounded_context='" + belongs_to_bounded_context + '\'' +
                ", visibility='" + visibility + '\'' +
                '}';
    }
}
