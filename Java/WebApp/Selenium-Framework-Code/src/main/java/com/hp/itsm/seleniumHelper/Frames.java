package com.hp.itsm.seleniumHelper;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Frames {

    @Autowired
    public Wait wait;

    public void switchTo(final String name) {
        wait.wait(ExpectedConditions.frameToBeAvailableAndSwitchToIt(name));
    }
}
