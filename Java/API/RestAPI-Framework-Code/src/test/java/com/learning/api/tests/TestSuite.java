package com.learning.api.tests;

import com.learning.api.configuation.TestConfiguration;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class TestSuite {

    private static final Logger log = LogManager.getLogger(TestSuite.class);

    @BeforeSuite
    public void setUpSuite() {

    }

}
