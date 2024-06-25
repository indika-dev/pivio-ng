package io.pivio.view.app.overview.detail.view;

import io.pivio.view.app.overview.detail.ServiceIdShortName;
import io.pivio.view.app.overview.detail.serverresponse.Internal;
import io.pivio.view.app.overview.detail.serverresponse.Provides;

import java.util.ArrayList;
import java.util.List;

public class ProvidesModel {

    public String description;
    public String service_name;
    public String protocol;
    public String port;
    public String transport_protocol;
    public List<String> public_dns = new ArrayList<>();
    public List<DependentServiceViewModel> dependentServices = new ArrayList<>();

    public ProvidesModel(Provides provides) {
        this.description = provides.description;
        this.service_name = provides.service_name;
        this.protocol = provides.protocol;
        this.port = provides.port;
        this.transport_protocol = provides.transport_protocol;
        this.public_dns = provides.public_dns;
    }

    public String getTechInfo() {
        return protocol + " " + transport_protocol + " " + port;
    }

    public void setDependentServices(List<ServiceIdShortName> allServices, String short_name) {
        for (ServiceIdShortName service : allServices) {
            if (service.hasDependencies()) {
                for (Internal internalDependency : service.service.depends_on.internal) {
                    if (internalDependency.service_name.equals(service_name) ||
                            (internalDependency.short_name.equals(short_name) && internalDependency.port.equals(port))) {
                        dependentServices.add(new DependentServiceViewModel(service.id, service.short_name));
                    }
                }
            }
        }
    }
}
