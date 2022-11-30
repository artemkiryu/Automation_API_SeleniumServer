package com.certain.External.service.v1;

import com.certain.external.dto.registration.RegistrationObj;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;


@SuppressWarnings("all")
public class Exhibitor extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private List<RegistrationObj> exhibitorList = null;
    private RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private RegistrationObj registrationObj = new RegistrationObj();
    private String accountCode;
    private String eventCode;
    private String attendeeType = "Exhibitor";
    private String exhibitorPin;
    private String exhibitorRegCode, exhibitorRegCodeReq;
    private String[] includeList = {"registration_questions", "profile_questions"};
    private String[] orderBy = {
            "dateModified_asc", "dateModified_desc", "dateCreated_asc", "dateCreated_desc", "lastName_asc", "lastName_desc",
            "firstName_asc", "firstName_desc", "badgeName_asc", "badgeName_desc", "eventName_asc", "eventName_desc", "externalKey_asc",
            "externalKey_desc", "registrationCode_asc", "registrationCode_desc", "pkProfileId_asc", "pkProfileId_desc"
    };


    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;

        try {
            exhibitorList = testDataLoad.getExhibitorObjData(accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "getExhibitorIncludeList-dp1", parallel = false)
    public Object[][] getExhibitorIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @Test(dataProvider = "getExhibitorIncludeList-dp1", enabled = false)
    public void testPOSTExhibitorAndValidateDefaultQuestionsSCM32960(String includeList) throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = exhibitorList.get(0);
        registrationObj.setAttendeeTypeCode(attendeeType);
        registrationObj.setSalesID(null);
        registrationObj.getProfile().setPin(exhibitorPin);
        registrationObj.getProfile().setFirstName("EXH" + uniqueCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            exhibitorRegCodeReq = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCodeReq, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
        if (includeList.contains("registration_questions")) {
            registrationObj.getProfile().setProfileQuestions(null);
            registrationObj.setRegistrationQuestions(testDataLoad.getRegistrationQuestionsObj());
            testCase = registrationObjSvc.GETExhibitorWithDefaultAnswerValue(accountCode, eventCode, exhibitorRegCodeReq, new String[]{includeList}, registrationObj);
        } else {
            registrationObj.setRegistrationQuestions(null);
            registrationObj.getProfile().setProfileQuestions(testDataLoad.getRegProfileQuestionsObj());
            testCase = registrationObjSvc.GETExhibitorWithDefaultAnswerValue(accountCode, eventCode, exhibitorRegCodeReq, new String[]{includeList}, registrationObj);
        }
        if (testCase.isPassed()) {
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"postExhibitor", "ExhibitorObj"})
    public void testPOSTExhibitor() throws Exception {
        String uniqueCode = randomString(9);
        registrationObj = exhibitorList.get(0);
        registrationObj.setAttendeeTypeCode(attendeeType);
        exhibitorPin = uniqueCode;
        registrationObj.getProfile().setPin(exhibitorPin);
        registrationObj.getProfile().setFirstName("EXH" + uniqueCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj, expectedCondition, false);
        if (testCase.getStatusCode() == 200) {
            exhibitorRegCode = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postExhibitorRequired", "ExhibitorObj"})
    public void testPOSTExhibitorRequiredFieldsOnly() throws Exception {
        String uniqueCode = randomString(9);
        RegistrationObj registrationObj = exhibitorList.get(0);
        registrationObj.setAttendeeTypeCode(attendeeType);
        registrationObj.setSalesID(null);
        registrationObj.getProfile().setPin(exhibitorPin);
        registrationObj.getProfile().setFirstName("EXH" + uniqueCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            exhibitorRegCodeReq = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCodeReq, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = false, groups = {"postExhibitor-ProQuestions", "ExhibitorObj"}, priority = 2)
    public void testPOSTExhibitorWithProfileQuestions() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = exhibitorList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("User " + uniqueCode);
        registrationObj.getProfile().setProfileQuestions(testDataLoad.getRegProfileQuestionsObj());
        registrationObj.setRegistrationQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj, expectedCondition, false);
        if (testCase.isPassed()) {
            exhibitorRegCodeReq = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCodeReq, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = false, groups = {"postExhibitor-RegQuestions", "ExhibitorObj"}, priority = 2)
    public void testPOSTExhibitorWithRegistrationQuestions() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj1 = exhibitorList.get(0);
        registrationObj1.getProfile().setPin(uniqueCode);
        registrationObj1.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj1.getProfile().setFirstName("User " + uniqueCode);
        registrationObj1.setRegistrationQuestions(testDataLoad.getRegistrationQuestionsObj());
        registrationObj1.getProfile().setProfileQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj1, expectedCondition, false);
        if (testCase.isPassed()) {
            exhibitorRegCodeReq = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCodeReq, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = true, groups = {"getExhibitors", "RegistrationObj"}, dependsOnGroups = {"postExhibitor"})
    public void testGETExhibitorDetailsByCode() throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, exhibitorRegCode, null);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getExhibitors", "RegistrationObj"}, dependsOnGroups = "postExhibitor")
    public void testGETExhibitorForEvent() throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getExhibitors", "RegistrationObj"}, dependsOnGroups = "postExhibitor")
    public void testGETExhibitorForEventUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, null, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @Test(dataProvider = "getExhibitorIncludeList-dp1", enabled = true, groups = {"getExhibitors", "RegistrationObj"}, dependsOnGroups = {"postExhibitor"})
    public void testGETRegistrationByEventCodeWithIncludeListUsingMaxResults(String includeList) throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, new String[]{includeList}, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "getExhibitorOrderBy-dp1", parallel = false)
    public Object[][] getExhibitorOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "getExhibitorOrderBy-dp1", enabled = true, groups = {"get-registration-obj", "ExhibitorObj"}, dependsOnGroups = "postExhibitor")
    public void testGETExhibitorForEventOrderBy(String orderBy) throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, null, null, orderBy, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("No suppliers found for the matching search criteria...", true);
            Assert.assertTrue(true);

        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-exhibitor-filter-dp1", parallel = false)
    public Object[][] getExhFilters() throws Exception {
        Object obj = testDataLoad.getExhibitorObjFilters(registrationObj);
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-exhibitor-filter-dp1", enabled = true, groups = {"getExhibitors", "ExhibitorObj"}, dependsOnGroups = "postExhibitor")
    public void testGETExhibitorByEventCodeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria [" + filter + "=" + value + "]");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-exhibitor-filter-dp2")
    public Object[][] getExhFiltersMultiple() throws Exception {
        Object obj = testDataLoad.getExhibitorObjFilters(registrationObj);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 5);
    }

    @Test(dataProvider = "get-exhibitor-filter-dp2", enabled = true, groups = {"getExhibitors", "ExhibitorObj"}, dependsOnGroups = "postExhibitor")
    public void testGETExhibitorWithMultipleSearchFilters(HashMap<String, Object> hashMap) throws Exception {
        TestCase testCase = registrationObjSvc.getExhibitors(accountCode, eventCode, hashMap, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"updateExhibitor", "ExhibitorObj"}, priority = 4, dependsOnGroups = {"postExhibitor"})
    public void testPOSTUpdateExhibitor() throws Exception {
        RegistrationObj registrationObj = exhibitorList.get(1);
        registrationObj.setRegistrationCode(exhibitorRegCode);
        registrationObj.setAttendeeTypeCode(attendeeType);
        registrationObj.setProfile(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.updateExhibitor(accountCode, eventCode, exhibitorRegCode, registrationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteExhibitorReq", "ExhibitorObj"}, priority = 6, dependsOnGroups = {"postExhibitorRequired"})
    public void testDELETEExhibitorRequired() throws Exception {
        TestCase testCase = registrationObjSvc.deleteExhibitor(accountCode, eventCode, exhibitorRegCodeReq);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteExhibitor", "ExhibitorObj"}, priority = 7, dependsOnGroups = {"postExhibitor"})
    public void testDELETEExhibitor() throws Exception {
        TestCase testCase = registrationObjSvc.deleteExhibitor(accountCode, eventCode, exhibitorRegCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}
