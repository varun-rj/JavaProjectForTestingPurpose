package com.hp.itsm.seleniumHelper;

import com.hp.itsm.constants.TimeOut;
import com.hp.itsm.testExecution.WebApp;
import com.hp.itsm.testExecution.WebAppBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Wait {

    private static final Logger LOGGER = LogManager.getLogger(Wait.class);

    private WebDriver driver;

    public Wait() {
        driver = WebApp.getDriver();
    }

    @Autowired
    public JavaScriptExecutor javaScriptExecutor;
    @Autowired
    public Alert alert;
    @Autowired
    public Frames frames;


    public void wait(ExpectedCondition<?> ec) {
        getWebDriverWait().until(ec);
    }

    public void wait(int timeOutInSeconds, int pollTimeInMilliSeconds, ExpectedCondition<?> ec) {
        getWebDriverWait(timeOutInSeconds, pollTimeInMilliSeconds).until(ec);
    }

    private ExpectedCondition<Boolean> pageReady() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                boolean result = false;
                String readyState = "";
                try {
                    readyState = (String) javaScriptExecutor.execute(driver, "return document.readyState;");
                } catch (WebDriverException e) {
                    LOGGER.info("Caught " + e);
                }

                if ("complete".equals(readyState)) {
                    result = true;
                }
                return result;
            }
        };
    }

    public WebDriverWait getWebDriverWait() {
        return new WebDriverWait(driver, TimeOut.DEFAULT_NAVIGATION_TIMEOUT_SEC, TimeOut.DEFAULT_POLL_TIMEOUT_MS);
    }

    public WebDriverWait getWebDriverWait(int timeOutInSeconds, int pollTimeInMilliSeconds) {
        return new WebDriverWait(driver, timeOutInSeconds, pollTimeInMilliSeconds);
    }

    public void pageToLoad() {
        getWebDriverWait().until(pageReady());
    }

    public WebElement elementToBeClickable(final By locator) {
        LOGGER.info(String.format("Waiting for locator: %s, to be clickable ...", locator));
        return getWebDriverWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement visibilityOfElementLocated(final By locator) {
        LOGGER.info(String.format("Waiting for locator: %s, visibility ...", locator));
        return getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement presenceOfElementLocated(final By locator) {
        LOGGER.info(String.format("Waiting for presence of locator:  %s ...", locator));
        return getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void textToBePresentInElementLocated(final By locator, final String text) {
        LOGGER.info(String.format("Waiting for the text to br present in the locator: %s ...", locator));
        getWebDriverWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public void invisibilityOfElementWithText(final By locator, final String text) {
        LOGGER.info(String.format("Waiting for the text to be invisible in the locator %s ...", locator));
        getWebDriverWait().until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }

    public void attributeToBe(final By locator, final String attribute, final String value) {
        LOGGER.info(String.format("Waiting for locator: %s, attribute: %s, value to be: %s ...", locator, attribute, value));
        getWebDriverWait().until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    public void attributeContains(final By locator, final String attribute, final String value) {
        LOGGER.info(String.format("Waiting for locator: %s, attribute: %s, to contain value: %s ...", locator, attribute, value));
        getWebDriverWait().until(ExpectedConditions.attributeContains(locator, attribute, value));
    }

    public ExpectedCondition<Boolean> reloadUntilNonNullValueInElementLocated(final By locator, boolean alertFlag, String frameName) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    String elementText = driver.findElement(locator).getAttribute("value");
                    boolean flag = elementText.isEmpty();
                    if (flag)
                        driver.navigate().refresh();
                    if (alertFlag)
                        alert.acceptAlert();
                    frames.switchTo(frameName);
                    return flag;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }
        };
    }

}
