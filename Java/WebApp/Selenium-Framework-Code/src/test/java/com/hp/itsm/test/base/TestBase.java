package com.hp.itsm.test.base;

import com.hp.itsm.configurations.test.TestConfiguration;
import com.hp.itsm.reporting.TestReport;
import com.hp.itsm.testExecution.WebApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

@Listeners({TestReport.class})
@ContextConfiguration(classes = TestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class TestBase extends AbstractTestNGSpringContextTests {

    protected final String DAAS_PROVIDER = "DassUIProvider";

    @Autowired
    protected TestConfiguration configuration;
    @Autowired
    protected WebApp webApp;

    @DataProvider(name = DAAS_PROVIDER)
    public Object[][] loadLoginPage() {
        return new Object[][] {{ webApp }};
    }

    @AfterMethod(alwaysRun = true, enabled = true)
    public void cleanUp() {
        WebApp.quitDriver();
    }
}
