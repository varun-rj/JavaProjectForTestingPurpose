package com.hp.itsm.pageObject.pages;

import com.hp.itsm.pageObject.base.PageBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.KeyEvent;

@Component
public class LoginPage extends PageBase {

    private static final Logger LOGGER = LogManager.getLogger(LoginPage.class);

    @Autowired
    public HomePage homePage;

    private final By userNameTextBox = By.id("username");
    private final By passwordTextBox = By.id("password");
    private final By submitButton = By.id("next_button");

    public HomePage login(String userName, String password) {
        seleniumHelper.type(userNameTextBox, userName);
        seleniumHelper.waitAndClick(submitButton);

        ByPassWindowAuthentication();

        seleniumHelper.waitAndType(userNameTextBox, userName);
        seleniumHelper.typeAndEnter(passwordTextBox, password);
        return homePage;
    }


    private void ByPassWindowAuthentication() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(3000);
            robot.keyPress(KeyEvent.VK_TAB);
            Thread.sleep(1000);
            robot.keyPress(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            LOGGER.info(" error while closing authentication popup {} ", e.getStackTrace() );
        }
    }

}
