package io.pivio.view.app.overview.detail;

import io.pivio.view.PivioServerConnector;
import io.pivio.view.app.overview.detail.serverresponse.Document;
import io.pivio.view.app.overview.detail.serverresponse.Internal;
import io.pivio.view.app.overview.detail.view.DocumentViewModel;
import io.pivio.view.app.overview.detail.view.ProvidesModel;
import io.pivio.view.app.overview.detail.view.ServiceDependencyViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
public class DetailService {

    @Autowired
    PivioServerConnector pivioServerConnector;

    @Autowired
    ServiceListGenerator serviceListGenerator;

    public DocumentViewModel getDetail(String id) {

        Document document = new Document();
        try {
            document = pivioServerConnector.getDocumentById(id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // TODO
        }

        return mapViewModel(document);
    }

    private DocumentViewModel mapViewModel(Document document) {
        DocumentViewModel result = new DocumentViewModel(document);
        List<ServiceIdShortName> allServices = pivioServerConnector.getAllServices();
        for (ProvidesModel provide : result.service.provides) {
            provide.setDependentServices(allServices, document.short_name);
        }

        Map<String, String> serviceNameMap = serviceListGenerator.getServiceNameMap();

        if (document.service != null && document.service.depends_on != null) {
            for (Internal internalDependency : document.service.depends_on.internal) {
                String serviceDisplayName = getServiceDisplayName(internalDependency);
                String linkId = "";
                if (serviceNameMap.containsKey(serviceDisplayName)) {
                    linkId = serviceNameMap.get(serviceDisplayName);
                }
                result.service.internalServiceDependencies.add(new ServiceDependencyViewModel(linkId, serviceDisplayName));
            }
        }
        return result;
    }

    private String getServiceDisplayName(Internal internalDependency) {
        String serviceDisplayName = internalDependency.service_name;
        if (serviceDisplayName.equals("")) {
            serviceDisplayName = internalDependency.short_name + " : " + internalDependency.port;
        }
        if (serviceDisplayName.equals("")) {
            serviceDisplayName = "UNKNOWN";
        }
        return serviceDisplayName;
    }
}
