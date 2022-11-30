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
public class Sessions extends CertainAPIBase {

    private static int noOfInstances;
    private static String multiInstanceSessionCode;
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private SessionManagement sessionManagement = new SessionManagement();
    List<String> speakerPinList = new ArrayList<>();
    List<String> speakerNames = new ArrayList<>();
    private LocationManagement locationManagement = new LocationManagement();
    private SpeakerManagement speakerManagement = new SpeakerManagement();
    //private List<SessionObject> sessionObjects = new ArrayList<>();
    private List<Occurrences> occurrencesObjects = new ArrayList<>();
    private List<CustomFields> customFieldsObjects = new ArrayList<>();
    private List<CustomFields> customFieldsObjects1 = new ArrayList<>();
    private List<Tags> tagsList = new ArrayList<>();
    private List<LocationsObject> locationObjects = new ArrayList<>();
    private ArrayList<String> industry = new ArrayList<>();
    private ArrayList<String> jobFunctions = new ArrayList();
    private int sessionDuration, sessionInstanceId, sessionInstanceIdReq;
    private String sessionCode, sessionName, sessionCodeReq, sessionNameReq, sessionCodeWithJF, sessionCodeWithFees;
    private String accountCode;
    private String eventCode;
    private String sessionTrack = TRACK_NAME;
    private String industries = CONF_INDUSTRY;
    private String jobFunction = CONF_JOB_FUNCTION;
    // String speakerPin;
    private SessionObject sessionObject = new SessionObject();
    private String locationName, locationCode;
    private String schedulerStartTime, schedulerEndTime, startTime;
    private SessionFees sessionFeeObject, sessionCancelFeeObject;
    private int accountId;
    private String[] includeList = {
            "customFields", "confIndustry", "confJobFunction", "tags"
    };

    // private boolean sessionLayout = SESSIONLAYOUT;
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;
        accountId = getAccountId();
        Reporter.log("Accound id is " + accountId);
        /*System.out.println("session layout value is " + sessionLayout);
        if (sessionLayout) {
            throw new SkipException("skipped Session Layout value is true and it is simple session.");
        }
        System.out.println("hello sessions");*/
        try {
            tagsList = testDataLoad.getTagsData(1, accountId);

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
            //  sessionObjects = testDataLoad.getUCSessionsObjData();
            locationObjects = testDataLoad.getUCLocationObjData();
            occurrencesObjects = testDataLoad.getSessionOccurancesData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(enabled = true, groups = {"postSession", "UC", "Sessions"})
    public void testPOSTSessionSingleInstance() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        String startTime = du.today(du.LONGDATE_AM);
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        ArrayList<String> industryList = new ArrayList<>();
        ArrayList<String> jobFunctionsList = new ArrayList();
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("Session " + uniqueCode);
        sessionObject.setNoOfInstances("3");
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_FEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_FEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_CANCFEE " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
    public void testPUTUpdateSessionFeesDetailsSCM34519() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(sessionCodeWithFees);
        sessionObject.setName(sessionObject.getName());
        sessionObject.setEventTrack(sessionTrack);
        ArrayList arrayList = new ArrayList();
        sessionObject.setConfIndustry(null);
        sessionObject.setNoOfInstances("1");
        sessionObject.setConfJobFunction(null);
        sessionFeeObject.setCode(null);
        sessionFeeObject.setExpDateInString(null);

        sessionFeeObject = testDataLoad.getSessionFees(300.0, false, 5);
        sessionCancelFeeObject = testDataLoad.getSessionFees(130.0, true, 3);

        arrayList.add(sessionFeeObject);
        arrayList.add(sessionCancelFeeObject);
        arrayList.add(testDataLoad.getSessionFees(55.0, false, 3));
        sessionObject.setSessionFees(arrayList);
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Two fees cannot have same expiration date and hour");
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(0);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SESS_TAGS " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setTags(tagsList);
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

    @Test(enabled = true, groups = {"getSession", "UC", "Sessions"})
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
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

    @Test(enabled = true, groups = {"postLocationssession"})
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

    @Test(enabled = true, priority = 10, groups = {"postSessionWithMultipleOccurances", "UC", "Sessions"}, dependsOnGroups = "postLocationssession")
    public void testPOSTSessionWithMultipleOccurances() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        System.out.println("----Session object value ---" + sessionObject);

        Occurrences occurances = occurrencesObjects.get(0);
        Occurrences occurances1 = occurrencesObjects.get(1);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        occurances.setLocationCode(locationCode);
        occurances1.setLocationCode(locationCode);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionOccurancesList.add(occurances1);
        sessionObject.setOccurances(sessionOccurancesList);

        ExpectedCondition expectedCondition = new ExpectedCondition(400, "The numberOfInstances (3) and the instances array count (2) are different, Please correct the numberOfInstances count, or remove the attribute from payload to allow auto-calculation.");
        TestCase testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            // Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        sessionObject.setNoOfInstances("2");
        String pattern = "[0-9]+";
        String regex = "\\(" + uniqueCode + "_" + pattern + "," + uniqueCode + "_" + pattern + "\\)";
        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The end dates on instances " + regex + " were ignored as they were auto-calculated from the Start Date and Duration");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        sessionObject.setSessionCode(randomString(5));

        expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict at the location : " + locationName);
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, priority = 11, groups = {"UC", "Sessions"}, dependsOnGroups = "postLocationssession")
    public void testPOSTSessionWithInvalidStartDateOccurances() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        System.out.println("----Session object value ---" + sessionObject.getCeuCredits());
        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("1");

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

