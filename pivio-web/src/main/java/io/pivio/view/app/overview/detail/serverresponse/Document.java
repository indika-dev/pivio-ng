package io.pivio.view.app.overview.detail.serverresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    public String owner;
    public String data_format_version;
    public String description;
    public String type;
    public String vcsroot;
    public String name;
    public String short_name;
    public String id;
    public String contact;
    public String created;
    public String lastUpload;
    public String lastUpdate;
    public String status;
    public String product_context;
    public List<String> tags = new ArrayList<>();
    public Map<String, String> links = new HashMap<>();

    public Runtime runtime = new Runtime();
    public List<SoftwareDependency> software_dependencies = new ArrayList<>();
    public Service service = new Service();
    public Context context = new Context();

    @Override
    public String toString() {
        return "Document{" +
                "owner='" + owner + '\'' +
                ", data_format_version='" + data_format_version + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", vcsroot='" + vcsroot + '\'' +
                ", name='" + name + '\'' +
                ", short_name='" + short_name + '\'' +
                ", id='" + id + '\'' +
                ", contact='" + contact + '\'' +
                ", created='" + created + '\'' +
                ", lastUpload='" + lastUpload + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", status='" + status + '\'' +
                ", product_context='" + product_context + '\'' +
                ", tags=" + tags +
                ", links=" + links +
                ", runtime=" + runtime +
                ", software_dependencies=" + software_dependencies +
                ", service=" + service +
                ", context=" + context +
                '}';
    }

    public boolean ofTypeService() {
        return type.equalsIgnoreCase("service");
    }

}
