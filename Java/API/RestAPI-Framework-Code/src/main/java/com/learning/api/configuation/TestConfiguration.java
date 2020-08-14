package com.learning.api.configuation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Getter
public class TestConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(TestConfiguration.class);
    private static final String ENV = "env";
    private static final String DEV = "dev";
    private static final String SUITE = "suite";
    private static final String environment =  System.getProperty(ENV);
    private static final String suiteToExecute = System.getProperty(SUITE);
    private static final TestConfiguration config = new TestConfiguration().loadProperties();
    public static final String PROPERTIES_DIR = "src/test/resources/Properties";

    public static String getEnvironment() {
        return environment;
    }
    public static String getSuiteToExecute() {
        return suiteToExecute;
    }

    private TestConfiguration () {}

    public static TestConfiguration getInstance(){
        return config;
    }

    public TestConfiguration loadProperties() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        TestConfiguration property = null;
        try {
            File file = new File(fetchPropertyFilePath());
            property = objectMapper.readValue(file, TestConfiguration.class);
        } catch (IOException e) {
            LOGGER.error(" Unable To Open Property File, Error message: [{}] ", e.getMessage());
        }
        return property;
    }

    private String fetchPropertyFilePath() {
        if (getEnvironment() == null)
            throw new IllegalArgumentException("Command Line Argument Must Contain Environment Parameter ex: [-Denv=dev]");
        if (getEnvironment().equalsIgnoreCase(DEV)) {
            LOGGER.info("Selected environment for execution is: [{}]", TestConfiguration::getEnvironment );
            return loadConfigFile(ResourceName.DEV_ENV);
    }
        LOGGER.info("Selected environment for execution is: [{}]", TestConfiguration::getEnvironment );
        return loadConfigFile((ResourceName.TEST_ENV));
    }

    private String loadConfigFile(ResourceName resourceName){
        String fileName = resourceName.toString() + getFileExtension();
        return Paths.get(PROPERTIES_DIR, fileName).toAbsolutePath().toString();
    }

    private String getFileExtension(){
        return ".yml";
    }

}
