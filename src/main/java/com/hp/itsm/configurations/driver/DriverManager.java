package com.hp.itsm.configurations.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverManager {

    public static final Browser DEFAULT_BROWSER = Browser.CHROME;
    private static final String BROWSER_PROPERTY = "selenium.browserName";

    private static Browser browser;

    private static Browser getBrowser() {

        if (browser == null) {
            String browserProp = System.getProperty(BROWSER_PROPERTY);
            if (browserProp == null || browserProp.isEmpty()) {
                System.setProperty("selenium.browserName", DEFAULT_BROWSER.toString());
                browserProp = DEFAULT_BROWSER.toString();
            }
            browser = Browser.fromString(browserProp);
        }
        return browser;
    }


    public static WebDriver getLocalDriver() {

        WebDriver driver = null;

        if (getBrowser() == Browser.FIREFOX) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        else if (getBrowser() == Browser.CHROME) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }
        else {
            throw new UnsupportedOperationException(String.format("This method does not support the '%s' browser.", getBrowser().getName()));
        }

        return driver;
    }


}
