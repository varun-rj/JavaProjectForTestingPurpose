package com.hp.itsm.pageObject.pages;

import com.hp.itsm.pageObject.base.PageBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class CasePage extends PageBase {

    private static final Logger LOGGER = LogManager.getLogger(CasePage.class);

    private final By NEW_BUTTON = By.id("sysverb_new");
    private final By CATEGORY_DROPDOWN = By.id("sn_customerservice_case.category");
    private final By SERIAL_NUMBER = By.id("sys_display.sn_customerservice_case.asset");
    private final By CONTACT_NAME = By.id("sys_display.sn_customerservice_case.u_contact_person");
    private final By SHORT_DESC = By.id("sn_customerservice_case.short_description");
    private final By DETAILED_DESC = By.id("sn_customerservice_case.description");
    private final By SUBMIT_BTN = By.id("sysverb_insert");
    private final By CASE_NUMBER_OUTPUT_MSG = By.xpath("//div[@id='output_messages']/div/div/div[@class='outputmsg_text']");
    private final By LIST_VIEW_SEARCH_CASE = By.id("sn_customerservice_case_table_header_search_control");
    private final By CORRELATION_ID = By.id("sn_customerservice_case.correlation_id");

    public void createNewCase() {
        seleniumHelper.waitAndClick(NEW_BUTTON);
        dropDown.selectByValue(CATEGORY_DROPDOWN, "3");
        seleniumHelper.waitAndType(SERIAL_NUMBER, "3CQ43230TF");
        seleniumHelper.waitAndType(CONTACT_NAME, " Aaron O'Dell");
        seleniumHelper.waitAndType(SHORT_DESC, "SELENIUM TESTING");
        seleniumHelper.waitAndType(DETAILED_DESC, "Testing");
        seleniumHelper.waitAndClick(SUBMIT_BTN);
    }

    public String fetchCaseNumber() {

        String text = seleniumHelper.getText(CASE_NUMBER_OUTPUT_MSG);
        String[] notificationText = text.split(" ");
        String caseNo = "";
        for (String word : notificationText) {
            if (word.startsWith("CS")) {
                caseNo = word;
            }
        }

        LOGGER.info("Created Case No: {}", caseNo);

        return caseNo;
    }

    public void openCase(String id) {
        seleniumHelper.waitTypeAndEnter(LIST_VIEW_SEARCH_CASE, id);
        seleniumHelper.waitAndClick(By.linkText(id));
        LOGGER.info("Case No: {}, Opened", id);
    }

    public String getCorrelationID() {
        window.refreshUntilNonNullValueInElementLocated(CORRELATION_ID, true, HomePage.MAIN_COMPONENT_FRAME);
        String id = seleniumHelper.getAttribute(CORRELATION_ID, "value");
        return id;
    }

}