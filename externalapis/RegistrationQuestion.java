package com.certain.External.service.v1;

import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;

@SuppressWarnings("all")
public class RegistrationQuestion extends CertainAPIBase {

    final private String[] questionTypes = {"Text", "Textarea", "Radio", "Checkbox", "Select", "Select multiple", "Ticket",
            "Date", "Time", "Date-Time", "Integer", "Number", "File", "Image"};
    private RegistrationObjSvc registrationObjectSvc = new RegistrationObjSvc();
    private String eventCode;
    private String registrationCode;

    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        //ACCOUNT_CODE = ACCOUNT_CODE;
        String name = "REG4QTNS";
        registrationCode = createRegistration(eventCode, name, name + "@gmail.com", "Adobe", "Attendee");
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForAccount() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, null, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the ACCOUNT_CODE " + ACCOUNT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForAccountUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, null, null, null, null, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the ACCOUNT_CODE " + ACCOUNT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForAccountUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, null, null, null, null, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the ACCOUNT_CODE " + ACCOUNT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForEvent() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the eventCode " + eventCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForEventUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, null, null, null, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the eventCode " + eventCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, null, null, null, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the eventCode " + eventCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForRegistration() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, registrationCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the registrationCode " + registrationCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForRegistrationUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, registrationCode, null, null, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the registrationCode " + registrationCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForRegistrationUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, registrationCode, null, null, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the registrationCode " + registrationCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-questions-dp1")
    public Object[][] getReqQuestions() {
        Object[][] questionsFilter = new Object[questionTypes.length][2];
        int index = 0;
        for (String questionType : questionTypes) {
            questionsFilter[index][0] = "questionType";
            questionsFilter[index][1] = questionType;
            index++;
        }
        return questionsFilter;
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForAccountWithSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the ACCOUNT_CODE " + ACCOUNT_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForEventWithSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the eventCode " + eventCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-questions-dp1", enabled = true, groups = "RegistrationQuestions")
    public void testGETRegistrationQuestionForRegistrationEventWithSearchFilter(String filter, Object value) throws Exception {
        TestCase testCase = registrationObjectSvc.getRegistrationQuestions(ACCOUNT_CODE, eventCode, registrationCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No registration questions found for the registrationCode " + registrationCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}