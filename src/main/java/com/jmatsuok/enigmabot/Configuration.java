package com.jmatsuok.enigmabot;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private Map<String, String> properties;
    private Logger logger = LoggerFactory.getLogger(Configuration.class);

    public Configuration(String path) {
        BasicConfigurator.configure();
        properties = new HashMap<String, String>();
        try {
            logger.debug("Initializing configuration from path: " + path);
            initialize(Paths.get(path).toFile());
        } catch (Exception e) {
            logger.error("Caught Exception", e.getMessage());
        }
    }

    private void initialize(File config) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(config));
            String line = reader.readLine();
            while (line != null) {
                String[] keyval = line.split("=");
                properties.put(keyval[0], keyval[1]);
                line = reader.readLine();
            }
        } catch (Exception e) {
            logger.error("Caught Exception", e);
        }
    }

    public String getConfigOption(String key) {
        return properties.get(key);
    }
}
