package com.certain.usecases;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.pojo.sessions.Occurrences;
import internal.qaauto.certain.platform.pojo.sessions.SessionObject;
import internal.qaauto.certain.platform.pojo.speakers.SpeakersObject;
import internal.qaauto.certain.platform.services.LocationManagement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class UCCustomerUseCase1 extends CertainAPIBase {

    private static String sessionCode;
    private static String sessionName;
    private static String registrationCode;
    private static String eventCode;
    private static int sessionInstanceId;
    private final UCAPICommon ucapiCommon = new UCAPICommon();
    private final AuthenticationScheme auth = new AuthenticationScheme();
    private final ArrayList<String> registrationCodes = new ArrayList<>();
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final int noOfConcurrentRegistrations = Integer.parseInt(config.getVal("concurrent.reg.size"));
    private final String sessionCapacity = config.getVal("capacity");
    private final int registeredUserProfiles = noOfConcurrentRegistrations / 2;
    private final int noOfSpeakersToTheSession = Integer.parseInt(config.getVal("speakers.size"));
    private final int preAssigned = registeredUserProfiles / 2;
    private final List<HashMap<String, Object>> registrations = new ArrayList<>();
    private final String[] filters = {"firstName_like",
            "lastName_like",
            "email_like",
            "email", "phone"
    };
    private final String[] statuses = {"Attended", "Cancelled"};
    private List<SessionObject> sessionObjects = new ArrayList<>();
    private List<LocationsObject> locationsObjects = new ArrayList<>();
    private List<SpeakersObject> speakersObjects = new ArrayList<>();
    private int registered = 0;
    private List<Occurrences> occurrencesObjects = new ArrayList<>();
    private LocationManagement locationManagement = new LocationManagement();
    private boolean sessionLayout = Boolean.valueOf(config.getVal("sessionLayout"));
    private int cancelled = 0;
    private int attended = 0;
    private int availableCount = 0;
    private int failedToAssignSession = 0;
    private String accountCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        auth.setUsername(USERNAME);
        auth.setPassword(PASSWORD);
        try {
            //eventCode = config.getVal("event.code");
            sessionObjects = testDataLoad.getUCSessionsObjData();
            locationsObjects = testDataLoad.getUCLocationObjData();
            speakersObjects = testDataLoad.getUCSpeakersObjData();
            occurrencesObjects = testDataLoad.getSessionOccurancesData();
            //create test data for
            for (int i = 0; i < noOfConcurrentRegistrations; i++) {
                String uniQNo = randomString(7);
                HashMap<String, Object> profileObject = new HashMap<>();
                profileObject.put("pin", randomString(9));
                String FN = "UC1-REG-FN" + uniQNo;
                profileObject.put("firstName", FN);
                profileObject.put("lastName", "UC1-REG-LN" + uniQNo);
                profileObject.put("email", FN + "@microsoft.com");
                profileObject.put("phone", randomNumber(3) + "-" + randomNumber(3) + "-" + randomNumber(4));
                registrations.add(profileObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setup Session and Publish

    @Test(groups = {"Step01", "UCCustomerUseCase1"})
    public void Step01_POST_Session() throws Exception {
        //POST New Location
        locationsObjects = testDataLoad.getUCLocationObjData();
        LocationsObject locationsObject = locationsObjects.get(0);
        String locationName = locationsObject.getLocationName();
        String locationCode = locationsObject.getLocationCode();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Location added to event successfully");
        TestCase testCase = locationManagement.postLocation(accountCode, eventCode, locationsObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());


        String uniqueCode = randomString(7);
        SessionObject sessionObject = sessionObjects.get(22);
        sessionObject.setNoOfInstances("1");
        Occurrences occurances = occurrencesObjects.get(1);

        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setName("SessOccurance " + uniqueCode);
        sessionObject.setDuration("10");
        sessionName = sessionObject.getName();
        occurances.setLocationCode(locationCode);
        occurances.setCapacity(sessionCapacity);
        occurances.setOverrideCapacity(false);
        ArrayList sessionOccurancesList = new ArrayList();
        sessionOccurancesList.add(occurances);
        sessionObject.setOccurances(sessionOccurancesList);
        sessionCode = sessionObject.getSessionCode();
        String expectedMessage;
        expectedMessage = "Session was created with the following warnings.";
        //Reporter.log("POST Session with invalid location code under instances ------------------------", true);
        expectedCondition = new ExpectedCondition(200, expectedMessage);
        TestCase postSession = ucapiCommon.PostSession(eventCode, sessionObject, expectedCondition, accountCode);
        if (postSession.isPassed()) {
            Reporter.log(postSession.getMessage());
            sessionInstanceId = Integer.parseInt(postSession.getSessionInstanceIds().get(0));
            Reporter.log("Session posted successfully...session instance id " + sessionInstanceId);
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, postSession.getMessage());
        /*String uniqueCode = randomString(15);
        SessionObject sessionObject = sessionObjects.get(1);
      //  sessionObject.setCapacity(sessionCapacity);
        sessionObject.setSessionCode(uniqueCode);
        sessionObject.setEventTrack(TRACK_NAME);
        sessionObjects.get(0).setName("UC1-"+uniqueCode);
        sessionCode = sessionObject.getSessionCode();
        sessionName = sessionObject.getName();

        ExpectedCondition expectedCondition = new ExpectedCondition(200,"Session added to event successfully");
        TestCase postSession = ucapiCommon.PostSession(eventCode, sessionObject, expectedCondition);
        if (postSession.isPassed()) {
            Reporter.log(postSession.getMessage());
            sessionInstanceId = Integer.parseInt(postSession.getSessionInstanceIds().get(0));
            Reporter.log("Session posted successfully...session instance id " + sessionInstanceId);
            Assert.assertTrue(true);
        }else
            Assert.assertTrue(false, postSession.getMessage());*/
    }

    @Test(enabled = false, groups = {"Step02", "UCCustomerUseCase1"}, dependsOnGroups = "Step01")
    public void Step02_SCHEDULE_Session() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        expectedCondition.setMessage("Location added to event successfully");
        String locationCode = locationsObjects.get(0).getLocationCode();
        TestCase postLocation = ucapiCommon.PostLocation(eventCode, locationsObjects.get(0), expectedCondition, accountCode);
        if (postLocation.isPassed()) {
            Reporter.log(postLocation.getMessage());
            expectedCondition.setMessage("Session is successfully scheduled at  " + locationsObjects.get(0).getLocationName());
            TestCase scheduleIt = ucapiCommon.ScheduleSession(eventCode, locationCode, sessionInstanceId, du.today(du.LONGDATE_AM), expectedCondition, accountCode);
            if (scheduleIt.isPassed()) {
                Reporter.log(scheduleIt.getMessage());
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, scheduleIt.getMessage());
        } else
            Assert.assertTrue(false, postLocation.getMessage());

    }

    @DataProvider(name = "post-speaker-dp1")
    public Object[][] speakersData() throws Exception {
        Object[][] speakerDO = new Object[noOfSpeakersToTheSession][3];
        for (int i = 0; i < noOfSpeakersToTheSession; i++) {
            String uniQNo = randomNumber(6);
            String fName = "SPKRFN" + uniQNo;
            speakerDO[i][0] = fName;
            speakerDO[i][1] = "SPKRLN" + uniQNo;
            speakerDO[i][2] = fName + "@gmail.com";
        }
        Reporter.log("----------" + speakerDO, true);
        return speakerDO;
    }

    @Test(dataProvider = "post-speaker-dp1", groups = {"Step03", "UCCustomerUseCase1"}, dependsOnGroups = "Step01")
    public void Step03_ASSIGN_Speakers(String firstName, String lastName, String email) throws Exception {
        Reporter.log("--------------Assign speakers test case ", true);
        int speakerCode;
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        SpeakersObject speakersObject = speakersObjects.get(0);
        speakersObject.setPin(randomString(9));
        speakersObject.setFirstName(firstName);
        speakersObject.setLastName(lastName);
        speakersObject.setEmail(email);
        speakersObject.setPhone(randomNumber(3) + "-" + randomNumber(3) + "-" + randomNumber(4));
        expectedCondition.setMessage("Speaker " + email + " added to event successfully");
        TestCase createSpeaker = ucapiCommon.POSTSpeaker(eventCode, speakersObject, expectedCondition, accountCode);
        if (createSpeaker.isPassed()) {
            speakerCode = ucapiCommon.GETSpeakerId(eventCode, "firstName", firstName, accountCode);
            if (speakerCode != 0) {
                Reporter.log("Speaker created with id " + speakerCode, true);
                expectedCondition.setMessage("Speaker " + email + " has been assigned  to session " + sessionName + " successfully");
                TestCase assignSpeaker = ucapiCommon.assignSessionToSpeaker(eventCode, speakerCode, sessionCode, expectedCondition, accountCode);
                if (assignSpeaker.isPassed()) {
                    Assert.assertTrue(true);
                } else Assert.assertTrue(false, assignSpeaker.getMessage());

            } else Assert.assertTrue(false, "failed to get speaker id");

        } else Assert.assertTrue(false, createSpeaker.getMessage());
    }

    @Test(groups = {"Step04", "UCCustomerUseCase1"}, dependsOnGroups = "Step03")
    public void Step04_PUBLISH_Session() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        expectedCondition.setMessage("Session is published successfully");
        TestCase step4 = ucapiCommon.PublishSession(eventCode, sessionCode, "Attendee", accountCode);
        if (step4.isPassed()) {
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "Failed to publish session " + sessionCode + " " + step4.getMessage());
    }

    @DataProvider(name = "pre-post-reg-dp1", parallel = true)
    public Object[][] registrationsData() throws Exception {
        Object[][] registrationsDO = new Object[registeredUserProfiles][1];
        for (int i = 0; i < registeredUserProfiles; i++) {
            registrationsDO[i][0] = registrations.get(i);
        }
        return registrationsDO;
    }

    @Test(dataProvider = "pre-post-reg-dp1", groups = {"Step05", "UCCustomerUseCase1"}, dependsOnGroups = "Step04")
    public void Step05_PreRegisteredRegistrations(HashMap<String, String> profileObj) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        TestCase postRegistration = ucapiCommon.PostRegistration(eventCode, "Attendee", profileObj, expectedCondition, accountCode);
        if (postRegistration.isPassed()) {
            registrationCode = postRegistration.getRegistrationCode();
            registrationCodes.add(registrationCode);
            Reporter.log(postRegistration.getMessage(), true);
            Assert.assertTrue(true);
        }
    }

    @DataProvider(name = "assign-reg-dp1", parallel = true)
    public Object[][] registrationIdsDO() throws Exception {
        Object[][] registrationsDO = new Object[preAssigned][1];
        for (int i = 0; i < preAssigned; i++) {
            registrationsDO[i][0] = registrationCodes.get(i);
        }
        return registrationsDO;
    }

    @Test(dataProvider = "assign-reg-dp1", groups = {"Step06", "UCCustomerUseCase1"}, dependsOnGroups = "Step05")
    public void Step06_Assign_Registrations(String registrationCode) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);
        expectedCondition.setMessage("Attendee has been successfully registered for the session " + sessionName);
        TestCase assignRegistrationToSession = ucapiCommon.AssignRegistrationToSession(eventCode, registrationCode, sessionInstanceId, expectedCondition, accountCode);
        Reporter.log(assignRegistrationToSession.getMessage(), true);
        if (assignRegistrationToSession.isPassed()) {
            registered++;
            int counts[] = ucapiCommon.getSessionAvailableCount(eventCode, sessionInstanceId, accountCode);
            availableCount = counts[0];
            Reporter.log("Got session counts \nCapacity  : " + counts[0] +
                    "\nRegistered: " + counts[1] +
                    "\nRemaining : " + counts[2], true);
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "Failed to assign registration " + registrationCode);
    }

    // Registrations
    @DataProvider(name = "reg-lookup-dp1", parallel = true)
    public Object[][] registrationsLookup() throws Exception {
        Object[][] registrationsDO = new Object[noOfConcurrentRegistrations][1];
        for (int i = 0; i < noOfConcurrentRegistrations; i++) {
            registrationsDO[i][0] = registrations.get(i);
        }
        return registrationsDO;
    }

    // multiple registration look-up and post new in parallel
    @Test(dataProvider = "reg-lookup-dp1", groups = {"Step07", "UCCustomerUseCase1"}, dependsOnGroups = "Step06")
    public void Step07_REGISTRATION_Lookup(HashMap<String, String> profileObj) throws Exception {
        registrationCode = null;
        String filter = filters[testDataLoad.getRandomNumberInRange(0, filters.length - 1)];
        String hashKey = filter;
        String status = statuses[testDataLoad.getRandomNumberInRange(0, statuses.length - 1)];
        if (filter.contains("_")) {
            hashKey = filter.split("_")[0];
        }
        ExpectedCondition expectedCondition = new ExpectedCondition();
        expectedCondition.setStatusCode(200);

        //GET Registration
        TestCase getRegistration = ucapiCommon.GetRegistration(eventCode, filter, profileObj.get(hashKey), expectedCondition, accountCode);
        Reporter.log(getRegistration.getMessage(), true);

        // registration found check whether he is registered if yes change the status to 'Attended' or 'Cancelled'
        boolean allowWalkingRegistrants = true;
        if (getRegistration.getStatusCode() == 200) {
            String registrationCode = getRegistration.getRegistrationCode();

            //GET SessionRegistrations
            TestCase getSessionRegistrations = ucapiCommon.GETSessionRegistrations(eventCode, sessionInstanceId, registrationCode, expectedCondition, accountCode);
            Reporter.log(getSessionRegistrations.getMessage(), true);

            //IF REGISTRATION REGISTERED
            if (getSessionRegistrations.isPassed()) {

                //GET RegistrationsSession
                TestCase getRegistrationSession = ucapiCommon.GETRegistrationSession(eventCode, sessionCode, registrationCode, null, expectedCondition, accountCode);
                if (getRegistrationSession.getStatusCode() == 200) {
                    TestCase updateRegistrationSessionStatus;

                    //DELETE RegistrationsSession
                    if (status.equals("Cancelled")) {
                        expectedCondition.setMessage("Attendee has been removed from session " + sessionName + " successfully");
                        updateRegistrationSessionStatus = ucapiCommon.CancelRegistrationSession(eventCode, sessionInstanceId, registrationCode, expectedCondition, accountCode);
                    } else {
                        //PUT RegistrationsSession
                        expectedCondition.setMessage("Registration status updated successfully");
                        updateRegistrationSessionStatus = ucapiCommon.UpdateRegistrationSessionStatus(eventCode, registrationCode, status, sessionInstanceId, expectedCondition, accountCode);
                    }

                    if (updateRegistrationSessionStatus.isPassed()) {
                        if (status.equals("Cancelled")) {
                            cancelled++;
                        } else if (status.equals("Attended")) {
                            attended++;
                            // set the default or custom registration property(s) to 'Badge Printed'
                            HashMap<String, Object> regObj = new HashMap<>();
                            regObj.put("isBadgePrinted", true);

                            //POST UPDATE_Registration
                            TestCase updateReg = ucapiCommon.UpdateRegistration(eventCode, registrationCode, regObj, expectedCondition, accountCode);
                            if (updateReg.isPassed()) {
                                Reporter.log("Updated the default or custom registration property(s) 'Badge Printed' to true", true);
                                Assert.assertTrue(true);
                            } else Assert.assertTrue(false, updateReg.getMessage());
                        } else Assert.assertTrue(false, " Failed to assign/cancel the registration");
                    } else Assert.assertTrue(false, updateRegistrationSessionStatus.getMessage());
                }

                // ELSE THE REGISTRATION NOT REGISTERED TO THE SESSION
            } else if (getSessionRegistrations.getStatusCode() == 404) {
                if (allowWalkingRegistrants) {
                    expectedCondition.setMessage("Attendee has been successfully registered for the session " + sessionName);

                    //POST RegistrationSession
                    TestCase assignRegistrationToSession = ucapiCommon.AssignRegistrationToSession(eventCode, registrationCode, sessionInstanceId, expectedCondition, accountCode);
                    if (assignRegistrationToSession.isPassed()) {
                        registered++;
                        Reporter.log(assignRegistrationToSession.getMessage(), true);

                        //PUT RegistrationSession
                        expectedCondition.setMessage("Registration status updated successfully");
                        TestCase updateRegistrationSessionStatus = ucapiCommon.UpdateRegistrationSessionStatus(eventCode, registrationCode, "Attended", sessionInstanceId, expectedCondition, accountCode);
                        if (updateRegistrationSessionStatus.isPassed()) {
                            attended++;
                            Reporter.log(updateRegistrationSessionStatus.getMessage(), true);
                            // set the default or custom registration property(s) to 'Badge Printed'
                            HashMap<String, Object> regObj = new HashMap<>();
                            regObj.put("isBadgePrinted", true);

                            //POST UPDATE_Registration
                            TestCase updateReg = ucapiCommon.UpdateRegistration(eventCode, registrationCode, regObj, expectedCondition, accountCode);
                            if (updateReg.isPassed()) {
                                Reporter.log("Updated the default or custom registration property(s) 'Badge Printed' to true", true);
                                Assert.assertTrue(true);
                            } else Assert.assertTrue(false, updateReg.getMessage());
                        } else Assert.assertTrue(false, updateRegistrationSessionStatus.getMessage());

                    } else {
                        failedToAssignSession++;
                        Assert.assertTrue(true, assignRegistrationToSession.getMessage());
                    }
                } else {
                    Reporter.log("No Walk-in Registrations allowed for the session " + sessionName + " Only Pre-Registered Attendees", true);
                    Assert.assertTrue(true);
                }
            } else
                Assert.assertTrue(false, "Failed to update pre registered attendee ");


            //IF NO REGISTRATION FOUND THEN CONTINUE WITH WALK-IN PROCEDURE
        } else if (getRegistration.getStatusCode() == 404) {
            if (allowWalkingRegistrants) {

                //POST Registration
                TestCase postRegistration = ucapiCommon.PostRegistration(eventCode, "Attendee", profileObj, expectedCondition, accountCode);
                if (postRegistration.isPassed()) {
                    registrationCodes.add(postRegistration.getRegistrationCode());
                    Reporter.log(postRegistration.getMessage(), true);
                    expectedCondition.setMessage("Attendee has been successfully registered for the session " + sessionName);

                    //POST RegistrationSession
                    TestCase assignRegistrationToSession = ucapiCommon.AssignRegistrationToSession(eventCode, postRegistration.getRegistrationCode(), sessionInstanceId, expectedCondition, accountCode);
                    if (assignRegistrationToSession.isPassed()) {
                        expectedCondition.setMessage("Registration status updated successfully");

                        //PUT RegistrationSession
                        TestCase updateRegistrationSessionStatus = ucapiCommon.UpdateRegistrationSessionStatus(eventCode, postRegistration.getRegistrationCode(), "Attended", sessionInstanceId, expectedCondition, accountCode);
                        if (updateRegistrationSessionStatus.isPassed()) {
                            attended++;
                            Reporter.log("Registration added and assigned to session and also changed to 'Attended'", true);
                            // set the default or custom registration property(s) to 'Badge Printed'
                            HashMap<String, Object> regObj = new HashMap<>();
                            regObj.put("isBadgePrinted", true);

                            //POST_UPDATE Registration
                            TestCase updateReg = ucapiCommon.UpdateRegistration(eventCode, postRegistration.getRegistrationCode(), regObj, expectedCondition, accountCode);
                            if (updateReg.isPassed()) {
                                Reporter.log("Updated the default or custom registration property(s) 'Badge Printed' to true", true);
                                Assert.assertTrue(true);
                            } else Assert.assertTrue(false, updateReg.getMessage());
                        } else Assert.assertTrue(false, updateRegistrationSessionStatus.getMessage());

                    } else {
                        failedToAssignSession++;
                        Assert.assertTrue(true, assignRegistrationToSession.getMessage());
                    }
                }
            } else {
                Reporter.log("No Walk-in Registrations allowed for the session " + sessionName + " Only Pre-Registered Attendees", true);
                Assert.assertTrue(true);
            }
        } else Assert.assertTrue(false, "Registration Lookup Failed...");

        //GET SESSION COUNTS
        int[] counts = ucapiCommon.getSessionAvailableCount(eventCode, sessionInstanceId, accountCode);
        if (counts != null) {
            Reporter.log("Got session counts \nCapacity  : " + counts[0] +
                    "\nRegistered: " + counts[1] +
                    "\nRemaining : " + counts[2], true);

            if (counts[2] < 0)
                Assert.assertTrue(false, "the count went into negative");
        }
    }

    @Test(groups = {"Step08", "UCCustomerUseCase1"}, dependsOnGroups = "Step07", priority = 5)
    public void Step08_PrintSummary() throws Exception {
        Reporter.log("========FINAL SUMMARY======================================");
        Reporter.log("Total Capacity/Available Count of the Session    : " + availableCount, true);
        Reporter.log("Total Registrants                                : " + noOfConcurrentRegistrations, true);
        Reporter.log("Total Speakers Added to the session              : " + noOfSpeakersToTheSession, true);
        Reporter.log("Total Registrants Pre-Registered to the session  : " + preAssigned, true);
        Reporter.log("Total Registrants Attended the session           : " + attended, true);
        Reporter.log("Total Walk-in Registered and attended the session: " + (noOfConcurrentRegistrations - preAssigned - failedToAssignSession), true);
        Reporter.log("Total Registrants Cancelled the session          : " + cancelled, true);
        Reporter.log("Total Wait listed due to unavailability          : " + failedToAssignSession);
    }

}