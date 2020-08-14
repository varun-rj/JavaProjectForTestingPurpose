package com.hp.itsm.testExecution;

import com.hp.itsm.configurations.test.TestConfiguration;
import com.hp.itsm.pageObject.pages.LoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebApp extends WebAppBase {

    @Autowired
    public TestConfiguration configuration;
    @Autowired
    public LoginPage loginPage;



    public LoginPage getDAASLoginPage() {
        getDriver().get(configuration.getBaseUri());
        return loginPage;
    }

}
