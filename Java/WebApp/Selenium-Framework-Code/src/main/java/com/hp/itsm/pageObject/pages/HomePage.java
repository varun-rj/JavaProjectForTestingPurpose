package com.hp.itsm.pageObject.pages;

import com.hp.itsm.pageObject.base.PageBase;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HomePage extends PageBase {

    private final By FILTER = By.name("filter");
    private final By CREATE_NEW = By.xpath("//a[@href='sn_customerservice_case_list.do?sysparm_userpref_module=4ddab177c342310015519f2974d3ae87&sysparm_query=active%3Dtrue%5Eassigned_to%3Djavascript%3AgetMyAssignments%28%29%5EEQ&active=true^assigned_to=javascript:gs.user_id()&sysparm_clear_stack=true']");

    private final String CASE_PAGE = "https://hptest.service-now.com/nav_to.do?uri=%2Fsn_customerservice_case_list.do%3Fsysparm_query%3D%26sysparm_first_row%3D1%26sysparm_view%3Dcase";
    public static final String MAIN_COMPONENT_FRAME = "gsft_main";


    @Autowired
    public CasePage casePage;

    public CasePage openCasePage() {
        wait.pageToLoad();
        seleniumHelper.waitAndType(FILTER, "Cases");
        seleniumHelper.waitAndClick(CREATE_NEW);
        frames.switchTo(MAIN_COMPONENT_FRAME);
        return casePage;
    }





}
