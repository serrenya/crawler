package util;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PropertiesReader {
    private static Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

    public static PropertiesConfiguration getProperties() {
        try {
            return new Configurations().properties(new File("application.properties"));
        } catch (ConfigurationException e) {
            logger.info("getProperties() {}", e.getMessage());
        }
        return null;
    }
}
