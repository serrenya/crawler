package util;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class PropertiesReader {
    private static Logger logger = LogManager.getLogger(PropertiesReader.class);

    public static PropertiesConfiguration getProperties() {
        PropertiesConfiguration properties = null;
        try {
            properties = new Configurations().properties(new File("application.properties"));
        } catch (ConfigurationException e) {
            logger.info("getProperties() {}", e.getMessage());
        }
        return properties;
    }
}
