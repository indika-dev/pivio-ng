package io.pivio.view.app.overview.list;

import io.pivio.view.app.overview.detail.serverresponse.Context;
import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

public class OverviewModel {
    public String name;
    public String short_name;
    public String description;
    public String lastUpload;
    public String lastUpdate;
    public Context context;
    public String id;
    public String owner;
    public String type;

    public String getPrettyUploadDate() {
        return new PrettyTime().format(new DateTime(lastUpload).toDate());
    }

    public String getPrettyUpdateDate() {
        return new PrettyTime().format(new DateTime(lastUpdate).toDate());
    }

    public String getMeta() {
        String meta = name + " " + short_name + " " + description + " " + owner + " ";
        if (context != null) {
            meta = meta + " " + context.belongs_to_bounded_context;
        }
        return meta.toLowerCase();
    }

    public String getCardColor() {
        if (type.equalsIgnoreCase("service")) {
            return "blue";
        }
        return "green";
    }
}
