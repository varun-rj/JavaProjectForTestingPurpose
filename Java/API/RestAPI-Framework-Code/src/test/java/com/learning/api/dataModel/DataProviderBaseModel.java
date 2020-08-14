package com.learning.api.dataModel;

import lombok.Getter;
import java.nio.file.Paths;
import java.util.List;

@Getter
public class DataProviderBaseModel {

    private static final String SCHEMA_LOCATION = "Schemas/";
    public static final String PROPERTY_ATTRIBUTE = "propertyAttribute";
    public static final String PROPERTY_VALUE = "propertyValue";
    public static final String XML_PATH = "xmlPath";

    public List<String> testCategory;
    public String schema;

    public String getSchemaLocation() {
        return Paths.get(SCHEMA_LOCATION, schema).toAbsolutePath().toString() + getFileExtension();
    }

    private String getFileExtension(){
        return ".json";
    }


}