        occurances.setEndTime("06/29/2019 2:45 pm");
        occurances.setStartTime(null);
        sessionOccurancesList.add(occurances);

        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "startTime is Mandatory to create an Instance for Conference Session. Format: (MM/dd/yyyy h:mm a)");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, priority = 11, groups = {"UC", "Sessions"})
    public void testPOSTSessionInstanceWithCustomFields() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        int type = 1;
        int type2 = 2;
        customFieldsObjects = testDataLoad.getCustomFieldData(type);
        customFieldsObjects1 = testDataLoad.getCustomFieldData(type2);

        SessionObject sessionObject = sessionObjects.get(0);
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

    @Test(enabled = true, priority = 12, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithCeuCreditSessioncodeValidations() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        System.out.println("----Session object value ---" + sessionObject.toString());

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        sessionObject.setCeuCredits("22.222980");
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "CEU Credits must be less than 50 and have no more than two decimal places. Example:49.99");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        sessionObject.setCeuCredits(null);

        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        expectedCondition = new ExpectedCondition(400, "Session code already exists");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);


        sessionObject.setSessionCode(null);

        expectedCondition = new ExpectedCondition(400, "You must specify Session Code.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);


    }

    @Test(enabled = true, priority = 13, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithNoOfInstancesValidation() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);

        System.out.println("----Session object value ---" + sessionObject);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        sessionObject.setNoOfInstances("0");
        ExpectedCondition expectedCondition = new ExpectedCondition(400, "Number of instances cannot be less than 1 or greater than 99.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);


        sessionObject.setNoOfInstances("100");
        expectedCondition = new ExpectedCondition(400, "Number of instances cannot be less than 1 or greater than 99.");
        testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);


    }

    @Test(enabled = true, priority = 14, groups = {"UC", "Sessions"})
    public void testPOSTSessionWithInstancesWarnings() throws Exception {
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
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        Occurrences occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setDuration("20");

        occurances.setLocationCode(locationCode);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        Reporter.log("POST Session with start and end time where duration not matched ------------------------", true);

        String pattern = "[0-9]+";
        String regex = "\\(" + uniqueCode + "_" + pattern + "\\)";
        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The end dates on instances " + regex + " were ignored as they were auto-calculated from the Start Date and Duration");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("POST Session with start and end time when duration is not passed.------------------------", true);


        uniqueCode = randomString(7);
        sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        occurances = occurrencesObjects.get(0);
        occurances = occurrencesObjects.get(0);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setDuration(null);

        occurances.setLocationCode(locationCode);
        occurances.setStartTime("06/29/2017 6:30 pm");
        sessionOccurancesList = new ArrayList();
        sessionOccurancesList.clear();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        pattern = "[0-9]+";
        regex = "\\(" + uniqueCode + "_" + pattern + "\\)";
        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The end dates on instances " + regex + " were ignored as they were auto-calculated with a 60 minute Duration");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Reporter.log("sessionInstanceId = " + testCase.getSessionInstanceId(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"UC", "Sessions"}, priority = 15)
    public void testPOSTSessionWithDocDataSCM33692() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(28);
        String expectedMessage = "CreateSource should be API.";
        int statusCode = 400;
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"UC", "Sessions"}, priority = 16)
    public void testPOSTSessionWithInvalidPointsSCM33922() throws Exception {
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(30);
        sessionObject.setPoints("ytty");
        String expectedMessage = "Points must contain only numbers.";
        int statusCode = 400;
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);
    }

    @Test(enabled = true, groups = {"UC", "Sessions"}, priority = 17)
    public void testGETSessionByDepthSCM34564() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, "Depth", "Complete", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 18, groups = {"putSessionWithSingleOccurancesCases", "UC", "Sessions"})
    public void testPUTSessionWithSingleOccurancesSCM34473() throws Exception {

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("------------POST Session with single occurrances-----------------------", true);

        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(31);
        Occurrences occurances = occurrencesObjects.get(31);

        sessionObject.setEventTrack(sessionTrack);
        occurances.setLocationCode(locationCode);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);
        String sessionCode = sessionObject.getSessionCode();
        System.out.println("-----------Occurences list " + sessionOccurancesList.get(0));
        String SessionInstanceCode = null;
        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            SessionInstanceCode = sessionCode + "_" + testCase.getSessionInstanceId();
            Reporter.log("SessionInstanceCode = " + SessionInstanceCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------PUT Session instance with all fields--------------------------------------------", true);

        //String uniqueCode = randomString(12);

        sessionObject.setSessionCode(sessionCode);
        sessionObject.setName(sessionObject.getName() + "Updated");

        occurances = occurrencesObjects.get(31);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setLocationCode(locationCode);
        occurances.setCapacity("2");
        occurances.setOverrideCapacity(true);

        sessionOccurancesList.clear();

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------PUT Session instance without startTime fields SCM34908--------------------------------------------", true);

        occurances = occurrencesObjects.get(31);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setLocationCode(locationCode);
        occurances.setCapacity(null);
        occurances.setStartTime(null);
        occurances.setOverrideCapacity(false);

        sessionOccurancesList.clear();

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------PUT Session instance without location fields--------------------------------------------", true);

        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setLocationCode(null);
        occurances.setOverrideCapacity(true);

        sessionOccurancesList.clear();

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("---------------------PUT Session with invalid instanec code SCM-33952-------------------------------------------", true);

        String instanceCode = SessionInstanceCode + "2345";
        occurances.setInstanceCode(instanceCode);
        occurances.setLocationCode(null);
        occurances.setOverrideCapacity(true);

        sessionOccurancesList.clear();

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(404, "Invalid Instance Code (" + instanceCode + ").");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("----------------------------PUT Session with duration SCM-34419 --------------------------------------------", true);

        sessionObject.setDuration("20");
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setLocationCode(null);
        occurances.setOverrideCapacity(true);

        sessionOccurancesList.clear();

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(null);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @Test(enabled = true, groups = {"getSession", "UC", "Sessions"})
    public void testGETSessionWithPublishedFilters() throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, "isPublished", "true", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("Event has no sessions", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        testCase = sessionManagement.getSessions(accountCode, eventCode, "isPublished", "false", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("Event has no sessions", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 19, groups = {"putSessionWithMultipleOccurancesAndInstances", "UC", "Sessions"})
    public void testPUTSessionWithMultipleOccurancesWithInstancesSCM34365() throws Exception {

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("------------POST Session with multiple occurrances-----------------------", true);

        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(31);
        Occurrences occurances = occurrencesObjects.get(31);
        Occurrences occurances1 = occurrencesObjects.get(0);
        sessionObject.setNoOfInstances("2");
        sessionObject.setEventTrack(sessionTrack);
        occurances.setLocationCode(locationCode);
        occurances1.setLocationCode(locationCode);

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionOccurancesList.add(occurances1);
        sessionObject.setOccurances(sessionOccurancesList);
        String sessionCode = sessionObject.getSessionCode();

        String SessionInstanceCode = null;
        String pattern = "[0-9]+";
        String regex = "\\(" + sessionCode + "_" + pattern + "," + sessionCode + "_" + pattern + "\\)";
        expectedCondition = new ExpectedCondition(200, "Session was created with the following warnings. The end dates on instances " + regex + " were ignored as they were auto-calculated from the Start Date and Duration");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            SessionInstanceCode = sessionCode + "_" + testCase.getSessionInstanceId();
            Reporter.log("SessionInstanceCode = " + SessionInstanceCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------PUT Session with 2 dummy or unschedule instance with all fields--------------------------------------------", true);

        //String uniqueCode = randomString(12);

        sessionObject.setSessionCode(sessionCode);
        sessionObject.setName(sessionObject.getName() + "Updated");

        sessionObject.setNoOfInstances("4");
        sessionObject.setOccurances(null);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("---------------PUT Session with less number of instances -----------------------------------------", true);

        sessionObject.setSessionCode(sessionCode);

        sessionObject.setNoOfInstances("1");

        expectedCondition = new ExpectedCondition(400, "You have scheduled 2 instances please provide correct value for number of Instances.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------------PUT Session with no of array and instanvces are not equal--------------------------", true);

        sessionObject.setSessionCode(sessionCode);
        occurances = occurrencesObjects.get(31);

        sessionObject.setNoOfInstances("5");
        sessionObject.setOccurances(null);

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "The numberOfInstances (5) and the instances array count (3) are different, Please correct the numberOfInstances count, or remove the attribute from payload to allow auto-calculation.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-----------------PUT Session with no of array and instanvces are equal--------------------------", true);

        sessionObject.setSessionCode(sessionCode);
        occurances = occurrencesObjects.get(31);

        sessionObject.setNoOfInstances("1");
        sessionObject.setOccurances(null);

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "The numberOfInstances (1) and the instances array count (4) are different, Please correct the numberOfInstances count, or remove the attribute from payload to allow auto-calculation.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-------------------PUT Session with less no of instances with with comibined of scheduled + unscheduled----------------", true);

        sessionObject.setSessionCode(sessionCode);

        sessionObject.setNoOfInstances("2");
        sessionObject.setOccurances(null);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 20, groups = {"UC", "Sessions"})
    public void testPUTSessionWithZeroInstancesValidationSCM34248() throws Exception {
        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(22);

        System.out.println("----Session object value ---" + sessionObject);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setEventTrack(sessionTrack);

        sessionObject.setNoOfInstances(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

        Reporter.log("-----------------SCM-34248 PUT Session with zero instances --------------------------------------", true);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setNoOfInstances("0");
        sessionObject.setOccurances(null);

        expectedCondition = new ExpectedCondition(400, "Number of instances cannot be less than 1 or greater than 99.");
        testCase = sessionManagement.putSession(accountCode, eventCode, uniqueCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false);

    }

    @Test(enabled = true, priority = 21, groups = {"putSessionWithOccurancesAndTags", "UC", "Sessions"})
    public void testPUTSessionWithOccurancesAndTags() throws Exception {

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String uniqueCode = randomString(8);
        locationsObject.setLocationName(uniqueCode);
        locationsObject.setLocationCode(uniqueCode);
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("------------POST Session with multiple occurrances-----------------------", true);

        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        sessionObject = sessionObjects.get(31);
        Occurrences occurances = occurrencesObjects.get(31);
        sessionObject.setEventTrack(sessionTrack);
        occurances.setLocationCode(locationCode);
        sessionObject.setTags(tagsList);
        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);
        String sessionCode = sessionObject.getSessionCode();

        String SessionInstanceCode = null;
        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.postSessionWithOccurances(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            SessionInstanceCode = sessionCode + "_" + testCase.getSessionInstanceId();
            Reporter.log("SessionInstanceCode = " + SessionInstanceCode, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    @DataProvider(name = "get-sessions-dp3", parallel = false)
    public Object[][] getSessionsFiltersObjects() throws Exception {
        System.out.println("session object result" + sessionObject);
        Object obj = sessionManagement.getSessionObjFilters(sessionObject);
        return this.testDataLoad.getKeyValuePairFromObject(obj);
    }


    @Test(dataProvider = "get-sessions-dp3", enabled = true, groups = {"UC", "Sessions"}, priority = 21, dependsOnGroups = "putSessionWithOccurancesAndTags")
    public void testGETSessionWithSearchFiltersForSessions(String filter, String value) throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @DataProvider(name = "get-session-includeList", parallel = false)
    public Object[][] getSessionIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @Test(dataProvider = "get-session-includeList", enabled = true, groups = {"UC", "Sessions"}, priority = 22)
    public void testGETSessionWithIncludeListForSessions(String includeList) throws Exception {
        TestCase testCase = sessionManagement.getSessions(accountCode, eventCode, new String[]{includeList}, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No sessions found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"postSpeaker"})
    public void testPostSpeaker() throws Exception {
        for (int i = 0; i < 12; i++) {
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

    @Test(enabled = true, priority = 30, groups = {"postSessionMasterSpeakers"}, dependsOnGroups = "postSpeaker")
    public void testPOSTAndPUTSessionWithMasterSpeakersWithoutInstance() throws Exception {

        Reporter.log("----POST Session with master speakers-----------------------------------------------------", true);

        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(32);
        String sessionName = sessionObject.getName();
        String sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setSpeakers(speakerPinList.get(0) + "|" + speakerPinList.get(1));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Session added to event successfully");
        TestCase testCase = sessionManagement.postSession(accountCode, eventCode, sessionObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("----------------PUT Session with master and instance level speaker-------------------------------", true);

        StringBuilder instanceSpeakers = new StringBuilder();
        instanceSpeakers.append(speakerPinList.get(2));
        instanceSpeakers.append("|");
        instanceSpeakers.append(speakerPinList.get(3));

        sessionObjects.clear();
        sessionObjects = testDataLoad.getUCSessionsObjData();
        sessionObject = sessionObjects.get(32);
        sessionObject.setName(sessionName);
        sessionObject.setSessionCode(sessionCode);
        sessionObject.setSpeakers(null);
        sessionObject.setNoOfInstances(null);
        sessionObject.setSpeakers(speakerPinList.get(1));
        Occurrences occurances = occurrencesObjects.get(32);
        Occurrences occurances1 = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setEndTime(null);
        occurances1.setLocationCode(locationCode);
        occurances1.setEndTime(null);
        occurances1.setSpeakers(instanceSpeakers.toString());

        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionOccurancesList.add(occurances1);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 31, groups = {"postSessionMasterSpeakers"}, dependsOnGroups = "postSpeaker")
    public void testPOSTAndPutSessionWithMasterSpeakersWithInstance() throws Exception {

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----POST Session with master speakers-----------------------------------------------------", true);

        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(33);
        String sessionName = sessionObject.getName();
        String sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setSpeakers(speakerPinList.get(4));
        //  sessionObject.setSpeakers("DYQAB493473");
        sessionObject.setNoOfInstances("1");
        Occurrences occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setEndTime(null);
        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, null, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            sessionInstanceId = testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("----------------PUT Session with master and instance level speaker-------------------------------", true);

        sessionObjects.clear();
        sessionObjects = testDataLoad.getUCSessionsObjData();
        sessionObject = sessionObjects.get(33);
        sessionObject.setName(sessionName);
        sessionObject.setSessionCode(sessionCode);
        sessionObject.setSpeakers(null);
        sessionObject.setSpeakers(speakerPinList.get(4) + "|" + speakerPinList.get(5));
        //sessionObject.setSpeakers("DYQAB493473"+"|"+"Z8P6B493994");
        sessionObject.setNoOfInstances("2");

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 32, groups = {"postSessionMasterSpeakers"}, dependsOnGroups = "postSpeaker")
    public void testPOSTAndPutSessionWithMasterAndInstanceSpeakersWithInstance() throws Exception {
        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

        LocationsObject locationsObject = locationObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----POST Session with master speakers and instance level speakers-----------------------------------------------------", true);

        String uniqueCode = randomString(12);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(33);
        String sessionName = sessionObject.getName();
        String sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setSpeakers(speakerPinList.get(6));
        sessionObject.setNoOfInstances("1");
        Occurrences occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setEndTime(null);
        occurances.setSpeakers(speakerPinList.get(7));
        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);
        String SessionInstanceCode = null;
        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, null, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            SessionInstanceCode = sessionCode + "_" + testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        Reporter.log("----------------PUT Session with master and instance level speaker-------------------------------", true);

        sessionObject = sessionObjects.get(33);
        sessionObject.setName(sessionName);
        sessionObject.setSessionCode(sessionCode);
        sessionObject.setSpeakers(null);
        sessionObject.setSpeakers(speakerPinList.get(6) + "|" + speakerPinList.get(8));

        sessionObject.setOccurances(null);
        sessionOccurancesList.clear();

        occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setSpeakers(speakerPinList.get(7) + "|" + speakerPinList.get(9));
        occurances.setEndTime(null);
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------PUT Session with instance level speaker empty-------------------------------", true);

        sessionObject.setOccurances(null);
        sessionOccurancesList.clear();

        occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setSpeakers("");
        occurances.setEndTime(null);
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "Scheduled instance should have at least one speaker.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------PUT Session with master speaker empty-------------------------------", true);

        sessionObject.setSpeakers("");
        sessionOccurancesList.clear();
        occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setEndTime(null);
        occurances.setSpeakers(speakerPinList.get(7) + "|" + speakerPinList.get(9));

        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------PUT Session with instance level speaker empty-------------------------------", true);

        sessionObject.setOccurances(null);
        sessionOccurancesList.clear();

        occurances = occurrencesObjects.get(33);
        occurances.setLocationCode(locationCode);
        occurances.setInstanceCode(SessionInstanceCode);
        occurances.setSpeakers("");
        occurances.setEndTime(null);
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(200, "Session updated successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, priority = 33, groups = {"postSessionMasterSpeakers"}, dependsOnGroups = "postSpeaker")
    public void testPOSTAndPutSessionWithSpeakerConflict() throws Exception {

        Reporter.log("--------------------POST Location---------------------------------------------------------", true);

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
        locationsObject = locationObjects.get(1);
        String locationCode1 = uniqueCode;
        locationsObject.setLocationCode(locationCode1);
        expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----POST Session speaker conflict error-----------------------------------------------------", true);

        uniqueCode = randomString(10);
        List<SessionObject> sessionObjects = testDataLoad.getUCSessionsObjData();
        SessionObject sessionObject = sessionObjects.get(34);
        String sessionName = sessionObject.getName();
        String sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("2");

        String speakerName = speakerPinList.get(10) + "|" + speakerPinList.get(11);
        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(getOccurrences(locationCode, speakerName));
        sessionOccurancesList.add(getOccurrences(locationCode1, speakerName));
        sessionObject.setOccurances(sessionOccurancesList);

        String SessionInstanceCode = null;
        expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict with speaker");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, null, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("------------------------POST Session -------------------------------------------------", true);

        sessionObjects.clear();
        sessionObjects = testDataLoad.getUCSessionsObjData();
        sessionObject = sessionObjects.get(34);
        sessionName = sessionObject.getName();
        sessionCode = sessionObject.getSessionCode();
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances("1");
        sessionObject.setOccurances(null);

        expectedCondition = new ExpectedCondition(200, "Session added to event successfully.");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, null, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            SessionInstanceCode = sessionCode + "_" + testCase.getSessionInstanceId();
            Reporter.log("sessionInstanceId = " + sessionInstanceId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("----------------PUT Session with master and instance level speaker-------------------------------", true);

        sessionObject = sessionObjects.get(34);
        sessionObject.setEventTrack(sessionTrack);
        sessionObject.setNoOfInstances(null);
        sessionObject.setSessionCode(sessionCode);

        sessionOccurancesList.clear();
        Occurrences occurrences = getOccurrences(locationCode, speakerName);
        occurrences.setInstanceCode(SessionInstanceCode);
        sessionOccurancesList.add(occurrences);
        sessionOccurancesList.add(getOccurrences(locationCode1, speakerName));
        sessionObject.setOccurances(sessionOccurancesList);

        expectedCondition = new ExpectedCondition(400, "There is a time schedule conflict with speaker :");
        testCase = sessionManagement.putSessionWithSpeakersOccurances(accountCode, eventCode, sessionCode, sessionObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


    }

    private Occurrences getOccurrences(String locationCode, String speakerName) {
        Occurrences occurrences = new Occurrences();
        occurrences.setInstanceCode(occurrencesObjects.get(34).getInstanceCode());
        occurrences.setEndTime(null);
        occurrences.setSpeakers(speakerName);
        occurrences.setLocationCode(locationCode);
        occurrences.setStartTime(occurrencesObjects.get(34).getStartTime());
        occurrences.setOverrideCapacity(occurrencesObjects.get(34).isOverrideCapacity());
        occurrences.setCapacity(occurrencesObjects.get(34).getCapacity());
        occurrences.setWaitlistedLimit(occurrencesObjects.get(34).getWaitlistedLimit());
        return occurrences;
    }

}