package io.pivio.view.app.feed.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {

    public Field() {
    }

    public String op;
    public String path;
    public String value;

    public Field(String op, String path, String value) {
        this.op = op;
        this.path = path;
        this.value = value;
    }

    public String getColor() {
        if (op.equals("add")) {
            return "green";
        }
        if (op.equals("replace")) {
            return "orange";
        }
        if (op.equals("remove")) {
            return "red";
        }
        return "grey";
    }

    public String getIcon() {
        if (op.equals("add")) {
            return "circle add";
        }
        if (op.equals("replace")) {
            return "arrow circle down";
        }
        if (op.equals("remove")) {
            return "minus circle";
        }
        return "selected radio";

    }

    public String getColoredIcon() {
        return getColor() + " " + getIcon();
    }

}
