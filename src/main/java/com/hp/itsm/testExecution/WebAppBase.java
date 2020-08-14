package com.hp.itsm.testExecution;


import com.hp.itsm.configurations.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public abstract class WebAppBase {

    private static final Logger LOGGER = LogManager.getLogger(WebAppBase.class);

    public static  WebDriver driver;

    public static WebDriver getDriver() {
        if(driver == null) {
            LOGGER.info("Initializing WebDriver");
            driver = DriverManager.getLocalDriver();
            return driver;
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            LOGGER.info("Quiting Driver");
            driver.quit();
        }
    }


}
