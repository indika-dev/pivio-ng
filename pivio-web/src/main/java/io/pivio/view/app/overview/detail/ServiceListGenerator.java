package io.pivio.view.app.overview.detail;

import io.pivio.view.PivioServerConnector;
import io.pivio.view.app.overview.detail.serverresponse.Provides;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces a map with serviceId as key and service_names and short_name:port as values of
 * all services known to the system.
 */

@Service
public class ServiceListGenerator {

    @Autowired
    PivioServerConnector pivioServerConnector;

    Map<String, String> getServiceNameMap() {
        List<ServiceIdShortName> allServices = pivioServerConnector.getAllServices();

        Map<String, String> serviceNameMap = new HashMap<>();
        for (ServiceIdShortName service : allServices) {
            if (service.service != null) {
                for (Provides provide : service.service.provides) {
                    if (provide.service_name != null) {
                        serviceNameMap.put(provide.service_name, service.id);
                    }
                    if (service.short_name != null) {
                        serviceNameMap.put(service.short_name + " : " + provide.port, service.id);
                    }
                }
            }
        }
        return serviceNameMap;
    }

}
