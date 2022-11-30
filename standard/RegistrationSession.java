package com.certain.standard.api;

/*
 *
 *   Error messages covered
 *   400 : This session was already scheduled in same time intervals - Reg Session Conflict
 *   400 : Scheduled sessions under the location will be impacted. You cannot delete this location - Delete Scheduled Location
 *   404 : Please provide registration code - Empty Reg Code
 *   404 : Attempted to associate with an invalid session id - Assign Reg to Unscheduled and Unpublished session
 *   404 : Attempted to associate with an invalid session id - Assign Reg to Scheduled but Unpublished session
 *   400 : This session is not published yet - Assign Reg to Scheduled but unpublished session (SCM-26833)
 *
 * */


import com.certain.external.dto.registration.RegistrationObj;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.emory.mathcs.backport.java.util.Arrays;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.locations.LocationSessionObject;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.pojo.sessions.SessionObject;
import internal.qaauto.certain.platform.pojo.sessions.RegistrationSessionData;
import internal.qaauto.certain.platform.pojo.sessions.Session;
import internal.qaauto.certain.platform.services.LocationManagement;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
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


@SuppressWarnings("all")
public class RegistrationSession extends CertainAPIBase {

    Map<String, Object> jsonBody = new HashMap<>();
    private SessionManagement sessionManagement = new SessionManagement();
    private RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private LocationManagement locationManagement = new LocationManagement();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private List<LocationsObject> locationObjects = new ArrayList<>();
    private List<SessionObject> sessionObjects = new ArrayList<>();
    private String locationCode, locationName, sessionCode, sessionName, registrationCode;
    private String locationCode2, locationName2, sessionCode2, sessionName2;
    private int sessionInstanceId, sessionInstanceId2, sessionDuration;
    private String schedulerStartTime, schedulerEndTime, startTime;
    private String accountCode;
    private String eventCode;
    private String sessionTrack = TRACK_NAME;
    private String sessionType;
    private String sessionInstanceSessionCode;
    private List<RegistrationObj> registrationList = new ArrayList<>();
    private String currentStatus = "Registered";
    private final Map<String, String> filterData = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        try {
            locationObjects = testDataLoad.getUCLocationObjData();
            sessionObjects = testDataLoad.getUCSessionsObjData();
            registrationList = testDataLoad.getRegistrationObjData(accountCode);
            startTime = du.today(du.LONGDATE_AM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"postSession-rs", "UC", "RegistrationSession"})
    public void testPOSTSessionInstance() throws Exception {
        String uniqueCode = randomString(12);
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionName = sessionObject.getName();
        sessionCode = sessionObject.getSessionCode();
        sessionInstanceSessionCode = sessionCode;
        sessionType = sessionObject.getSessionType();
        sessionObject.setEventTrack(sessionTrack);
        sessionDuration = Integer.valueOf(sessionObject.getDuration());
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSession-rs2", "UC", "RegistrationSession"})
    public void testPOSTSessionInstance2() throws Exception {
        String uniqueCode = randomString(12);
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionName2 = sessionObject.getName();
        sessionCode2 = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId2 = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId2, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postRegSessions3", "UC", "RegistrationSession"}, dependsOnGroups = {"postSession-rs"})
    public void testPOSTRegistrationToUnpublishedSession() throws Exception {
        TestCase testCase = postRegistration("REG", "SESS", "Attendee");
        if (testCase.isPassed()) {
            Reporter.log("Created registration " + testCase.getRegistrationCode());
            registrationCode = testCase.getRegistrationCode();
            Map<String, Object> jsonRequest = new HashMap<>();
            jsonRequest.put("registrationCode", registrationCode);
            ExpectedCondition expectedCondition = new ExpectedCondition(400, "This session is not published yet.");
            TestCase testCase1 = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
            if (testCase1.isPassed()) {
                Reporter.log(testCase1.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase1.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocation-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions3"})
    public void testPostLocation() throws Exception {
        String uniqueCode = randomString(12);
        LocationsObject locationsObject = locationObjects.get(0);
        locationsObject.setLocationCode(uniqueCode);
        locationsObject.setLocationName("Location " + uniqueCode);
        locationName = locationsObject.getLocationName();
        locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocation-rs2", "UC", "RegistrationSession"})
    public void testPostLocation2() throws Exception {
        String uniqueCode = randomString(12);
        LocationsObject locationsObject = locationObjects.get(0);
        locationsObject.setLocationCode(uniqueCode);
        locationsObject.setLocationName("Location " + uniqueCode);
        locationName2 = locationsObject.getLocationName();
        locationCode2 = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"postSession-rs", "postLocation-rs"})
    public void testPOSTScheduleSessionAtLocation() throws Exception {
        LocationSessionObject locationSessionObject = new LocationSessionObject();
        schedulerStartTime = du.convertDateFromString(startTime, du.UC_LONG_DATE1);
        schedulerEndTime = du.convertDateFromString(du.getFutureTime(schedulerStartTime, sessionDuration), du.UC_LONG_DATE1);
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

    @Test(enabled = true, groups = {"postLocationSession-rs2", "UC", "RegistrationSession"}, dependsOnGroups = {"postSession-rs2", "postLocation-rs2"})
    public void testPOSTScheduleSessionAtLocation2() throws Exception {
        LocationSessionObject locationSessionObject = new LocationSessionObject();
        schedulerStartTime = du.convertDateFromString(startTime, du.UC_LONG_DATE1);
        schedulerEndTime = du.convertDateFromString(du.getFutureTime(schedulerStartTime, sessionDuration), du.UC_LONG_DATE1);
        locationSessionObject.setLocationCode(locationCode2);
        locationSessionObject.setStartDate(startTime);
        locationSessionObject.setSessionInstanceId(String.valueOf(sessionInstanceId2));
        locationSessionObject.setSessionName(sessionName2);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is successfully scheduled at " + locationName2);
        TestCase testCase = locationManagement.postLocationSession(accountCode, eventCode, locationCode2, sessionInstanceId2, locationSessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postRegSessions4", "UC", "RegistrationSession"}, dependsOnGroups = {"postLocationSession-rs", "postSession-rs", "postLocation-rs", "postRegSessions3"})
    public void testPOSTRegistrationToScheduledButUnpublishedSession() throws Exception {
        TestCase testCase = postRegistration("REG", "SESS", "Attendee");
        if (testCase.isPassed()) {
            Reporter.log("Created registration " + testCase.getRegistrationCode());
            registrationCode = testCase.getRegistrationCode();
            Map<String, Object> jsonRequest = new HashMap<>();
            jsonRequest.put("registrationCode", registrationCode);
            ExpectedCondition expectedCondition = new ExpectedCondition(400, "This session is not published yet");
            TestCase testCase1 = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
            if (testCase1.isPassed()) {
                Reporter.log(testCase1.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase1.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"publishSession-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"postLocationSession-rs"})
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

    @Test(enabled = true, groups = {"publishSession-rs2", "UC", "RegistrationSession"}, dependsOnGroups = {"postLocationSession-rs2"})
    public void testPublishSessionsSpecificAttendeeType2() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is published successfully");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCode2, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"postSession-rs", "postLocationSession-rs", "publishSession-rs"})
    public void testPOSTRegistrationSessionMissingRegCode() throws Exception {
        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("registrationCode", null);
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Please provide registration code");
        TestCase testCase = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"publishSession-rs"})
    public void testPOSTRegistrationSession() throws Exception {
        TestCase testCase = postRegistration("REG", "SESS", "Attendee");
        if (testCase.isPassed()) {
            Reporter.log("Created registration " + testCase.getRegistrationCode());
            registrationCode = testCase.getRegistrationCode();
            Map<String, Object> jsonRequest = new HashMap<>();
            jsonRequest.put("registrationCode", registrationCode);
            ExpectedCondition expectedCondition = new ExpectedCondition(200, "Attendee has been successfully registered for the session " + sessionName);
            TestCase testCase1 = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
            if (testCase1.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
            } else Assert.assertTrue(false, testCase.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //Test Registration session time conflict
    @Test(enabled = true, groups = {"postRegSessions2", "UC", "RegistrationSession"}, dependsOnGroups = {"postSession-rs2", "postLocation-rs2", "postLocationSession-rs2", "publishSession-rs2", "postRegSessions"})
    public void testPOSTRegistrationSessionTimeConflict() throws Exception {
        Map<String, Object> jsonRequest = new HashMap<>();
        String uniqueKey = randomString(4);
        jsonRequest.put("registrationCode", registrationCode);
        String uniqueCode = randomString(6) + uniqueKey;
        SessionObject sessionObject = new SessionObject();
        sessionObject.setSessionCode(uniqueCode);
        sessionCode = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Attendee has been successfully registered for the session");
        TestCase testCase = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId2, jsonRequest, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-reg-sessions-dp1", parallel = false)
    public Object[][] getRegSessions() throws Exception {
        jsonBody.put("regSessionStatus", "Registered");
        jsonBody.put("sessionName", sessionName);
        jsonBody.put("sessionCode", sessionCode);
        jsonBody.put("trackName", sessionTrack);
        jsonBody.put("typeName", sessionType);
        jsonBody.put("locationName", locationName);
        jsonBody.put("startDate", schedulerStartTime);
        jsonBody.put("endDate", schedulerEndTime);
        Object obj = testDataLoad.getUCRegistrationSessionObjFilters(jsonBody);
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-reg-sessions-dp1", enabled = true, groups = {"getRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testGETRegistrationSessionWithSearchFilter(String key, String value) throws Exception {
        TestCase testCase = sessionManagement.getRegistrationSessions(accountCode, eventCode, registrationCode, key, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getSessionReg", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testGETSessionRegistrations() throws Exception {
        TestCase testCase = sessionManagement.getSessionRegistrations(accountCode, eventCode, sessionInstanceId, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testGETRegistrationSessions() throws Exception {
        TestCase testCase = sessionManagement.getRegistrationSessions(accountCode, eventCode, registrationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getPayload(), true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-reg-sessions-dp2")
    public Object[][] getLocationFilters() throws Exception {
        Object obj = testDataLoad.getUCRegistrationSessionObjFilters(jsonBody);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @Test(dataProvider = "get-reg-sessions-dp2", enabled = true, groups = {"getRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testGETRegistrationSessionWithMultipleSearchFilter(HashMap<String, Object> f) throws Exception {
        TestCase testCase = sessionManagement.getRegistrationSessions(accountCode, eventCode, registrationCode, f, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-reg-status", parallel = false)
    public Object[][] getStatuses() throws Exception {
        Object[][] obj = new Object[3][1];
        obj[0][0] = "Attended";
        obj[1][0] = "Cancelled";
        obj[2][0] = "Registered";
        return obj;
    }

    @Test(dataProvider = "get-reg-status", enabled = true, groups = {"putRegSessions", "UC", "RegistrationSession"}, priority = 3, dependsOnGroups = {"postRegSessions"})
    public void testPOSTRegistrationSessionStatusChange(String regStatus) throws Exception {
        Reporter.log("[SCM-21490] updating registration session status from [" + currentStatus.toUpperCase() + " to " + regStatus.toUpperCase() + "] of registrationCode " + registrationCode, true);
        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("status", regStatus);
        jsonRequest.put("registrationCode", registrationCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Cannot update the status");
        if (regStatus.equals("Attended") || regStatus.equals("Registered")) {
            expectedCondition = new ExpectedCondition(200, "Registration status updated successfully");
        }

        TestCase testCase = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            currentStatus = regStatus;
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteRegSessions", "UC", "RegistrationSession"}, dependsOnGroups = {"putRegSessions"})
    public void testDELETERegistrationSession() throws Exception {
        TestCase testCase = sessionManagement.deleteRegistrationSession(accountCode, eventCode, sessionInstanceId, registrationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"deleteLocation-rs1", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteRegSessions"})
    public void testDELETEScheduledLocation() throws Exception {
        TestCase testCase = locationManagement.deleteLocation(accountCode, eventCode, locationCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Scheduled sessions under the location will be impacted. You cannot delete this location");
        if (testCase.getStatusCode() == expectedCondition.getStatusCode()) {
            if (testCase.getPayload().contains(expectedCondition.getMessage())) {
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "error message returned not as expected '" + expectedCondition.getMessage() + "'");
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"deleteLocationSession-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteLocation-rs1"})
    public void testDELETELocationSession() throws Exception {
        TestCase testCase = locationManagement.deleteLocationSession(accountCode, eventCode, locationCode, sessionInstanceId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteLocation-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteLocationSession-rs"})
    public void testDELETELocation() throws Exception {
        TestCase testCase = locationManagement.deleteLocation(accountCode, eventCode, locationCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"deleteSession-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteLocation-rs"})
    public void testDELETESessionInstance() throws Exception {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionInstanceSessionCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"deleteLocationSession-rs2", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteRegSessions"})
    public void testDELETELocationSession2() throws Exception {
        TestCase testCase = locationManagement.deleteLocationSession(accountCode, eventCode, locationCode2, sessionInstanceId2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteLocation-rs2", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteLocationSession-rs2"})
    public void testDELETELocation2() throws Exception {
        TestCase testCase = locationManagement.deleteLocation(accountCode, eventCode, locationCode2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"deleteSession-rs", "UC", "RegistrationSession"}, dependsOnGroups = {"deleteLocation-rs2"})
    public void testDELETESessionInstance2() throws Exception {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCode2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    public TestCase postRegistration(String firstName, String lastName, String attendeeType) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "");
        String uniqueNumber = randomString(9);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.setAttendeeTypeCode(attendeeType);
        registrationObj.getProfile().setFirstName(firstName + uniqueNumber);
        registrationObj.getProfile().setFirstName(lastName + uniqueNumber);
        registrationObj.getProfile().setPin(uniqueNumber);
        return registrationObjSvc.postRegistration(accountCode, eventCode, registrationObj, expectedCondition, false);
    }

    @Test(enabled = false, groups = {"dateModifiedSession", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testPOSTRegistrationSessionAndCheckDateModified() throws Exception {
        // getSessionRegistrationsDateModified(String accountCode, String eventCode, int sessionInstanceId, String registrationCode, ExpectedCondition expectedCondition, Map<String, Object> jsonBody)
        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("registrationCode", registrationCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Attendee has been successfully registered for the session");
        TestCase testCase = sessionManagement.getSessionRegistrationsDateModified(accountCode, eventCode, sessionInstanceId, registrationCode, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = false, groups = {"dateModifiedSession", "UC", "RegistrationSession"}, dependsOnGroups = {"postRegSessions"})
    public void testDELETEAssignSessionToRegistration() throws Exception {
        TestCase testCase = postRegistration("REG", "SESS", "Attendee");
        if (testCase.isPassed()) {
            Reporter.log("Created registration " + testCase.getRegistrationCode());
            registrationCode = testCase.getRegistrationCode();
            Map<String, Object> jsonRequest = new HashMap<>();
            jsonRequest.put("registrationCode", registrationCode);
            ExpectedCondition expectedCondition = new ExpectedCondition(200, "Attendee has been successfully registered for the session " + sessionName);
            TestCase testCase1 = sessionManagement.assignRegistrations(accountCode, eventCode, sessionInstanceId, jsonRequest, expectedCondition);
            if (testCase1.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
            } else Assert.assertTrue(false, testCase.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
        TestCase testCase2 = sessionManagement.deleteRegistrationSession(accountCode, eventCode, sessionInstanceId, registrationCode);
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "You can not delete this session.");
        if (testCase.getStatusCode() == expectedCondition.getStatusCode()) {
            if (testCase.getPayload().contains(expectedCondition.getMessage())) {
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "error message returned not as expected '" + expectedCondition.getMessage() + "'");
        } else Assert.assertTrue(false);
    }

    @DataProvider(name = "get-regSession")
    public Object[][] getRegSession() throws Exception {
        return testDataLoad.getKeyValuePairFromObject(filterData);
    }

    /*@Test(enabled = true, groups = {"get-regSession", "UC", "RegistrationSession"})
    public void testGETRegistrationSessionsSCM41061() throws Exception {
        TestCase testCase = sessionManagement.getRegistrationSessionsWithoutRegCode(accountCode, eventCode, null, 0, 0);
        if (testCase.isPassed()) {
            ArrayList<Object> al = jsonHelper.getJsonArray(testCase.getPayload(), "data");
            ObjectMapper mapper = new ObjectMapper();
            List<RegistrationSessionData> ppl2 = new ArrayList();
            for (Object obj : al) {
                //RegistrationSessionData rsession = mapper.readValue(obj, RegistrationSessionData.class);
                //ppl2.add(rsession);
            }
            //   List<RegistrationSession> regSessionList = testCase.getPayload().toString();
            RegistrationSessionData[] pp1 = mapper.readValue(testCase.getPayload(), RegistrationSessionData[].class);
            //  List<RegistrationSessionData> ppl2 = Arrays.asList(mapper.readValue(al, RegistrationSessionData[].class));

            *//*filterData.put("registrationId",);
            filterData.put("registrationUUID", registrationList.get(0).get("registrationUUID").toString());
            filterData.put("pkRegId", registrationList.get(0).get("pkRegId").toString());
            filterData.put("registrationCode", registrationList.get(0).get("registrationCode").toString());
            filterData.put("pkEventId", registrationList.get(0).get("pkEventId").toString());
            filterData.put("eventCode", eventCode);
            *//**//*List<HashMap<String, Object>> registrationList = jsonHelper.getList(testCase.getPayload(), "data");
            // registrationCode = sessionsInstances.get("registrationCode").toString();
            filterData.put("registrationId", registrationList.get(0).get("registrationId").toString());
            filterData.put("registrationUUID", registrationList.get(0).get("registrationUUID").toString());
            filterData.put("pkRegId", registrationList.get(0).get("pkRegId").toString());
            filterData.put("registrationCode", registrationList.get(0).get("registrationCode").toString());
            filterData.put("pkEventId", registrationList.get(0).get("pkEventId").toString());
            filterData.put("eventCode", eventCode);

            for(int i =0; i < registrationList.size();i++) {
               // ArrayList<String> sessionInstances = new ArrayList<String>(Arrays.asList(registrationList.get(i).get("sessions")));
             //   if(registrationList.get(i).get("sessions") > 0){
              //  ArrayList<String> sessionsInstances = registrationList.get(i).get("sessions");

                    *//**//*filterData.put("instanceId", sessionsInstances.get(i).get("instanceId").toString());
                    filterData.put("registrationCode", sessionsInstances.get(i).get("registrationCode").toString());
                    filterData.put("sessionCode", sessionsInstances.get(i).get("sessionCode").toString());
                    filterData.put("sessionType", sessionsInstances.get(i).get("sessionType").toString());
                    filterData.put("sessionLevel", sessionsInstances.get(i).get("sessionLevel").toString());
                    filterData.put("regSessionStatus", sessionsInstances.get(i).get("regSessionStatus").toString());
                    filterData.put("dateCreated", sessionsInstances.get(i).get("dateCreated").toString());
                    filterData.put("dateModified", sessionsInstances.get(i).get("dateModified").toString());*//**//*
                }
            }
*//*

            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true, "filter data " + filterData);
        *//*} else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());*//*
        }
    }
*/
}
