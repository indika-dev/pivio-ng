package io.pivio.view.configuration;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConfigReader {

    private final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    @Value(value = "${config}")
    String configFile;

    @Autowired
    ServerConfig serverConfig;

    @PostConstruct
    void readArguments() {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            objectMap = readYamlFile(configFile);
        } catch (FileNotFoundException e) {
            log.info("Config file: {} not found.", configFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (objectMap.containsKey("pages")) {
            serverConfig.pages = (List) objectMap.get("pages");
        }

        serverConfig.apiAddress = getValueFromConfigMapOrEnvVariable(objectMap, "api", serverConfig.apiAddress, "PIVIO_SERVER");
        serverConfig.jsApiAddress = getValueFromConfigMapOrEnvVariable(objectMap, "js_api", serverConfig.jsApiAddress, "PIVIO_SERVER_JS");
        serverConfig.mainUrl = getValueFromConfigMapOrEnvVariable(objectMap, "mainurl", serverConfig.mainUrl, "PIVIO_VIEW");

        addMainUrlToLocalUrls();

        log.info("Using config: " + serverConfig.toString());

    }

    private void addMainUrlToLocalUrls() {
        for (Object page : serverConfig.pages) {
            if (page instanceof HashMap) {
                HashMap p = ((HashMap) page);
                if (p.containsKey("url")) {
                    String url = (String) p.get("url");
                    if (url.startsWith("/")) {
                        p.put("url", serverConfig.mainUrl + p.get("url"));
                    }
                }
            }
        }
    }

    private String getValueFromConfigMapOrEnvVariable(Map<String, Object> objectMap, String key, String defaultValue, String envVariable) {
        String result = defaultValue;
        if (objectMap.containsKey(key)) {
            result = (String) objectMap.get(key);
            log.debug("Pivio View Server Config: Reading {} = {} from config file.", key, result);
        }

        String propFromEnvironment = System.getenv(envVariable);
        if (propFromEnvironment != null) {
            result = propFromEnvironment;
            log.debug("Pivio View Server Config: Reading {} = {} from environment variables.", key, propFromEnvironment);
        }

        return result;
    }

    Map<String, Object> readYamlFile(String yamlFile) throws FileNotFoundException, UnsupportedEncodingException {
        log.info("Using config file: ", configFile);
        File file = new File(yamlFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        YamlReader yamlReader = new YamlReader(in);
        Object object = null;
        try {
            object = yamlReader.read();
        } catch (YamlException e) {
            log.error("Error reading  yaml file {}.", yamlFile);
            e.printStackTrace();
        }

        return (Map<String, Object>) object;
    }
}
