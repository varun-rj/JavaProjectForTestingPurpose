package com.hp.itsm.configurations.driver;

public enum Browser  {

    FIREFOX("Firefox"),
    CHROME("Chrome");

    private String browserName;

    Browser(String browser) {
        this.browserName = browser;
    }

    public String getName() {
        return browserName;
    }

    public static Browser fromString(String browserString) {
        for (Browser browser : Browser.values()) {
            if (browser.getName().equalsIgnoreCase(browserString)) {
                return browser;
            }
        }
        throw new IllegalArgumentException(browserString + " is not a valid name for a Browser.");
    }

    @Override
    public String toString() {
        return browserName;
    }
}
