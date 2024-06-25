package io.pivio.view.app.overview.detail.view;

public class ServiceDependencyViewModel {

    public String serviceId;
    public String serviceDisplayName;

    public boolean hasServiceId() {
        return !serviceId.equals("");
    }

    public ServiceDependencyViewModel(String serviceId, String serviceDisplayName) {
        this.serviceId = serviceId;
        this.serviceDisplayName = serviceDisplayName;
    }
}
