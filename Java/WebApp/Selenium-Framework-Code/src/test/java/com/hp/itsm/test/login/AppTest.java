package com.hp.itsm.test.login;

import com.hp.itsm.constants.TestGroups;
import com.hp.itsm.pageObject.pages.CasePage;
import com.hp.itsm.pageObject.pages.HomePage;
import com.hp.itsm.test.base.TestBase;
import com.hp.itsm.reporting.Tracking;
import com.hp.itsm.testExecution.WebApp;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppTest extends TestBase {

    @Tracking(TC = 2323)
    @Test(dataProvider = DAAS_PROVIDER, groups = {TestGroups.REGRESSION})
    public void createCase(WebApp webApp) {
        HomePage homePage = webApp.getDAASLoginPage().login(configuration.getUserName(), configuration.getPassword());
        CasePage casePage = homePage.openCasePage();
        casePage.createNewCase();
        String caseNo = casePage.fetchCaseNumber();
        casePage.openCase(caseNo);
        String correlationID = casePage.getCorrelationID();
        Assert.assertTrue(correlationID.isEmpty(), String.format(" Correlation ID is not assigned to caseNo: %s ", caseNo));
    }

}
