package io.pivio.view.app.overview.detail;

import io.pivio.view.PivioServerConnector;
import io.pivio.view.app.overview.detail.serverresponse.Provides;
import io.pivio.view.app.overview.detail.serverresponse.Service;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceListGeneratorTest {

    PivioServerConnector pivioServerConnectorMock;
    ServiceListGenerator serviceListGenerator;
    ArrayList<ServiceIdShortName> serviceIdShortNames;

    @Before
    public void setup() {
        serviceListGenerator = new ServiceListGenerator();
        pivioServerConnectorMock = mock(PivioServerConnector.class);
        serviceListGenerator.pivioServerConnector = pivioServerConnectorMock;
        serviceIdShortNames = new ArrayList<>();
        setupTestData();
    }

    @Test
    public void testSize() throws Exception {
        when(pivioServerConnectorMock.getAllServices()).thenReturn(serviceIdShortNames);
        Map<String, String> serviceNameMap = serviceListGenerator.getServiceNameMap();
        assertThat(serviceNameMap.keySet()).hasSize(8);
    }

    @Test
    public void testServiceName() throws Exception {
        when(pivioServerConnectorMock.getAllServices()).thenReturn(serviceIdShortNames);
        Map<String, String> serviceNameMap = serviceListGenerator.getServiceNameMap();
        assertThat(serviceNameMap.keySet()).contains("BServiceName");
    }

    @Test
    public void testShortNamePort() throws Exception {
        when(pivioServerConnectorMock.getAllServices()).thenReturn(serviceIdShortNames);
        Map<String, String> serviceNameMap = serviceListGenerator.getServiceNameMap();
        assertThat(serviceNameMap.keySet()).contains("bbb_short_name : justBPort");
    }

    private void setupTestData() {
        Service serviceA = generateServiceA();
        Service serviceB = generateServiceB();
        serviceIdShortNames.add(new ServiceIdShortName(serviceA, "AAA", "aaa_short_name"));
        serviceIdShortNames.add(new ServiceIdShortName(serviceB, "BBB", "bbb_short_name"));
    }

    private Service generateServiceB() {
        Service service = new Service();
        service.provides.add(generateProvides("BServiceName", null));
        service.provides.add(generateProvides(null, "justBPort"));
        service.provides.add(generateProvides("SecondBServiceName", null));
        return service;
    }

    private Service generateServiceA() {
        Service service = new Service();
        service.provides.add(generateProvides("justAServiceName", null));
        service.provides.add(generateProvides("aSecondServiceName", null));
        service.provides.add(generateProvides(null, "justAPort"));
        return service;
    }

    private Provides generateProvides(String serviceName, String port) {
        Provides provides = new Provides();
        provides.service_name = serviceName;
        provides.port = port;
        return provides;
    }

}