package com.learning.api.tests;

import com.learning.api.configuation.TestConfiguration;
import com.learning.api.dataModel.DataProviderBaseModel;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.ValidatableResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.internal.matcher.xml.XmlXsdMatcher.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class APITestBase {

    private static final Logger LOGGER = LogManager.getLogger(APITestBase.class);
    protected TestConfiguration config = TestConfiguration.getInstance();

    protected void validateProperties(ValidatableResponse response, List<HashMap<String, Object>> propertyMapArray) {
        if (propertyMapArray != null) {
            for (HashMap<String, Object> propertyMap : propertyMapArray) {
                String property = (String) propertyMap.get(DataProviderBaseModel.PROPERTY_ATTRIBUTE);
                Object value = propertyMap.get(DataProviderBaseModel.PROPERTY_VALUE);
                validateProperty(response, property, value);
            }
        }
    }

    private void validateProperty(ValidatableResponse response, String property, Object value) {
        response.body(property, equalTo(value));
    }

    private void validateTextProperty(ValidatableResponse response, String value) {
        Assert.assertTrue(response.extract().asString().contains(value));
    }

    protected void validateTextProperties(ValidatableResponse response, ArrayList<HashMap<String, Object>> propertyMapArray) {
        if (propertyMapArray != null) {
            for (HashMap<String, Object> propertyMap : propertyMapArray) {
                String validateString = (String) propertyMap.get(DataProviderBaseModel.PROPERTY_ATTRIBUTE);
                validateTextProperty(response, validateString);
            }
        }
    }

    protected void validateXmlProperties(ValidatableResponse response, List<HashMap<String, Object>> propertyMapArray) {
        if (propertyMapArray != null) {
            for (HashMap<String, Object> propertyMap : propertyMapArray) {
                Object value = propertyMap.get(DataProviderBaseModel.PROPERTY_VALUE);
                String xpath = (String) propertyMap.get(DataProviderBaseModel.XML_PATH);
                validateXmlProperty(response, xpath, value);
            }
        }
    }

    private void validateXmlProperty(ValidatableResponse response, String xmlPath, Object value) {
        if (value instanceof Integer) {
            Integer val = Integer.parseInt(response.extract().body().xmlPath().get(xmlPath));
            Assert.assertEquals((Integer) value, val);
        } else if (value instanceof String) {
            Assert.assertEquals((String) value, response.extract().body().xmlPath().get(xmlPath));
        }
    }


    protected void validateJSONSchema(ValidatableResponse validatableResponse, String schemaLocation) {
        if (schemaLocation != null && !schemaLocation.isEmpty())
            validatableResponse.body(matchesJsonSchemaInClasspath(schemaLocation))
                    .log().ifValidationFails(LogDetail.ALL);
    }

    protected void validateXMLSchema(ValidatableResponse validatableResponse, String schemaLocation) {
        if (schemaLocation != null && !schemaLocation.isEmpty())
            validatableResponse.body(matchesXsdInClasspath(schemaLocation))
                    .log().ifValidationFails(LogDetail.ALL);
    }

}
