package com.certain.standard.api;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.TestCaseData;
import internal.qaauto.certain.platform.pojo.locations.LocationSessionObject;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.pojo.sessions.*;
import internal.qaauto.certain.platform.pojo.speakers.SpeakersObject;
import internal.qaauto.certain.platform.services.LocationManagement;
import internal.qaauto.certain.platform.services.SessionManagement;
import internal.qaauto.certain.platform.services.SpeakerManagement;
import org.testng.Assert;
import org.testng.Reporter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
public class SimpleSessions extends CertainAPIBase {
    private static int noOfInstances;
    private static String multiInstanceSessionCode;
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private SessionManagement sessionManagement = new SessionManagement();
    List<String> speakerPinList = new ArrayList<>();
    private LocationManagement locationManagement = new LocationManagement();
    private List<CustomFields> customFieldsObjects = new ArrayList<>();
    private List<CustomFields> customFieldsObjects1 = new ArrayList<>();
    List<String> locationCodeList = new ArrayList<>();
    private List<Tags> tagsList = new ArrayList<>();
    private SpeakerManagement speakerManagement = new SpeakerManagement();
    private List<LocationsObject> locationObjects = new ArrayList<>();
    private ArrayList<String> industry = new ArrayList<>();
    private ArrayList<String> jobFunctions = new ArrayList();
    private String tags = TAGS;
    private String industries = CONF_INDUSTRY;
    private String jobFunction = CONF_JOB_FUNCTION;
    private int sessionDuration, sessionInstanceId, sessionInstanceIdReq;
    private String sessionCode, sessionName, sessionCodeReq, sessionNameReq, sessionCodeWithJF, sessionCodeWithFees, sessionCodeInstances, sessionInstanceCode;
    private String accountCode;
    private String eventCode;
    private String sessionTrack = TRACK_NAME;
    private SessionObject sessionObject = new SessionObject();
    private String locationName, locationCode;
    private String schedulerStartTime, schedulerEndTime, startTime;
    private SessionFees sessionFeeObject, sessionCancelFeeObject;
    private int accountId;
    // private boolean sessionLayout = SESSIONLAYOUT;
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = SIMPLE_EVENT_CODE;
        accountId = getAccountId();
        Reporter.log("Accound id is " + accountId);
        /*System.out.println("session layout value is " + sessionLayout);
        if (!sessionLayout) {
            throw new SkipException("skipped Session Layout value is false and it is conference session.");
        }*/
        try {
            tagsList = testDataLoad.getTagsData(1, accountId);
            // locationObjects = testDataLoad.getUCLocationObjData();

            jobFunctions.add(jobFunction);
            industry.add(industries);
            startTime = du.today(du.LONGDATE_AM);
            schedulerStartTime = du.convertDateFromString(startTime, du.UC_LONG_DATE1);
            schedulerEndTime = du.convertDateFromString(du.getFutureTime(schedulerStartTime, SESSION_DURATION), du.UC_LONG_DATE1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeSetUp() {
        try {
            // sessionObjects = testDataLoad.getUCSessionsObjData();
            locationObjects = testDataLoad.getUCLocationObjData();
            //occurrencesObjects = testDataLoad.getSessionOccurancesData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(enabled = true, groups = {"postSession", "UC", "Sessions"})
    public void testPOSTSessionSingleInstance() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESSION " + uniqueCode);
        sessionName = sessionObject.getName();
        sessionCode = sessionObject.getSessionCode();
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

    @Test(enabled = true, groups = {"publishInvSession", "UC", "Sessions"}, dependsOnGroups = {"postSession"})
    public void testPublishUnscheduledSessionInstance() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Session SESSION " + sessionCode + " is not in Scheduled status, So you can't publish the session.");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCode, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocations4session", "UC", "Sessions"})
    public void testAddLocation() throws Exception {
        LocationsObject locationsObject = locationObjects.get(0);
        locationName = locationsObject.getLocationName();
        locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postLocationSession4Session", "UC", "Sessions"}, priority = 1, dependsOnGroups = {"postSession", "postLocations4session"})
    public void testScheduleSessionAtLocation() throws Exception {
        LocationSessionObject locationSessionObject = new LocationSessionObject();
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

    @Test(enabled = true, groups = {"publishSessionInstance", "UC", "Sessions"}, priority = 1, dependsOnGroups = {"postLocationSession4Session"})
    public void testPublishSessionsForAllAttendeeType() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session is published successfully");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCode, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"publishSessionInstance2", "UC", "Sessions"}, priority = 1, dependsOnGroups = {"publishSessionInstance"})
    public void testPublishSessionsAlreadyPublished() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        // System.out.print("message ------     Session "+sessionName+" is already published.");
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Session " + sessionName + " is already published.");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCode, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"publishInvSession", "UC", "Sessions"})
    public void testPublishInvalidSessionInstance() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Invalid Session Code.");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, "INVSESS1", ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 1, groups = {"postSessionWithJF", "UC", "Sessions"})
    public void testPOSTSessionWithJobFunctionAndIndustryList() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_JFIND " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        ArrayList<String> jobfunctions = new ArrayList<>();
        String a[] = CONF_JOB_FUNCTION.split(",");
        for (String s : a) {
            jobfunctions.add(s);
        }

        ArrayList<String> industriesList = new ArrayList<>();
        String b[] = CONF_INDUSTRY.split(",");
        for (String s : b) {
            industriesList.add(s);
        }
        sessionObject.setConfJobFunction(jobfunctions);
        sessionObject.setConfIndustry(industriesList);
        sessionCodeWithJF = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 1, groups = {"postSessionWithJF", "UC", "Sessions"})
    public void testPOSTSessionWithMultipleJobFunctionAndIndustryList() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_MUL_JFIND " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        ArrayList<String> jobfunctions = new ArrayList<>();
        String a[] = CONF_JOB_FUNCTION.split(",");
        for (String s : a) {
            jobfunctions.add(s);
        }

        ArrayList<String> industriesList = new ArrayList<>();
        String b[] = CONF_INDUSTRY.split(",");
        for (String s : b) {
            industriesList.add(s);
        }
        sessionObject.setConfJobFunction(jobfunctions);
        sessionObject.setConfIndustry(industriesList);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionReq", "UC", "Sessions"})
    public void testPOSTSessionWithInvalidJobFunctionAndIndustry() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_JFIND " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        jobFunctions.add("InvAutomation");
        industry.add("InvQAIndustry1");
        sessionObject.setConfJobFunction(jobFunctions);
        sessionObject.setConfIndustry(industry);
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Industry with the name %s has been chosen for use in the event, so it can't be assigned to a session. Note: Multiple Industries for the same session must be separated by the | pipe character.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSessionReq", "UC", "Sessions"})
    public void testPOSTSessionMultipleInstanceRequiredFieldsOnly() throws Exception {
        String uniqueCode = randomString(15);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionObject.setEventTrack(sessionTrack);
        sessionNameReq = sessionObject.getName();
        sessionCodeReq = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceIdReq = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getSession", "UC", "Sessions"}, priority = 2, dependsOnGroups = "postSessionReq")
    public void testGETSessionMultipleInstanceView() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, sessionInstanceIdReq);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getPayload(), true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"publishSessionInstance2", "UC", "Sessions"}, dependsOnGroups = {"postSessionReq"})
    public void testPublishUnscheduledSessions() throws Exception {
        Map<String, Object> ___json = new HashMap<>();
        ___json.put("attendeetypes", "all");
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Session " + sessionNameReq + " is not in Scheduled status, So you can't publish the session.");
        TestCase testCase = sessionManagement.publishSession(accountCode, eventCode, sessionCodeReq, ___json, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionSingleInstanceWithSessionFees() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_FEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("1");
        ArrayList sesionFeeArray = new ArrayList();
        sesionFeeArray.add(testDataLoad.getSessionFees(90.0, false, 10));
        sessionObject.setSessionFees(sesionFeeArray);

        sessionObject.setConfIndustry(null);
        sessionObject.setConfJobFunction(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //SCM-26763
    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionInstanceWithFeeButNoAdditionParams() throws Exception {
        Reporter.log("------------SCM-26763---------------", true);
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_FEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("1");
        ArrayList arrayList = new ArrayList();
        SessionFees sessionFees = new SessionFees();
        sessionFees.setAmount(190.0);
        sessionFees.setIsCancel(false);
        sessionFees.setExpDateInString(du.dayAfter("yyyy/MM/dd 8:0:00", 10));
        arrayList.add(sessionFees);
        sessionObject.setSessionFees(arrayList);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionInstanceWithCancellationFees() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_CANCFEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("1");
        ArrayList arrayList = new ArrayList();
        arrayList.add(testDataLoad.getSessionFees(120.0, true, 20));
        System.out.println("----Size of arraylist" + arrayList.size());
        sessionObject.setConfIndustry(null);
        sessionObject.setConfJobFunction(null);
        sessionObject.setSessionFees(arrayList);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithBothFees", "UC", "Sessions"})
    public void testPOSTSessionInstanceWithBothSessionAndCancellationFees() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setNoOfInstances("1");
        sessionObject.setName("SESS_FEES " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        ArrayList arrayList = new ArrayList();
        sessionObject.setConfIndustry(null);
        sessionObject.setConfJobFunction(null);
        sessionFeeObject = testDataLoad.getSessionFees(650.0, false, 5);
        sessionCancelFeeObject = testDataLoad.getSessionFees(90.0, true, 5);
        arrayList.add(sessionFeeObject);
        arrayList.add(sessionCancelFeeObject);
        sessionObject.setSessionFees(arrayList);
        sessionCodeWithFees = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //SCM-22521
    @Test(enabled = true, priority = 2, groups = {"putSessionHavingBothFees", "UC", "Sessions"}, dependsOnGroups = "postSessionWithBothFees")
    public void testPUTUpdateSessionFeesDetails() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(sessionCodeWithFees);
        sessionObject.setName(sessionObject.getName() + " UPD");
        sessionObject.setEventTrack(sessionTrack);
        ArrayList arrayList = new ArrayList();
        sessionObject.setConfIndustry(null);
        sessionObject.setNoOfInstances("1");
        sessionObject.setConfJobFunction(null);
        sessionObject.setCapacity(null);
        sessionFeeObject.setCode(null);
        sessionFeeObject.setExpDateInString(null);
        sessionFeeObject = testDataLoad.getSessionFees(300.0, false, 4);
        sessionCancelFeeObject = testDataLoad.getSessionFees(130.0, true, 3);
        arrayList.add(sessionFeeObject);
        arrayList.add(sessionCancelFeeObject);
        arrayList.add(testDataLoad.getSessionFees(55.0, false, 3));
        sessionObject.setSessionFees(arrayList);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session updated successfully");
        TestCase testCase = sessionManagement.putSession(accountCode, eventCode, sessionCodeWithFees, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession", "UC", "Sessions"}, priority = 4, dependsOnGroups = "putSessionHavingBothFees")
    public void testDELETESessionHavingSessionFees() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCodeWithFees);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionWithMultipleSessionFees() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_CANCFEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        ArrayList sessionFeesList = new ArrayList();
        // sessionFeesList.add(testDataLoad.getSessionFees(1200.0,false,20));
        // sessionFeesList.add(testDataLoad.getSessionFees(90.0,true,5));
        // sessionFeesList.add(testDataLoad.getSessionFees(120.0,true,10));
        sessionObject.setSessionFees(sessionFeesList);
        sessionObject.setConfIndustry(null);
        sessionObject.setConfJobFunction(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionWithMultipleCancellationFees() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_CANCFEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        ArrayList sessionFeesList = new ArrayList();
        // sessionFeesList.add(testDataLoad.getSessionFees(50.0,true,5));
        // sessionFeesList.add(testDataLoad.getSessionFees(90.0,true,10));
//        sessionObject.setSessionFees(sessionFeesList);
        sessionObject.setConfIndustry(null);
        sessionObject.setConfJobFunction(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"postSessionWithFees", "UC", "Sessions"})
    public void testPOSTSessionSingleInstanceWithTags() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_TAGS " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setTags(tagsList);
        sessionObject.setNoOfInstances("1");
        sessionObject.setSessionFees(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getSession", "UC", "Sessions"}, priority = 2, dependsOnGroups = "postSession")
    public void testGETSessionInstanceView() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, sessionInstanceId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getPayload(), true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = false, groups = {"getSession", "UC", "Sessions"})
    public void testGETSessionsWithMaxSize() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"getSession", "UC", "Sessions"}, dependsOnGroups = "postSession")
    public void testGETSessionsByEventCode() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = false, groups = {"getSession", "UC", "Sessions"})
    public void testGETSessionsWithMaxSizeAndPage() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, 3, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-sessions-dp1", parallel = false)
    public Object[][] getSessionsFilters() throws Exception {
        Object obj = sessionManagement.getSessionFilters();
        return this.testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-sessions-dp1", enabled = true, groups = {"getSession", "UC", "Sessions"}, dependsOnGroups = "postSession")
    public void testGETSessionWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-sessions-multi-filters")
    public Object[][] getSessionMultipleFilters() throws Exception {
        Object obj = sessionManagement.getSessionFilters();
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-sessions-multi-filters", enabled = true, groups = {"getSession", "UC", "Sessions"}, dependsOnGroups = "postSession")
    public void testGETSessionWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, multipleFilters, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-sessions-dp2", parallel = false)
    public Object[][] getNegSessionFilters() throws Exception {
        return this.testDataLoad.getNegativeFiltersData("Sessions");
    }

    @Test(dataProvider = "get-sessions-dp2", enabled = true, groups = {"getSession", "UC", "Sessions"})
    public void testGETSessionsWithNegativeFilters(String key, String value, String statusCode, String expectedErrorMessage) throws Exception {
        Reporter.log("Getting sessions for the event " + eventCode + " by search filter " + key, true);
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, key, value, 0, 0);
        if (testCase.getStatusCode() == Integer.parseInt(statusCode)) {
            if (testCase.getPayload().contains(expectedErrorMessage)) {
                Reporter.log(testCase.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"updateSession", "UC", "Sessions"}, priority = 3, dependsOnGroups = "postSession")
    public void testPUTUpdateSessionDetails() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(1);
        sessionObject.setSessionCode(sessionCode);
        sessionName += " edited";
        sessionObject.setName(sessionName);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setCapacity(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session updated successfully");
        TestCase testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"updateSession", "UC", "Sessions"}, priority = 3, dependsOnGroups = "postSession")
    public void testPUTUpdateSessionDetailsWithInvalidJobFunctionAndIndustry() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(1);
        sessionObject.setSessionCode(sessionCode);
        sessionObject.setName(sessionName);
        jobFunctions.add("InvaidJF");
        industry.add("InvaidIndustry");
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setConfJobFunction(jobFunctions);
        sessionObject.setConfIndustry(industry);
        ExpectedCondition expectedCondition = new ExpectedCondition(404, "Industry with the name %s has been chosen for use in the event, so it can't be assigned to a session. Note: Multiple Industries for the same session must be separated by the | pipe character.");
        TestCase testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession", "UC", "Sessions"}, priority = 4, dependsOnGroups = "publishSessionInstance2")
    public void testDELETESession() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession", "UC", "Sessions"}, priority = 4, dependsOnGroups = "postSessionWithJF")
    public void testDELETESessionWithJF() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCodeWithJF);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"deleteSession", "UC", "Sessions"}, priority = 4, dependsOnGroups = "postSessionReq")
    public void testDELETESessionRequired() {
        TestCase testCase = sessionManagement.deleteSession(accountCode, eventCode, sessionCodeReq);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "post-sessions-dp1", parallel = false)
    public Object[][] postSessionNegative() throws Exception {
        List<TestCaseData> testCaseDataList;
        testCaseDataList = testDataLoad.getSessionsData();
        int startRow = 4;
        int endRow = 11;
        Object[][] objects = new Object[endRow - startRow][4];
        int index = 0;
        for (int i = startRow; i < endRow; i++) {
            objects[index][0] = testCaseDataList.get(i).getObject();
            objects[index][1] = testCaseDataList.get(i).getDescription();
            objects[index][2] = testCaseDataList.get(i).getStatusCode();
            objects[index][3] = testCaseDataList.get(i).getExpectedMessage();
            index++;
        }
        return objects;
    }

    @DataProvider(name = "post-sessions-dp2", parallel = false)
    public Object[][] postSessionAllNegativeCases() throws Exception {
        List<TestCaseData> testCaseDataList;
        testCaseDataList = testDataLoad.getSessionsDataForNegativeCases();
        int startRow = 12;
        int endRow = 21;
        Object[][] objects = new Object[endRow - startRow][4];
        int index = 0;
        for (int i = startRow; i < endRow; i++) {
            objects[index][0] = testCaseDataList.get(i).getObject();
            objects[index][1] = testCaseDataList.get(i).getDescription();
            objects[index][2] = testCaseDataList.get(i).getStatusCode();
            objects[index][3] = testCaseDataList.get(i).getExpectedMessage();
            index++;
        }
        return objects;
    }

    @Test(dataProvider = "post-sessions-dp1", enabled = true, groups = {"postSsessionNeg", "UC", "Sessions"})
    public void testPOSTSessionNegative(SessionObject sessionObject, String description, int statusCode, String expectedMessage) throws Exception {
        Reporter.log("Test Scenario " + description, true);
        if (expectedMessage.contains("Session Status should be")) {
            expectedMessage = "Session Status for a simple session should be Qualified.";
        }
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(dataProvider = "post-sessions-dp2", enabled = true, groups = {"postSessionNeg", "UC", "Sessions"})
    public void testPOSTSessionAllNegativeCases(SessionObject sessionObject, String description, int statusCode, String expectedMessage) throws Exception {
        Reporter.log("Test Scenario " + description, true);
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"postLocation4Instances"})
    public void testPOSTLocation() throws Exception {
        locationObjects = testDataLoad.getUCLocationObjData();
        LocationsObject locationsObject = locationObjects.get(0);
        locationName = locationsObject.getLocationName();
        locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 10, groups = {"postSessionWithMultipleOccurances", "UC", "Sessions"}, dependsOnGroups = "postLocation4Instances")
    public void testPOSTSessionWithSingleOccurances() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        //  sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();
        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        String sessionCode = uniqueCode;
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        //  sessionCodeInstances = sessionObject.getSessionCode();
        // sessionName = sessionObject.getName();
        occurances.setLocationCode(locationCode);
        occurances.setEndTime(null);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);
        String duration = sessionObject.getDuration();
        String regex = "(" + duration + ")";
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings.The End Date was not passed, so the Duration in payload " + regex + " is ignored for simple session and set to the default 60 mins and the End Date was auto-calculated");
        TestCase testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            //   sessionInstanceCode = sessionName + "_"+ testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        sessionObject.setSessionCode(randomString(5));

        expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict at the location : " + locationName);
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        Occurrences occurances1 = occurrencesObjects.get(1);
        sessionObject.setSessionCode(uniqueCode);
        sessionOccurancesList.add(occurances1);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "The layout is configured as Simple Sessions, hence only 1 session instance can be created per session.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, priority = 11, groups = {"UC", "Sessions"}, dependsOnGroups = "postLocation4Instances")
    public void testPOSTSessionWithInvalidStartDateOccurances() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();

        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        occurances.setLocationCode(locationCode);
        occurances.setStartTime("14-11-2019 14:30:00");
        occurances.setEndTime("06/29/2019 2:45 pm");
        ArrayList sessionOccurancesList = new ArrayList();

        sessionOccurancesList.add(occurances);

        sessionObject.setOccurances(sessionOccurancesList);

        sessionObject.setSessionCode(randomString(5));

        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Start Date format must be (MM/dd/yyyy h:mm a)");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        sessionOccurancesList.clear();

        occurances.setEndTime("14-11-2019 14:30:00");
        occurances.setStartTime("06/29/2019 2:45 pm");
        sessionOccurancesList.add(occurances);

        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "End Date format must be (MM/dd/yyyy h:mm a)");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        sessionOccurancesList.clear();

        occurances.setEndTime("06/29/2019 2:45 pm");
        occurances.setStartTime(null);
        sessionOccurancesList.add(occurances);

        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "Start Date is required with End Date. Format: (MM/dd/yyyy h:mm a)");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, priority = 12, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithNoOfInstancesValidationSCM33919() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        sessionObject.setNoOfInstances("2");
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "The layout is configured as Simple Sessions, hence only 1 session instance can be created per session.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        sessionObject.setNoOfInstances("0");
        expectedCondition = new ExpectedCondition(400, "The layout is configured as Simple Sessions, hence only 1 session instance can be created per session.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        sessionObject.setNoOfInstances("-1");
        expectedCondition = new ExpectedCondition(400, "The layout is configured as Simple Sessions, hence only 1 session instance can be created per session.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

    }

    @Test(enabled = true, priority = 13, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithValidateOccurancesWarnings() throws Exception {

        //POST New Location
        locationObjects = testDataLoad.getUCLocationObjData();
        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        String uniqueCode = randomString(7);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();
        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        String locCode = "Lcddtft7t7y8y";
        occurances.setLocationCode(locCode);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        Reporter.log("POST Session with invalid location code under instances ------------------------", true);
        expectedCondition = new ExpectedCondition(404, "Invalid Location Code (" + locCode + ").");
        testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("POST Session with startDate and endDate ,duration not matched ------------------------", true);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setDuration("20");

        occurances.setLocationCode(locationCode);
        sessionOccurancesList.clear();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The Start and End Date passed did not match with the Duration passed, so the Duration was auto-calculated");
        testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("POST Session with startDate only ------------------------", true);

        sessionObject.setSessionCode(randomString(7));
        sessionObject.setDuration(null);

        occurances.setStartTime("11/20/2019 10:35 pm");
        occurances.setEndTime(null);
        sessionOccurancesList.clear();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings.The End Date and Duration were not passed, so the Duration was set to the default 60 mins and the End Date was auto-calculated");
        testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 14, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithDurationValidationSCM33852() throws Exception {

        //POST New Location
        String uniqueCode = randomString(7);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(29);
        sessionObject.setDuration("30");
        String duration = sessionObject.getDuration();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The Start and End Date were not passed, so the Duration(" + duration + ") was ignored for simple session");
        TestCase testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 15, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithNegativeCapacitySCM33929() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();
        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);

        occurances.setLocationCode(locationCode);
        occurances.setCapacity("-1");

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Capacity must be non-negative and less than 100,000.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        sessionObject.setSessionCode(randomString(5));
        occurances.setWaitlistedLimit("-1");
        occurances.setCapacity("1");

        expectedCondition = new ExpectedCondition(400, "WaitListedLimit must be non-negative and less than 100,000.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

    }

    @Test(enabled = true, priority = 16, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithOverideCapacityAtSessionLevelSCM34598() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        sessionObject.setOverrideCapacity("true");

        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The session level override capacity value was ignored, the override capacity can be set against an instance only");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = true, priority = 17, groups = {"UC", "Sessions"})
    public void testPOSTSessionAtDescriptionBlankSCM34399() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        String sessionCode = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("------------PUT Session with abstract Desc and Description values blank ------------------------", true);

        sessionObject.setSessionCode(sessionCode);
        sessionObject.setAbstractDesc("");
        sessionObject.setDescription("");

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);

        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"UC", "Sessions"}, priority = 18)
    public void testPUTSessionWithTagJobFunctionAndIndustrySCM34356() throws Exception {

        Reporter.log("----------------POST Session with multiple tags,job fucntion ,industry---------------------", true);

        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(1);

        ArrayList<String> jobfunctions = new ArrayList<>();
        String a[] = CONF_JOB_FUNCTION.split(",");
        for (String s : a) {
            jobfunctions.add(s);
        }

        ArrayList<String> industriesList = new ArrayList<>();
        String b[] = CONF_INDUSTRY.split(",");
        for (String s : b) {
            industriesList.add(s);
        }

        List<Tags> tagsArray = testDataLoad.getTagsData(TAGS);

        sessionObject.setConfJobFunction(jobfunctions);
        sessionObject.setConfIndustry(industriesList);
        sessionObject.setTags(tagsArray);
        String sessionCode = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------PUT Session with multiple tags,job fucntion ,industry---------------------", true);

        sessionObject.setSessionCode(sessionCode);

        jobfunctions.clear();
        jobfunctions.add(CONF_JOB_FUNCTION.split(",")[0]);

        industriesList.clear();
        industriesList.add(CONF_INDUSTRY.split(",")[0]);

        tagsArray.clear();
        tagsArray = testDataLoad.getTagsData(TAGS.split(",")[0]);

        sessionObject.setConfJobFunction(jobfunctions);
        sessionObject.setConfIndustry(industriesList);
        sessionObject.setTags(tagsArray);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("------------------POST Session with invalid tags -------------------------");

        sessionCode = randomString(6);
        sessionObject.setSessionCode(sessionCode);
        tagsArray.clear();

        tagsArray = testDataLoad.getTagsData("InvalidTag");

        sessionObject.setTags(tagsArray);

        expectedCondition = new ExpectedCondition(404, "No Tags are assigned to the account.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("------------------POST Session with tags is not associated with session object SCM-34353-------------------------");

        sessionCode = randomString(6);
        sessionObject.setSessionCode(sessionCode);
        industriesList.clear();
        jobfunctions.clear();
        tagsArray.clear();

        String tagsVal = TAGS.split(",")[0] + "," + "Tag4";

        tagsArray = testDataLoad.getTagsData(tagsVal);

        sessionObject.setTags(tagsArray);

        expectedCondition = new ExpectedCondition(404, "Tag names in Tags are invalid for account.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("---------------------PUT Session with invalid session code in payload SCM-34343-----------------------", true);

        sessionObject.setSessionCode(randomString(5));

        jobfunctions.clear();
        industriesList.clear();
        tagsArray.clear();

        expectedCondition = new ExpectedCondition(400, "Session Code cannot be updated");
        testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = true, groups = {"UC", "Sessions"}, priority = 19)
    public void testPUTSessionWithInstancesAndWrongFormatSCM33949SCM33951() throws Exception {

        Reporter.log("----------------POST Session in qualified state---------------------", true);

        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjDataSimpleSession();
        SessionObject sessionObject = sessionObjects.get(1);
        sessionObject.setNoOfInstances(null);

        String sessionCode = sessionObject.getSessionCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------POST Location for Session---------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("---------------------PUT Session with invalid date time format SCM-33951--------------------------------", true);

        sessionObject.setSessionCode(sessionCode);
        sessionObject.setNoOfInstances("1");
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();
        Occurrences occurances = occurrencesObjects.get(0);
        occurances.setLocationCode(locationCode);
        occurances.setStartTime("31/02/2015 01:30 am");

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "Start Date format must be (MM/dd/yyyy h:mm a)");
        testCase = sessionManagement.putSession(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("---------------------PUT Session with instances SCM-33949--------------------------------", true);
        sessionObject.setCapacity(null);
        occurances.setLocationCode(locationCode);
        occurances.setStartTime("02/02/2015 01:30 am");
        occurances.setEndTime("02/02/2015 01:40 am");

        sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSingleInstances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 20, groups = {"UC", "Sessions"})
    public void testPOSTSessionInstanceWithCustomFields() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        int type = 1;
        int type2 = 2;
        customFieldsObjects = testDataLoad.getCustomFieldData(type);
        customFieldsObjects1 = testDataLoad.getCustomFieldData(type2);

        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setNoOfInstances("1");
        sessionObject.setDuration(null);
        CustomFields customFields0 = customFieldsObjects.get(0);
        CustomFields customFields1 = customFieldsObjects.get(1);
        CustomFields customFields2 = customFieldsObjects1.get(2);
        CustomFields customFields4 = customFieldsObjects1.get(4);
        ArrayList sessionCustomFieldList = new ArrayList();
        sessionCustomFieldList.add(customFields0);
        sessionCustomFieldList.add(customFields1);
        sessionCustomFieldList.add(customFields2);
        sessionCustomFieldList.add(customFields4);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setCustomFields(sessionCustomFieldList);

        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("PUT Session with custom fields ------------------------------------------", true);

        customFieldsObjects = testDataLoad.getCustomFieldData(type);
        customFieldsObjects1 = testDataLoad.getCustomFieldData(type2);
        CustomFields customFields3 = customFieldsObjects1.get(3);
        CustomFields customFields5 = customFieldsObjects1.get(5);
        sessionCustomFieldList.clear();
        sessionCustomFieldList.add(customFields3);
        sessionCustomFieldList.add(customFields5);

        sessionObject.setCustomFields(sessionCustomFieldList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully");
        testCase = sessionManagement.putSessionWithCustomFields(accountCode, eventCode, uniqueCode, sessionObject, expectedCondition, null);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("PUT Session with invalid custom fields code ------------------------------------------", true);

        customFieldsObjects = testDataLoad.getCustomFieldData(type);
        customFieldsObjects1 = testDataLoad.getCustomFieldData(type2);
        CustomFields customFields6 = customFieldsObjects1.get(6);
        sessionCustomFieldList.clear();
        sessionCustomFieldList.add(customFields3);
        sessionCustomFieldList.add(customFields6);

        sessionObject.setCustomFields(sessionCustomFieldList);

        String message = "Session was updated with the following warnings. Some custom fields (" + customFields6.getCode() + ") were not part of the session layout or were invalid  data, so they were ignored while creating the session";

        expectedCondition = new ExpectedCondition(200, message);
        testCase = sessionManagement.putSessionWithCustomFields(accountCode, eventCode, uniqueCode, sessionObject, expectedCondition, customFields6.getCode());
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("PUT Session with invalid custom fields confield option code ------------------------------------------", true);

        customFieldsObjects = testDataLoad.getCustomFieldData(type);
        customFieldsObjects1 = testDataLoad.getCustomFieldData(type2);
        CustomFields customFields7 = customFieldsObjects1.get(7);
        sessionCustomFieldList.clear();
        sessionCustomFieldList.add(customFields7);

        sessionObject.setCustomFields(sessionCustomFieldList);
        String invalidField = customFields7.getCustomFieldResponse().get(0).getConfFieldOptionCode();
        message = "Session was updated with the following warnings. Some custom fields (" + customFields7.getCode() + ") were not part of the session layout or were invalid  data, so they were ignored while creating the session";

        expectedCondition = new ExpectedCondition(200, message);
        testCase = sessionManagement.putSessionWithCustomFields(accountCode, eventCode, uniqueCode, sessionObject, expectedCondition, invalidField);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"postSpeaker"})
    public void testPostSpeaker() throws Exception {
        for (int i = 0; i < 2; i++) {
            String uniqueCode = randomString(12);
            List<SpeakersObject> speakersObjects = testDataLoad.getUCSpeakersObjData();
            SpeakersObject speakersObject = new SpeakersObject();
            speakersObject = speakersObjects.get(0);
            String speakerEmail = uniqueCode + "@gmail.com";
            speakersObject.setEmail(speakerEmail);
            speakersObject.setPin(speakersObject.getPin() + randomNumber(3));
            speakersObject.setFirstName("Speaker " + uniqueCode);
            String speakerPin = speakersObject.getPin();
            speakerPinList.add(speakerPin);
            String firstName = speakersObject.getFirstName();
            ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " added to event successfully");
            TestCase testCase = speakerManagement.postSpeaker(accountCode, eventCode, speakersObject, expectedCondition, false);
            if (testCase.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
                //String speakerId = String.valueOf(testCase.getSpeakerId());
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
        Reporter.log("Speaker pin list " + speakerPinList);
    }

    @Test(enabled = true, groups = {"postLocations"})
    public void testPostLocation() throws Exception {
        for (int i = 0; i < 2; i++) {
            List<LocationsObject> locationObjects = new ArrayList<>();
            LocationManagement locationManagement = new LocationManagement();
            LocationsObject locationsObject = new LocationsObject();
            locationObjects = testDataLoad.getUCLocationObjData();
            String uniqueCode = randomString(6);
            locationsObject = locationObjects.get(0);
            String locationName = locationsObject.getLocationName();
            locationsObject.setLocationCode(uniqueCode);
            locationCodeList.add(uniqueCode);
            ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
            TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
            if (testCase.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
    }

    @Test(enabled = true, priority = 30, groups = {"postSessionMasterSpeakers"}, dependsOnGroups = {"postSpeaker", "postLocations"})
    public void testPOSTAndPUTSessionWithSpeakersWithInstance() throws Exception {

        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        sessionObject.setDuration(null);
        List<Occurrences> occurrencesObjects = testDataLoad.getSessionOccurancesData();
        Occurrences occurances = occurrencesObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        occurances.setLocationCode(locationCodeList.get(0));
        occurances.setSpeakers(speakerPinList.get(0) + "|" + speakerPinList.get(1));

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        TestCase testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        sessionObject.setSessionCode(randomString(5));
        sessionOccurancesList.clear();
        occurances = occurrencesObjects.get(0);

        occurances.setLocationCode(locationCodeList.get(1));
        occurances.setSpeakers(speakerPinList.get(0) + "|" + speakerPinList.get(1));

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict with speaker");
        testCase = sessionManagement.postSessionWithSingleInstances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        Reporter.log("---------Session deleted---------------------------------------", true);
        testCase = sessionManagement.deleteSession(accountCode, eventCode, uniqueCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);


    }

}
