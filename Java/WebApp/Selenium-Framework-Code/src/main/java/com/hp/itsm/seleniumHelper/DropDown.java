package com.hp.itsm.seleniumHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DropDown {

    @Autowired
    public SeleniumHelper seleniumHelper;

    public void selectByValue(final By locator, String value) {
        Select select = new Select(seleniumHelper.findElement(locator));
        select.selectByValue(value);
    }

    public void selectByIndex(final By locator, int value) {
        Select select = new Select(seleniumHelper.findElement(locator));
        select.selectByIndex(value);
    }

    public void selectsByVisibleText(final By locator, String text) {
        Select select = new Select(seleniumHelper.findElement(locator));
        select.selectByVisibleText(text);
    }
}
