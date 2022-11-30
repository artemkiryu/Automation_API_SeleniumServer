package com.certain.External.service.v1;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.EventObjSvc;
import internal.qaauto.certain.platform.services.LocationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.util.*;

import static internal.qaauto.certain.platform.services.EventObjSvc.jsonString2Map;


@SuppressWarnings("all")
public class Event extends CertainAPIBase {

    private String[] includeList = {"answers", "groups", "rotations", "question_assignments", "forms", "websites"};
    private String[] orderBy = {"dateModified_asc", "dateModified_desc", "dateCreated_asc", "dateCreated_desc", "startDate_asc", "startDate_desc", "endDate_asc", "endDate_desc", "eventCode_asc", "eventCode_desc", "eventName_asc", "eventName_desc"};
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private List<com.certain.external.dto.event.EventObj> eventObjList = new ArrayList<>();
    private List<com.certain.external.dto.location.LocationObj> locationObjList;
    private LocationObjSvc locationObjSvc = new LocationObjSvc();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private String accountCode;
    private String eventCode, eventCodeReq, LocationCode, LocationName;
    private String eventName, txtEvtCreatedBy, txtEvtModifiedBy;
    private String eventId;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        try {
            loadData();
            // eventCode = USER_EVENT_CODE;
            accountCode = ACCOUNT_CODE;
            //Read Test Data and store in plain java object class
            eventObjList = testDataLoad.getEventObjData("eventQuestions,location", accountCode);
            locationObjList = testDataLoad.getLocationObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "get-events-includeList", parallel = false)
    public Object[][] getEventIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @Test(dataProvider = "get-events-includeList", enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventWithIncludeList(String includeList) throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, new String[]{includeList}, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventsWithIncludeListAll() throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, includeList, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-events-orderby", parallel = false)
    public Object[][] getEventsOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "get-events-orderby", enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventsOrderBy(String orderBy) throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, null, null, orderBy, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(priority = 1, enabled = true, groups = {"post-location-obj-for-event", "EventObj"})
    public void testPOSTLocationRequiredFields() throws Exception {
        com.certain.external.dto.location.LocationObj locationObj = locationObjList.get(1);
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase testCase = locationObjSvc.createLocation(accountCode, locationObj, expectedCondition, true);
        LocationCode = locationObj.getLocationCode();
        LocationName = locationObj.getName();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-event-obj", "EventObj"}, dependsOnGroups = "post-location-obj-for-event")
    public void testPOSTNewEvent() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        eventObj.setIsLocked(true);
        com.certain.external.dto.event.Location location = new com.certain.external.dto.event.Location();
        location.setLocationCode(LocationCode);
        location.setLocationName(LocationName);
        eventObj.setLocation(location);
        TestCase testCase = eventObjSvc.postEvent(accountCode, eventObj, expectedCondition, false);
        eventCode = eventObj.getEventCode();
        eventName = eventObj.getEventName();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-event-obj-req", "EventObj"})
    public void testPOSTNewEventRequired() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        TestCase testCase = eventObjSvc.postEvent(accountCode, eventObj, expectedCondition, true);
        eventCodeReq = eventObj.getEventCode();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        for (int i = 3; i < 7; i++) {
            eventObj = eventObjList.get(i);
            expectedCondition = new ExpectedCondition(200);
            uniqueCode = randomString(15);
            eventObj.setEventName("Event " + uniqueCode);
            eventObj.setEventCode(uniqueCode);
            testCase = eventObjSvc.postEvent(accountCode, eventObj, expectedCondition, true);
            eventCodeReq = eventObj.getEventCode();
            if (testCase.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
    }

    @Test(enabled = true, groups = {"get-events-info", "EventObj"},
            dependsOnGroups = "post-event-obj")
    public void testGETEventDetailsByCode() throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, eventCode, null);
        if (testCase.isPassed()) {
            Reporter.log("Event [" + eventCode + "] details retrieved successfully", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(dataProvider = "get-events-includeList", enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventsWithIncludeListUsingMaxResultsAndStartIndex(String includeList) throws Exception {
        int size = eventObjSvc.getEventSize(accountCode);
        TestCase testCase = eventObjSvc.getEvents(accountCode, new String[]{includeList}, null, null, 2, size / 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventsWithIncludeListMaxResults() throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, new String[]{"forms", "websites"}, null, null, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-events-obj", "EventObj"})
    public void testGETEventsWithIncludeListMaxResultsAndStartIndex() throws Exception {
        int size = eventObjSvc.getEventSize(accountCode);
        TestCase testCase = eventObjSvc.getEvents(accountCode, new String[]{"answers", "groups"}, null, null, 2, size / 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-events-dp1", parallel = false)
    public Object[][] getEventsFilters() throws Exception {
        Object obj = testDataLoad.getEventObjFilters(eventObjList.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-events-dp1", enabled = true, groups = {"get-events-obj", "EventObj"},
            dependsOnGroups = "post-event-obj")
    public void testGETEventsWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-events-multi-filters")
    public Object[][] getEventMultipleFilters() throws Exception {
        Object obj = testDataLoad.getEventObjFilters(eventObjList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-events-multi-filters", enabled = true, groups = {"get-events-obj", "EventObj"},
            dependsOnGroups = "post-event-obj")
    public void testGETEventsWithMultipleSearchFilters(HashMap<String, Object> searchFilters) throws Exception {
        TestCase testCase = eventObjSvc.getEvents(accountCode, null, searchFilters, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No profiles found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"update-event-obj", "EventObj"},
            priority = 3, dependsOnGroups = "post-event-obj")
    public void testPOSTUpdateEvent() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(1);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        eventObj.setEventCode(eventCode);
        TestCase testCase = eventObjSvc.updateEvent(accountCode, eventCode, eventObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"delete-event-obj", "EventObj"},
            priority = 5, dependsOnGroups = {"post-event-obj"})
    public void testDELETEEvent() throws Exception {
        TestCase testCase = eventObjSvc.deleteEvent(accountCode, eventCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"update-event-obj-req", "EventObj"},
            priority = 3, dependsOnGroups = {"post-location-obj-for-event", "post-event-obj-req"})
    public void testPOSTUpdateEventReqWithLocationDetails() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(1);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        eventObj.setEventCode(eventCodeReq);
        com.certain.external.dto.event.Location location = new com.certain.external.dto.event.Location();
        location.setLocationCode(LocationCode);
        location.setLocationName(LocationName);
        eventObj.setLocation(location);
        TestCase testCase = eventObjSvc.updateEvent(accountCode, eventCodeReq, eventObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"delete-event-obj-req", "EventObj"},
            priority = 5, dependsOnGroups = {"post-event-obj-req"})
    public void testDELETEEventRequired() throws Exception {
        TestCase testCase = eventObjSvc.deleteEvent(accountCode, eventCodeReq);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-event-obj", "EventObj"}, priority = 6)
    public void testPOSTNewEventWithTemplateSCM31104() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(7);
        ExpectedCondition expectedCondition = new ExpectedCondition(200);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        TestCase testCase = eventObjSvc.postEventUsingTemplate(accountCode, eventObj, expectedCondition, true, null);
        eventCode = eventObj.getEventCode();
        eventName = eventObj.getEventName();

        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
        String result = testCase.getPayload();
        try {
            txtEvtCreatedBy = jsonString2Map(result, "txtEvtCreatedBy");

        } catch (NullPointerException e) {
            Reporter.log("[AssertFalse] txtEvtCreatedBy and txtEvtModifiedBy fields having null values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
        }
        try {

            txtEvtModifiedBy = jsonString2Map(result, "txtEvtModifiedBy");
        } catch (NullPointerException e) {
            Reporter.log("[AssertFalse] txtEvtCreatedBy and txtEvtModifiedBy fields having null values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
        }

        eventObj = eventObjList.get(0);
        uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        testCase = eventObjSvc.postEventUsingTemplate(accountCode, eventObj, expectedCondition, true, eventCode);
        if (testCase.getStatusCode() == 200) {
            result = testCase.getPayload();

            try {
                txtEvtCreatedBy = jsonString2Map(result, "txtEvtCreatedBy");

            } catch (NullPointerException e) {
                Reporter.log("[AssertFalse] txtEvtCreatedBy and txtEvtModifiedBy fields having null values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
            }
            try {

                txtEvtModifiedBy = jsonString2Map(result, "txtEvtModifiedBy");
            } catch (NullPointerException e) {
                Reporter.log("[AssertFalse] txtEvtCreatedBy and txtEvtModifiedBy fields having null values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
            }
            if (txtEvtCreatedBy.isEmpty() || txtEvtModifiedBy.isEmpty()) {

                Reporter.log("txtEvtCreatedBy and txtEvtModifiedBy fields having empty values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
            } else
                Reporter.log("[AssertTrue] txtEvtCreatedBy and txtEvtModifiedBy fields having values " + txtEvtCreatedBy + "and " + txtEvtModifiedBy, true);
        } else {
            Reporter.log("Status code is not coming as expected " + testCase.getStatusCode(), true);
            Assert.assertTrue(false);
        }
    }
}
