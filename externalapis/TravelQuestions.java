package com.certain.External.service.v1;

import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

@SuppressWarnings("all")
public class TravelQuestions extends CertainAPIBase {

    private String eventCode;
    private String accountCode;
    private RegistrationObjSvc registrationObjectSvc = new RegistrationObjSvc();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForAccount() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the ACCOUNT_CODE [" + ACCOUNT_CODE + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForAccountUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, null, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the ACCOUNT_CODE [" + ACCOUNT_CODE + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForAccountUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, null, null, null, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the ACCOUNT_CODE [" + ACCOUNT_CODE + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-travel-qtn-dp1")
    private Object[][] filtersDP1() throws Exception {
        Object[][] objects = new Object[1][2];
        objects[0][0] = "eventCode";
        objects[0][1] = eventCode;
        return objects;
    }

    @org.testng.annotations.Test(dataProvider = "get-travel-qtn-dp1", enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForAccountUSingSearchFilter(String filter, Object value) throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-travel-qtn-dp1", enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForAccountUSingSearchFilterUsingMaxResults(String filter, String value) throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, null, filter, value, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForEvent() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, eventCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the eventCode [" + eventCode + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForEventUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, eventCode, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the eventCode [" + eventCode + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "TravelQuestions")
    public void testGETTravelQuestionsForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getTravelQuestions(ACCOUNT_CODE, eventCode, null, null, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No transactions found for the eventCode [" + eventCode + "]", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}
