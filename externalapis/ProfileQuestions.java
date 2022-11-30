package com.certain.External.service.v1;

import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.ProfileObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;

@SuppressWarnings("all")
public class ProfileQuestions extends CertainAPIBase {
    String accountCode;
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
    }

    private final ProfileObjSvc profileObjSvc = new ProfileObjSvc();
    // --Commented out by Inspection (4/6/2016 2:25 PM):String eventCode = USER_EVENT_CODE;

    @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForAccount() throws Exception {
        TestCase testCase = profileObjSvc.getProfileQuestions(accountCode, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForInvalidAccount() throws Exception {
        TestCase testCase = profileObjSvc.getProfileQuestions("InvalidAccount", null, null, 0, 0);
        if (testCase.getStatusCode() == 403) {
            Reporter.log("Got proper error message 'Resource Access not validated' ", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, "expected 403 but actual " + testCase.getSessionCode());
    }

    @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForAccountUsingMaxResult() throws Exception {
        TestCase testCase = profileObjSvc.getProfileQuestions(accountCode, null, null, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForAccountUsingStartIndex() throws Exception {
        TestCase testCase = profileObjSvc.getProfileQuestions(accountCode, null, null, 0, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForAccountUsingMaxResultWithStartIndex() throws Exception {
        TestCase testCase = profileObjSvc.getProfileQuestions(accountCode, null, null, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

   /* @org.testng.annotations.Test(groups = "ProfileQuestions")
    public void testGETProfileQuestionsForAccountUsingSearchFilter() throws Exception{
        TestCase testCase = profileObjSvc.getProfileQuestions(ACCOUNT_CODE, "eventCode", USER_EVENT_CODE, 0, 0);
        if(testCase.isPassed()){
            Reporter.log(testCase.getMessage(),true);
            Assert.assertTrue(true);
        }else if(testCase.getStatusCode() == 404){
            Reporter.log("No profile questions found for the eventCode  "+USER_EVENT_CODE,true);
            Assert.assertTrue(true);
        }else Assert.assertTrue(false,testCase.getMessage());
    }*/


}
