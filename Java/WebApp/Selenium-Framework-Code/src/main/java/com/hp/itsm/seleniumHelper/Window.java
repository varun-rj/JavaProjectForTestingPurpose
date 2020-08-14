package com.hp.itsm.seleniumHelper;

import com.hp.itsm.constants.TimeOut;
import com.hp.itsm.testExecution.WebApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Window {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);
    public WebDriver driver;

    public Window() {
        driver = WebApp.getDriver();
    }

    @Autowired
    public Wait wait;

    public void refresh() {
        driver.navigate().refresh();
        wait.pageToLoad();
    }

    public void navigateTo(String uri) {
        driver.navigate().to(uri);
        wait.pageToLoad();
    }

    public void refreshUntilNonNullValueInElementLocated(final By locator, boolean alertFlag, String frameName) {
        wait.wait(TimeOut.MAX_TIMEOUT_IN_SEC, TimeOut.MAXIMUM_POLL_TIMEOUT_MS,
                wait.reloadUntilNonNullValueInElementLocated(locator, alertFlag, frameName));
    }
}
