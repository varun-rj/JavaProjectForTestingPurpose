package com.learning.api.services.base;

import com.learning.api.resource.base.ResourceBase;
import io.restassured.filter.log.LogDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

public abstract class ResourceServiceBase<T extends ResourceBase> {

    private static final Logger LOGGER = LogManager.getLogger(ResourceServiceBase.class);

    public abstract String getEndpoint();

    @SuppressWarnings("all")
    public T create(T resource) {

        return given().
                contentType(resource.getContentType()).
                body(resource).
                when().log().ifValidationFails(LogDetail.ALL).
                post(getEndpoint()).
                then().log().ifValidationFails(LogDetail.ALL).
                statusCode(200).
                extract().body().as((Class<T>) resource.getClass());
    }
}
