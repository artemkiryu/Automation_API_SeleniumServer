package com.certain.External.service.v1;

import com.certain.external.dto.exhibitorManagement.ExhibitorManagementObj;
import com.certain.external.dto.registration.RegistrationObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ExhibitorManagement extends CertainAPIBase {

    private final AuthenticationScheme authenticationScheme = new AuthenticationScheme(USERNAME, PASSWORD);
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private String accountCode;
    private String eventCode;
    private List<RegistrationObj> exhibitorList = null;
    private List<ExhibitorManagementObj> exhibitorManagementObjs = new ArrayList<>();
    private String exhibitorRegCode, uniqueLocationId;

    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;

        try {
            exhibitorList = testDataLoad.getExhibitorObjData(accountCode);
            exhibitorManagementObjs = testDataLoad.exhibitorManagementObjList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(groups = {"postExhibitor4Mgmt", "ExhibitorObj"})
    public void testPOSTExhibitor() throws Exception {
        String uniqueCode = randomString(9);
        RegistrationObj registrationObj = exhibitorList.get(0);
        String attendeeType = "Exhibitor";
        registrationObj.setAttendeeTypeCode(attendeeType);
        String exhibitorPin = uniqueCode;
        registrationObj.getProfile().setPin(exhibitorPin);
        registrationObj.getProfile().setFirstName("EXH" + uniqueCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        TestCase testCase = registrationObjSvc.postExhibitor(accountCode, eventCode, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            exhibitorRegCode = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + exhibitorRegCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(groups = {"postExhibitorManagement", "ExhibitorManagement"}, dependsOnGroups = "postExhibitor4Mgmt")
    public void testPOSTExhibitorManagement() throws Exception {
        Reporter.log("Posting  Exhibitor Management of the registrationCode [" + exhibitorRegCode + "]");
        ExhibitorManagementObj exhibitorManagementObj = exhibitorManagementObjs.get(0);
        String url = SERVER_HOST + BASE_PATH + "/Exhibitor/Manage/" + accountCode + "/" + eventCode + "/" + exhibitorRegCode;
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(exhibitorManagementObj), url, authenticationScheme);
        if (response.getStatusCode() == 200) {
            uniqueLocationId = jsonHelper.getJsonValueByKey(response.asString(), "uniqueLocationId").toString();
            if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(exhibitorManagementObj), response.asString())) {
                Reporter.log("Successfully posted exhibitor management for registrationCode [" + exhibitorRegCode + "]", true);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Post was Success but Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post Exhibitor Management for the Registration " + exhibitorRegCode);
        }
    }

    @Test(groups = {"getExhibitorManagement", "ExhibitorManagement"}, dependsOnGroups = "postExhibitorManagement")
    public void testGETExhibitorManagementByRegCode() throws Exception {
        Reporter.log("Retrieving Exhibitor Management of the registrationCode [" + exhibitorRegCode + "]");
        String url = SERVER_HOST + BASE_PATH + "/Exhibitor/Manage/" + accountCode + "/" + eventCode + "/" + exhibitorRegCode;
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        if (response.getStatusCode() == 200) {
            String __actualUnqLocation = jsonHelper.getJsonValueByKey(response.asString(), "uniqueLocationId").toString();
            if (__actualUnqLocation.equals(uniqueLocationId)) {
                Reporter.log("Successfully posted exhibitor management for registrationCode [" + exhibitorRegCode + "]", true);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Post was Success but Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post Exhibitor Management for the Registration " + exhibitorRegCode);
        }

    }

    @Test(groups = {"updateExhibitorManagement", "ExhibitorManagement"}, dependsOnGroups = "postExhibitorManagement")
    public void testPOSTUpdateExhibitorManagement() throws Exception {
        Reporter.log("Posting  Exhibitor Management of the registrationCode [" + exhibitorRegCode + "]");
        ExhibitorManagementObj exhibitorManagementObj = exhibitorManagementObjs.get(1);
        String url = SERVER_HOST + BASE_PATH + "/Exhibitor/Manage/" + accountCode + "/" + eventCode + "/" + exhibitorRegCode;
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(exhibitorManagementObj), url, authenticationScheme);
        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(exhibitorManagementObj), response.asString())) {
                Reporter.log("Successfully posted exhibitor management for registrationCode [" + exhibitorRegCode + "]", true);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Post was Success but Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post Exhibitor Management for the Registration " + exhibitorRegCode);
        }
    }

    @Test(groups = {"deleteExhibitor4Mgmt", "ExhibitorManagement"}, priority = 7, dependsOnGroups = {"updateExhibitorManagement"})
    public void testDELETEExhibitor() throws Exception {
        TestCase testCase = registrationObjSvc.deleteRegistration(accountCode, eventCode, exhibitorRegCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}
