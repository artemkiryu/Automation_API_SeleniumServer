package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.TravelReservationSvcObj;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("all")
public class TravelReservation extends CertainAPIBase {

    private TestDataLoad testDataLoad = new TestDataLoad();
    private TravelReservationSvcObj travelReservationSvcObj = new TravelReservationSvcObj();
    private List<com.certain.external.dto.travelReservation.TravelReservation> travelReservationObjects = new ArrayList<>();
    private com.certain.external.dto.travelReservation.TravelReservation travelReservation = new com.certain.external.dto.travelReservation.TravelReservation();
    private String registrationTravelId;
    private String accountCode;
    private String[] orderBy = {"dateModified_asc", "dateModified_desc", "dateCreated_asc", "dateCreated_desc", "registrationCode_asc",
            "registrationCode_desc", "registrationTravelType_asc", "registrationTravelType_desc",
            "travelProviderName_asc", "travelProviderName_desc", "travelProviderIATA_asc", "travelProviderIATA_desc", "travelProviderICAO_asc", "travelProviderICAO_desc",
            "conveyanceNumber_asc", "conveyanceNumber_desc"};

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        try {
            travelReservationObjects = testDataLoad.getTravelReservationObjData(accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.testng.annotations.Test(groups = {"post-travelReservation", "TravelReservation"}, dependsOnGroups = {"post-event-for-reg", "post-attendee-for-reg", "post-registration-obj", "post-agenda-for-reg"})
    public void testPOSTTravelReservation() throws Exception {
        travelReservation = travelReservationObjects.get(0);
        travelReservation.setRegistrationCode(REGISTRATION_CODE);
        travelReservation.setEventCode(REG_EVENT_CODE);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = travelReservationSvcObj.postTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, travelReservation, expectedCondition, false);
        if (testCase.isPassed()) {
            registrationTravelId = testCase.getRegistrationTravelId();
            Reporter.log(testCase.getMessage() + "\nReservation Travel Id = " + registrationTravelId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = {"post-travelReservation-update", "TravelReservation"}, priority = 1, dependsOnGroups = {"post-travelReservation"})
    public void testPOSTUpdateTravelReservation() throws Exception {
        travelReservation = travelReservationObjects.get(1);
        travelReservation.setRegistrationCode(REGISTRATION_CODE);
        travelReservation.setEventCode(REG_EVENT_CODE);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = travelReservationSvcObj.updateTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, registrationTravelId, travelReservation, expectedCondition);
        if (testCase.isPassed()) {
            registrationTravelId = testCase.getRegistrationTravelId();
            Reporter.log(testCase.getMessage() + "\nReservation Travel Id = " + registrationTravelId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByRegCode() throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-travelReg-filters", parallel = false)
    public Object[][] getTravelRegs() throws Exception {
        Object obj = testDataLoad.getTravelReservationObjFilters(travelReservationObjects.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-travelReg-filters", groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByRegCodeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Travel Reservation found matching criteria ", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-travelReg-filters", groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByEventCodeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Travel Reservation found matching criteria ", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-travel-reg-multi-filters")
    public Object[][] getTravelRegMultipleFilters() throws Exception {
        Object obj = testDataLoad.getTravelReservationObjFilters(travelReservationObjects.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-travel-reg-multi-filters", groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByRegCodeWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, multipleFilters, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Travel Reservation found matching criteria ", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-travelRegOrderBy-dp", parallel = false)
    public Object[][] getTravelRegOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @org.testng.annotations.Test(dataProvider = "get-travelRegOrderBy-dp", groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByRegCodeOrderBy(String orderBy) throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, orderBy, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-travelRegOrderBy-dp", groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByEventCodeOrderBy(String orderBy) throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, null, orderBy, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = {"get-travelReservation", "TravelReservation"}, priority = 2, dependsOnGroups = {"post-travelReservation"})
    public void testGETTravelReservationByEventCodeUsingMaxResultsAndStartIndex() throws Exception {
        TestCase testCase = travelReservationSvcObj.getTravelReservation(accountCode, REG_EVENT_CODE, null, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(groups = {"get-travelReservation", "TravelReservation"}, priority = 3, dependsOnGroups = {"post-travelReservation"})
    public void testDELETETravelReservationById() throws Exception {
        TestCase testCase = travelReservationSvcObj.DELETETravelReservation(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, registrationTravelId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
