package com.certain.standard.api;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.locations.LocationSessionObject;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.pojo.sessions.SessionObject;
import internal.qaauto.certain.platform.services.LocationManagement;
import internal.qaauto.certain.platform.services.SessionManagement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skumar on 8/16/2015.
 */

@SuppressWarnings("all")
public class LocationSessions extends CertainAPIBase {

    private LocationManagement locationManagement = new LocationManagement();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private SessionManagement sessionManagement = new SessionManagement();
    private List<LocationsObject> locationObjects = new ArrayList<>();
    private List<SessionObject> sessionObjects = new ArrayList<>();
    private LocationsObject locationsObject = new LocationsObject();
    private String locationCode, locationName, sessionCode, sessionName, sessionCode2, sessionName2;
    private int sessionInstanceId, sessionInstanceId2;
    private String schedulerStartTime, schedulerEndTime;
    private String accountCode;
    private String eventCode;
    private String sessionTrack = TRACK_NAME;
    private String sessionDuration, sessionDuration2, startTime;
    private LocationSessionObject locationSessionObject = new LocationSessionObject();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        try {
            locationObjects = testDataLoad.getUCLocationObjData();
            sessionObjects = testDataLoad.getUCSessionsObjData();
            startTime = du.today(du.LONGDATE_AM);
            schedulerStartTime = du.convertDateFromString(startTime, du.UC_LONG_DATE1);
            schedulerEndTime = du.convertDateFromString(du.getFutureTime(schedulerStartTime, SESSION_DURATION), du.UC_LONG_DATE1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"postSession-lc", "UC", "LocationSession"})
    public void testPOSTSessionSingleInstance() throws Exception {
        String uniqueCode = randomString(12);
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionName = sessionObject.getName();
        sessionCode = sessionObject.getSessionCode();
        sessionDuration = sessionObject.getDuration();
        sessionObject.setEventTrack(sessionTrack);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = "postLocations-lc")
    public void testPostLocation() throws Exception {
        locationsObject = locationObjects.get(0);
        locationName = locationsObject.getLocationName();
        locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession", "UC", "LocationSession"}, dependsOnGroups = {"postSession-lc", "postLocations-lc"})
    public void testPOSTSessionLocation() throws Exception {
        locationSessionObject.setLocationCode(locationCode);
        locationSessionObject.setStartDate(startTime);
        locationSessionObject.setSessionInstanceId(String.valueOf(sessionInstanceId));
        locationSessionObject.setSessionName(sessionName);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is successfully scheduled at " + locationName);
        TestCase testCase = locationManagement.postLocationSession(accountCode, eventCode, locationCode, sessionInstanceId, locationSessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getLocationSession", "UC", "LocationSession"}, priority = 2, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testGETLocationSessions() throws Exception {
        TestCase testCase = locationManagement.getLocationSession(accountCode, eventCode, locationCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getLocationSession", "UC", "LocationSession"}, priority = 2, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testGETLocationSessionsWithLimitAndPaging() throws Exception {
        TestCase testCase = locationManagement.getLocationSession(accountCode, eventCode, locationCode, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"publishSession", "UC", "LocationSession"}, priority = 3, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testPublishSessionsSpecificAttendeeType() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is published successfully");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCode, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-location-sessions", parallel = false)
    public Object[][] getLocations() throws Exception {
        locationSessionObject.setStartDate(schedulerStartTime);
        locationSessionObject.setEndDate(schedulerEndTime);
        Object obj = locationManagement.getLocationSessionObjFilters(locationSessionObject);
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-location-sessions", enabled = true, priority = 2, groups = {"getLocationSession", "UC", "LocationSession"}, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testGETLocationSessionsWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = locationManagement.getLocationSession(accountCode, eventCode, locationCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No data found matching criteria ", true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-locations-multi-filters", parallel = false)
    public Object[][] getLocationSessionMultiFilters() throws Exception {
        Object obj = locationManagement.getLocationSessionObjFilters(locationSessionObject);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @Test(dataProvider = "get-locations-multi-filters", enabled = true, priority = 2, groups = {"getLocationSession", "UC", "LocationSession"}, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testGETLocationSessionsWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = locationManagement.getLocationSession(accountCode, eventCode, locationCode, multipleFilters, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No data found matching criteria ", true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-locationSession-neg-dp1", parallel = false)
    public Object[][] getNegSessionFilters() throws Exception {
        return this.testDataLoad.getNegativeFiltersData("LocationSession");
    }

    @Test(dataProvider = "get-locationSession-neg-dp1", enabled = true, groups = {"getLocationSession", "UC", "LocationSession"}, dependsOnGroups = {"postSession-lc", "postLocations-lc", "postLocationSession"})
    public void testGETSessionLocationWithNegativeSearchCriteria(String key, String value, String statusCode, String Message) throws Exception {
        TestCase testCase = locationManagement.getLocationSession(accountCode, eventCode, locationCode, key, value, 0, 0);
        if (testCase.getStatusCode() == Integer.parseInt(statusCode)) {
            if (testCase.getPayload().contains(Message)) {
                Reporter.log("Received proper error code and message", true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"putLocationSession", "UC", "LocationSession"}, dependsOnGroups = {"postLocationSession"})
    public void testPUTUpdateLocationSession() throws Exception {
        startTime = du.today(du.LONGDATE_AM, 20);
        schedulerStartTime = du.convertDateFromString(startTime, du.UC_LONG_DATE1);
        schedulerEndTime = du.convertDateFromString(du.getFutureTime(schedulerStartTime, SESSION_DURATION), du.UC_LONG_DATE1);
        locationSessionObject.setLocationCode(locationCode);
        locationSessionObject.setStartDate(startTime);
        locationSessionObject.setSessionInstanceId(String.valueOf(sessionInstanceId));
        locationSessionObject.setSessionName(sessionName);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is successfully scheduled at " + locationName);
        TestCase testCase = locationManagement.putLocationSession(accountCode, eventCode, locationCode, sessionInstanceId, locationSessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSession-lc2", "UC", "LocationSession"})
    public void testPOSTSessionInstance() throws Exception {
        String uniqueCode = randomString(12);
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionName2 = sessionObject.getName();
        sessionCode2 = sessionObject.getSessionCode();
        sessionDuration2 = sessionObject.getDuration();
        sessionObject.setEventTrack(sessionTrack);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId2 = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession2", "UC", "LocationSession"}, dependsOnGroups = {"postSession-lc", "postSession-lc2", "postLocations-lc", "postLocationSession"})
    public void testPOSTScheduleAnotherSessionsAtSameTimeSlot() throws Exception {
        Reporter.log("scheduling another session to the same location at already scheduled time slot", true);
        LocationSessionObject locationSessionObject2 = locationSessionObject;
        locationSessionObject2.setStartDate(startTime);
        locationSessionObject2.setSessionInstanceId(String.valueOf(sessionInstanceId2));
        locationSessionObject2.setSessionName(sessionName2);
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict at the location " + locationName);
        TestCase testCase = locationManagement.postLocationSession(accountCode, eventCode, locationCode, sessionInstanceId2, locationSessionObject2, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteLocationSession", "UC", "LocationSession"}, priority = 5, dependsOnGroups = {"postLocationSession2"})
    public void testDELETELocationSession() throws Exception {
        TestCase testCase = locationManagement.deleteLocationSession(accountCode, eventCode, locationCode, sessionInstanceId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession@deletedLocation", "UC", "LocationSession"}, dependsOnGroups = {"deleteLocation-lc"})
    public void testPOSTScheduleSessionAtDeletedLocation() throws Exception {
        Reporter.log("scheduling an active session to the deleted location ", true);
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Invalid Location Code.");
        TestCase testCase = locationManagement.postLocationSession(accountCode, eventCode, locationCode, sessionInstanceId2, locationSessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession-lc2", "UC", "Sessions"}, dependsOnGroups = "postLocationSession2")
    public void testDELETESession() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCode2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession@deletedSession", "UC", "Sessions"}, dependsOnGroups = "deleteSession-lc2")
    public void testPOSTScheduleDeletedSession() throws Exception {
        Reporter.log("scheduling deleted session to the active location", true);
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Invalid session instance id");
        TestCase testCase = locationManagement.postLocationSession(accountCode, eventCode, locationCode, sessionInstanceId2, locationSessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @Test(enabled = true, priority = 3, groups = {"deleteLocation-lc", "UC", "LocationSession"}, dependsOnGroups = {"postLocationSession@deletedSession"})
    public void testDELETELocation() throws Exception {
        TestCase testCase = locationManagement.deleteLocation(accountCode, eventCode, locationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


}
