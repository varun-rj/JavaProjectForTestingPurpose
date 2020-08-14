package com.hp.itsm.seleniumHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
public class JavaScriptExecutor {

    private static final Logger LOGGER = LogManager.getLogger(JavaScriptExecutor.class);

    public Object execute(WebDriver webDriver, String javascript) {
        LOGGER.info("executeJavaScript executing:" + javascript);

        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        Object retVal = js.executeScript(javascript);

        LOGGER.info("executeJavaScript returning:" + retVal);
        return retVal;
    }

}
