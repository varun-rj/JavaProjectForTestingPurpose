package com.learning.api.resource.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.learning.api.configuation.ResourceName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public class ResourceBase extends HashMap<String, Object> {

    private static final Logger LOGGER = LogManager.getLogger(ResourceBase.class);
    public static final String TEMPLATES_DIRECTORY = "templates";

    public static String getFileExtension() {
        return ".json";
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ResourceBase loadTemplate(ResourceName resourceName, Class containerClass) {
        String resourceFile = resourceName + getFileExtension();
        try (InputStream templateStream = ResourceBase.class.getResourceAsStream("/" + TEMPLATES_DIRECTORY + "/" + resourceFile)) {
            if (templateStream == null)
                throw new IOException("Invalid Resource Name: " + resourceName);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return (ResourceBase) mapper.readValue(templateStream, containerClass);
        } catch (IOException e) {
            LOGGER.error("Unable to load the template: [{}]", e.getStackTrace());
        }
        return null;
    }

    public static String getContentType() {
        return "application/json";
    }

}
