package com.hp.itsm.seleniumHelper;

import com.hp.itsm.testExecution.WebApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeleniumHelper {

    private static final Logger LOGGER = LogManager.getLogger(SeleniumHelper.class);
    public WebDriver driver;

    public SeleniumHelper() {
        driver = WebApp.getDriver();
    }

    @Autowired
    public Wait wait;

    public WebElement findElement(final By locator) {
        return driver.findElement(locator);
    }

    public void click(final By locator) {
        LOGGER.info("Performing click action ... ");
        findElement(locator).click();
    }

    public void waitAndClick(final By locator) {
        WebElement element = wait.elementToBeClickable(locator);
        LOGGER.info("Performing click action ... ");
        element.click();
    }

    public void type(final By locator, String keyword) {
        LOGGER.info(String.format("Typing %s ...",keyword));
        findElement(locator).sendKeys(keyword);
    }

    public void waitAndType(final By locator, String keyword) {
        wait.elementToBeClickable(locator);
        LOGGER.info(String.format("Typing %s ...",keyword));
        findElement(locator).sendKeys(keyword);
    }

    public void typeAndEnter(final By locator, String keyword) {
        LOGGER.info(String.format("Typing %s ...",keyword));
        findElement(locator).sendKeys(keyword, Keys.ENTER);
    }

    public void waitTypeAndEnter(final By locator, String keyword) {
        wait.elementToBeClickable(locator);
        LOGGER.info(String.format("Typing %s ...",keyword));
        findElement(locator).sendKeys(keyword, Keys.ENTER);
    }

    public String getText(final By locator) {
        WebElement element = wait.visibilityOfElementLocated(locator);
        return element.getText();
    }

    public String getAttribute(final By locator, String attribute) {
        WebElement element = wait.visibilityOfElementLocated(locator);
        return element.getAttribute(attribute);
    }




}
