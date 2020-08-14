package com.learning.api.dataProviders.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.learning.api.configuation.ResourceName;
import com.learning.api.configuation.TestConfiguration;
import com.learning.api.dataModel.DataProviderBaseModel;
import com.learning.api.resource.base.ResourceBase;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BaseDataProvider {

    private static final String TEST_DATA_LOCATION = "/TestData/";

    private static String getFileExtension() {
        return ".yml";
    }

    @SuppressWarnings("rawtypes")
    public static Object loadDataFromYml(ResourceName resource, Class deserializerClass) {
        Object result;
        try {
            result = BaseDataProvider.deserializeToObject(resource, deserializerClass);
        } catch (IOException e) {
            throw new RuntimeException("Data Load Failed!!", e);
        }

        DataProviderBaseModel[][] dpbmList = (DataProviderBaseModel[][]) result;
        ArrayList<Object[]> enabledList = new ArrayList<>();
        for (DataProviderBaseModel[] dpbm : dpbmList) {
            List<String> testCategory = dpbm[0].getTestCategory();
            if (testCategory.contains(TestConfiguration.getSuiteToExecute())) {
                enabledList.add(dpbm);
            }
        }

        Object[][] dpArray = (Object[][]) Array.newInstance(result.getClass().getComponentType(), enabledList.size());
        for (int i = 0; i < enabledList.size(); i++) {
            dpArray[i] = enabledList.get(i);
        }
        return dpArray;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static DataProviderBaseModel[][] deserializeToObject(ResourceName resourceName, Class containerClass) throws IOException {
        String resourceFile = resourceName + getFileExtension();
        try (InputStream templateStream = ResourceBase.class.getResourceAsStream(TEST_DATA_LOCATION + resourceFile)) {
            if (templateStream == null)
                throw new IOException("Invalid Resource Name: " + resourceName);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            return (DataProviderBaseModel[][]) mapper.readValue(templateStream, containerClass);
        }
    }

}