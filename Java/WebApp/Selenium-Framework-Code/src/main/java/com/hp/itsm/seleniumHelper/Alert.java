package com.hp.itsm.seleniumHelper;

import com.hp.itsm.testExecution.WebApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Alert {

    private static final Logger LOGGER = LogManager.getLogger(Alert.class);
    public WebDriver driver;

    public Alert() {
        driver = WebApp.getDriver();
    }

    @Autowired
    public Wait wait;

    public boolean isAlertPresent(){
        boolean foundAlert = false;
        WebDriverWait webDriverWait = wait.getWebDriverWait();
        try {
            webDriverWait.until(ExpectedConditions.alertIsPresent());
            foundAlert = true;
        } catch (TimeoutException eTO) {
            foundAlert = false;
        }
        return foundAlert;
    }

    public void acceptAlert() {
        if (isAlertPresent()) {
            driver.switchTo().alert().accept();
            LOGGER.info("Alert Accepted");
        } else {
            LOGGER.warn("No Alert Present");
        }
    }

}
