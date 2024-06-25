package io.pivio.view.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ConfigurationController {

    @Autowired
    ServerConfig serverConfig;

    @RequestMapping(value = "/config", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ServerConfig showConfig() {
        return serverConfig;
    }

}
