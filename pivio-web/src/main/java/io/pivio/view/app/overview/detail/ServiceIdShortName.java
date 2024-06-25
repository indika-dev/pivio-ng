package io.pivio.view.app.overview.detail;


import io.pivio.view.app.overview.detail.serverresponse.Service;

public class ServiceIdShortName {

    public Service service;
    public String id;
    public String short_name;

    public ServiceIdShortName() {
    }

    public ServiceIdShortName(Service service, String id, String short_name) {
        this.service = service;
        this.id = id;
        this.short_name = short_name;
    }

    public boolean hasDependencies() {
        return service != null && service.depends_on != null;
    }

    @Override
    public String toString() {
        return "ServiceIdShortName{" +
                "service=" + service +
                ", id='" + id + '\'' +
                ", short_name='" + short_name + '\'' +
                '}';
    }
}
