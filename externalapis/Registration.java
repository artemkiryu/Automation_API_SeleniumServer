package com.certain.External.service.v1;

import com.certain.external.dto.agendaItem.AgendaItemObj;
import com.certain.external.dto.profile.ProfileObj;
import com.certain.external.dto.registration.RegistrationObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.dataprovider.XLSTestCaseData;
import internal.qaauto.certain.platform.pojo.*;
import internal.qaauto.certain.platform.services.*;
import io.restassured.response.Response;
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
public class Registration extends CertainAPIBase {

    private final ArrayList<String> pkProfilePinList = new ArrayList<>();
    private final ArrayList<String> pkRegId = new ArrayList<>();
    String AttendeeTypeCode, RegistrationName, RegistrationPin;
    String accountCode;
    String ATTENDEE_TYPE_CODE_REQ;
    String eventCode;
    String eventCodeDefault;
    String profilePin2;
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
    private AgendaItemObjSvc agendaItemObjSvc = new AgendaItemObjSvc();
    private AttendeeTypeObjSvc attendeeTypeObjSvc = new AttendeeTypeObjSvc();
    private List<AgendaItemObj> agendaTypeTestData = new ArrayList<>();
    private List<com.certain.external.dto.attendeeType.AttendeeType> attendeeTypeData = new ArrayList<>();
    private List<com.certain.external.dto.event.EventObj> eventObjList = new ArrayList<>();
    private List<com.certain.external.dto.profile.ProfileObj> profileObjs = new ArrayList<>();
    private ProfileObjSvc profileObjSvc = new ProfileObjSvc();
    private AuthenticationScheme authenticationScheme = new AuthenticationScheme();
    private List<RegistrationObj> registrationList = new ArrayList<>();
    private RegistrationObj registrationObj = new RegistrationObj();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private String REG_CODE_REQ;
    private String[] profileIdentifiers = {
            "SYNC_EMAIL", "SYNC_PIN", "SYNC_EXTERNAL", "EMAIL", "PIN", "EXTERNAL"
    };
    private String[] includeList = {
            "registration_questions", "travel_questions", "reg_properties", "groups", "financial", "order_code"
    };
    private String[] orderBy = {
            "dateModified_asc", "dateModified_desc", "statusModified_asc", "statusModified_desc", "dateCreated_asc", "dateCreated_desc",
            "lastName_asc", "lastName_desc", "firstName_asc", "firstName_desc", "eventName_asc", "eventName_desc", "registrationCode_asc", "registrationCode_desc",
            "registrationStatusLabel_asc", "registrationStatusLabel_desc", "pkProfileId_asc", "pkProfileId_desc"
    };
    private String[] questionsList = {
            "registration_questions", "travel_questions"
    };

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        eventCodeDefault = USER_EVENT_CODE;
        authenticationScheme.setUsername(USERNAME);
        authenticationScheme.setPassword(PASSWORD);
        try {
            registrationList = testDataLoad.getRegistrationObjData(accountCode);
            agendaTypeTestData = testDataLoad.getAgendaItemObjData();
            eventObjList = testDataLoad.getEventObjData("location", accountCode);
            attendeeTypeData = testDataLoad.getAttendeeTypeObjData(accountCode);
            profileObjs = testDataLoad.getProfileObjData(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"post-event-for-reg", "RegistrationObj"})
    public void testAddNewEvent() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        TestCase testCase = eventObjSvc.postEvent(accountCode, eventObj, expectedCondition, true);
        REG_EVENT_CODE = eventObj.getEventCode();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-agenda-for-reg", "RegistrationObj"}, dependsOnGroups = "post-event-for-reg")
    public void testPOSTAgendaItemMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        AgendaItemObj agendaItemObj = agendaTypeTestData.get(0);
        String uniqueCode = randomString(15);
        agendaItemObj.setActivityCode(uniqueCode);
        agendaItemObj.setName("Agenda " + uniqueCode);
        ACTIVITY_CODE = uniqueCode;
        TestCase testCase = agendaItemObjSvc.createAgendaItem(accountCode, REG_EVENT_CODE, agendaItemObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-attendee-for-reg", "RegistrationObj"},
            dependsOnGroups = "post-event-for-reg")
    public void testPOSTAttendeeTypeMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(0);
        String uniqueCode = randomString(15);
        attendeeType.setAttendeeTypeCode(uniqueCode);
        attendeeType.setInventory(10);
        attendeeType.setName(uniqueCode);
        AttendeeTypeCode = uniqueCode;
        TestCase testCase = attendeeTypeObjSvc.createAttendeeType(accountCode, REG_EVENT_CODE, attendeeType, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-profile-for-reg1", "RegistrationObj"})
    public void testAddProfile() throws Exception {
        com.certain.external.dto.profile.ProfileObj profileObj = profileObjs.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        profileObj.setProfilePin("PIN" + uniqueCode);
        profileObj.setFirstName("Reg " + uniqueCode);
        profileObj.setLastName("LN" + uniqueCode);
        profilePin2 = profileObj.getProfilePin();
        TestCase testCase = profileObjSvc.postProfile(accountCode, profileObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-req1", "RegistrationObj"}, dependsOnGroups = {"post-event-for-reg", "post-attendee-for-reg"})
    public void testPOSTRegistrationRequired() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("Reg4 " + randomNumber(2));
        registrationObj.getProfile().setLastName("MandatoryFieldsOnly " + randomNumber(2));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, REG_EVENT_CODE, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            REG_CODE_REQ = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + REG_CODE_REQ, true);

            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-obj", "RegistrationObj"}, dependsOnGroups = {"post-event-for-reg", "post-attendee-for-reg"})
    public void testPOSTRegistration() throws Exception {
        String uniqueCode = randomString(12);
        registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin("PIN" + uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("User " + uniqueCode);
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);
        registrationObj.getProfile().setProfileQuestions(null);
        registrationObj.setRegistrationQuestions(null);
        registrationObj.setTravelQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        RegistrationName = registrationObj.getProfile().getFirstName();
        RegistrationPin = registrationObj.getProfile().getPin();
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, REG_EVENT_CODE, registrationObj, expectedCondition, false);
        if (testCase.isPassed()) {
            REGISTRATION_CODE = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + REGISTRATION_CODE, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-regQtn", "RegistrationObj"}, priority = 2)
    public void testPOSTRegistrationWithProfileQuestions() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("User " + uniqueCode);
        registrationObj.getProfile().setProfileQuestions(testDataLoad.getRegProfileQuestionsObj());
        registrationObj.setRegistrationQuestions(null);
        registrationObj.setTravelQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, eventCodeDefault, registrationObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + testCase.getRegistrationCode(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-regQtn", "RegistrationObj"}, priority = 2)
    public void testPOSTRegistrationWithRegistrationQuestions() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("User " + uniqueCode);
        registrationObj.setRegistrationQuestions(testDataLoad.getRegistrationQuestionsObj());
        registrationObj.getProfile().setProfileQuestions(null);
        registrationObj.setTravelQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, eventCodeDefault, registrationObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + testCase.getRegistrationCode(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-travelQtn", "RegistrationObj"}, priority = 2)
    public void testPOSTRegistrationWithTravelQuestions() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("User " + uniqueCode);
        registrationObj.getProfile().setProfileQuestions(null);
        registrationObj.setRegistrationQuestions(null);
        registrationObj.setTravelQuestions(testDataLoad.getRegTravelQuestionsObj());
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, eventCodeDefault, registrationObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + testCase.getRegistrationCode(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = {"post-registration-obj"})
    public void testGETRegistrationDetailsByCode() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, null);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = "post-registration-req1")
    public void testGETRegistrationForEvent() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, REG_EVENT_CODE, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationForEventUsingMaxResults() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, null, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationForAccountUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, null, null, null, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-reg-includeList", parallel = false)
    public Object[][] getRegIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @Test(dataProvider = "get-reg-includeList", enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationByAccountCodeWithIncludeListUsingMaxResults(String includeList) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, null, new String[]{includeList}, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-reg-includeList", enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationByEventCodeWithIncludeListUsingMaxResults(String includeList) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, new String[]{includeList}, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-regOrderBy-dp", parallel = false)
    public Object[][] getRegOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "get-regOrderBy-dp", enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationForAccountOrderByUsingMaxResultsWithStartIndex(String orderBy) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, null, null, null, orderBy, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-regOrderBy-dp", enabled = true, groups = {"get-registration-obj", "RegistrationObj"})
    public void testGETRegistrationForEventOrderByUsingMaxResults(String orderBy) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, null, null, orderBy, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-regFilterBy-dp1", parallel = false)
    public Object[][] getRegFilters() throws Exception {
        Object obj = testDataLoad.getRegistrationObjFilters(registrationObj);
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-regFilterBy-dp1", enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = "post-registration-obj")
    public void testGETRegistrationByAccountCodeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, null, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria [" + filter + "=" + value + "]");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-regFilterBy-dp1", enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = "post-registration-obj")
    public void testGETRegistrationByEventCodeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria [" + filter + "=" + value + "]");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-regFilterBy-dp2")
    public Object[][] getRegFiltersMultiple() throws Exception {
        Object obj = testDataLoad.getRegistrationObjFilters(registrationObj);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 5);
    }

    @Test(dataProvider = "get-regFilterBy-dp2", enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = "post-registration-obj")
    public void testGETRegistrationForAccountWithMultipleSearchFilters(HashMap<String, Object> hashMap) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, null, hashMap, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-regFilterBy-dp2", enabled = true, groups = {"get-registration-obj", "RegistrationObj"}, dependsOnGroups = "post-registration-obj")
    public void testGETRegistrationWithMultipleSearchFilters(HashMap<String, Object> hashMap) throws Exception {
        TestCase testCase = registrationObjSvc.getRegistrations(accountCode, USER_EVENT_CODE, hashMap, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No registrations found matching criteria");
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-registration-obj-update", "RegistrationObj"}, priority = 4, dependsOnGroups = {"post-registration-obj"})
    public void testPOSTUpdateRegistration() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(1);
        registrationObj.setRegistrationCode(REGISTRATION_CODE);
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);
        registrationObj.setProfile(null);
        registrationObj.setRegistrationQuestions(null);
        registrationObj.setTravelQuestions(null);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.updateRegistration(accountCode, REG_EVENT_CODE, REGISTRATION_CODE, registrationObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"delete-registration-req", "RegistrationObj"}, priority = 6, dependsOnGroups = {"post-registration-req1"})
    public void testDELETERegistrationRequired() throws Exception {
        TestCase testCase = registrationObjSvc.deleteRegistration(accountCode, REG_EVENT_CODE, REG_CODE_REQ);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"delete-registration-obj", "RegistrationObj"}, priority = 7, dependsOnGroups = {"post-registration-obj"})
    public void testDELETERegistration() throws Exception {
        TestCase testCase = registrationObjSvc.deleteRegistration(accountCode, REG_EVENT_CODE, REGISTRATION_CODE);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //Profile - Identifiers
    @DataProvider(name = "post-reg-identifiers", parallel = false)
    public Object[][] postProfileIdentifiers() throws Exception {
        Object[][] identifiers = new Object[profileIdentifiers.length][2];
        int i = 0;
        for (String item : profileIdentifiers) {
            identifiers[i][0] = item;
            identifiers[i][1] = registrationList.get(i + 2);
            i++;
        }
        return identifiers;
//        identifiers[0][0] = "SYNC_EMAIL";
//        identifiers[0][1] = registrationList.get(2);
//        identifiers[1][0] = "SYNC_PIN";
//        identifiers[1][1] = registrationList.get(3);
//        identifiers[2][0] = "SYNC_EXTERNAL";
//        identifiers[2][1] = registrationList.get(4);
//        identifiers[3][0] = "EMAIL";
//        identifiers[3][1] = registrationList.get(5);
//        identifiers[4][0] = "PIN";
//        identifiers[4][1] = registrationList.get(6);
//        identifiers[5][0] = "EXTERNAL";
//        identifiers[5][1] = registrationList.get(7);
//        return identifiers;
    }

    @Test(dataProvider = "post-reg-identifiers", enabled = true, description = "post new registration with profile identifiers", groups = {"post-registration-obj-identifier", "RegistrationObj"},
            dependsOnGroups = {"post-event-for-reg", "post-attendee-for-reg"})
    public void testPOST_RegistrationWithURLIdentifiersPositive(String identifier, RegistrationObj registrationObj) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + REG_EVENT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        registrationObj.setAttendeeType(AttendeeTypeCode);
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);

        switch (identifier) {
            case "SYNC_EMAIL":
            case "EMAIL":
                Reporter.log("POSTING REGISTRATION HAVING USING " + identifier + " AND THE 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setPin(null);
                registrationObj.getProfile().setExternalKey(null);

                break;
            case "SYNC_PIN":
            case "PIN":
                Reporter.log("POSTING REGISTRATION HAVING USING " + identifier + " AND THE 'pin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setEmail(null);
                registrationObj.getProfile().setExternalKey(null);

                break;
            case "SYNC_EXTERNAL":
            case "EXTERNAL":
                Reporter.log("POSTING REGISTRATION HAVING USING " + identifier + " AND THE 'externalKey' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setPin(null);
                registrationObj.getProfile().setEmail(null);
                break;
        }

        String __jsonRequestMessage = super.gson.toJson(registrationObj);
        Response response = super.restAssuredClient.POST(__jsonRequestMessage, urlPath, authenticationScheme);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __pkRegId = jsonHelper.getValueByXpath(__jsonResponseMessage, "pkRegId");
            pkRegId.add(__pkRegId);
            HashMap<String, String> profileMap = (HashMap) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "profile");

            pkProfilePinList.add(profileMap.get("pkProfileId"));
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage)) {
                Reporter.log("Registration Created successfully " + __pkRegId + " Profile Pin " + profileMap.get("pkProfileId"), true);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Post was Successful but Response Assertion Failed ");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post New Registration with URL identifier " + identifier);
        }
    }

    @Test(dataProvider = "post-reg-identifiers", enabled = true, description = "post new registration with profile identifiers", groups = {"post-registration-obj-identifier-upd", "RegistrationObj"},
            priority = 2, dependsOnGroups = {"post-registration-obj-identifier"})
    public void testPOST_RegistrationWithURLIdentifiersFoundAndUpdate(String identifier, RegistrationObj registrationObj) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + REG_EVENT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        String uniqueNum = randomNumber(3);
        registrationObj.setDoFollowUp(true);
        String pin = registrationObj.getProfile().getPin();
        String emailId = registrationObj.getProfile().getEmail();
        String exKey = registrationObj.getProfile().getExternalKey();
        registrationObj.getProfile().setPin(null);
        registrationObj.getProfile().setEmail(null);
        registrationObj.getProfile().setExternalKey(null);

        switch (identifier) {
            case "SYNC_EMAIL":
            case "EMAIL":
                Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setFirstName("FNRUpdateForEmail" + uniqueNum);
                registrationObj.getProfile().setLastName("LNRUpdateForEmail" + uniqueNum);
                registrationObj.getProfile().setEmail(emailId);

                break;
            case "SYNC_PIN":
            case "PIN":
                Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'profilePin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setFirstName("FNRUpdateForPin" + uniqueNum);
                registrationObj.getProfile().setLastName("LNRUpdateForPin" + uniqueNum);
                registrationObj.getProfile().setPin(pin);

                break;
            case "SYNC_EXTERNAL":
            case "EXTERNAL":
                Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'externalKey' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setFirstName("FNRUpdateForExternal" + uniqueNum);
                registrationObj.getProfile().setLastName("LNRUpdateForExternal" + uniqueNum);
                registrationObj.getProfile().setExternalKey(exKey);
                break;
        }
        String fname = registrationObj.getProfile().getFirstName();
        String lname = registrationObj.getProfile().getLastName();

        String __jsonRequestMessage = super.gson.toJson(registrationObj);
        Response response = super.restAssuredClient.POST(__jsonRequestMessage, urlPath, authenticationScheme);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __pkRegId = jsonHelper.getValueByXpath(__jsonResponseMessage, "pkRegId");
            boolean doFollowUp = (Boolean) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "doFollowUp");
            Map<String, String> profileMap = (Map) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "profile");

            boolean pinFound = false;
            for (String aPkProfilePinList : pkProfilePinList) {
                if (aPkProfilePinList.equalsIgnoreCase(profileMap.get("pkProfileId"))) {
                    pinFound = true;
                }
            }

            if (pinFound) {
                if (fname.equals(profileMap.get("firstName")) && lname.equals(profileMap.get("lastName")) && doFollowUp) {
                    Reporter.log("Profile already exist in the database with id " + profileMap.get("pin") + " it is retrieved and was updated successfully and created new registration  " + __pkRegId, true);
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(true, "Profile already exist in the database with id Profile Pin " + profileMap.get("pin") + " but was not updated or reg created");
            } else
                Assert.assertTrue(false, "Could not retrieve the existing Profile Pin " + profileMap.get("pin"));

        } else if (response.getStatusCode() == 400) {
            String patternString = "HTTP Status 400 - A profile already exists for given profile pin";
            if (__jsonResponseMessage.contains(patternString)) {
                Assert.assertTrue(true, "Not able to post profile using same pin wiht identifier " + identifier);
            } else {
                Assert.assertTrue(false, response.getStatusLine() + " Failed to Post New Registration with URL identifier " + identifier);
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post New Registration with URL identifier " + identifier);
        }
    }

    @Test(dataProvider = "post-reg-identifiers", enabled = true, description = "post new registration with profile identifiers - nagative", groups = {"post-registration-obj-identifier-neg", "RegistrationObj"},
            priority = 3, dependsOnGroups = {"post-registration-obj-identifier-upd"})
    public void testPOST_RegistrationWithURLIdentifiersNegative(String identifier, RegistrationObj registrationObj1) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + REG_EVENT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        RegistrationObj registrationObj;
        String uniqueKey = randomNumber(3);
        registrationObj = registrationList.get(2);
        registrationObj.setAttendeeType(AttendeeTypeCode);
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);
        registrationObj.getProfile().setPin(null);
        registrationObj.getProfile().setEmail(null);
        registrationObj.getProfile().setExternalKey(null);

        switch (identifier) {
            case "SYNC_EMAIL":
            case "EMAIL":
                Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setPin("REG_PRO_PIN" + uniqueKey);
                registrationObj.getProfile().setExternalKey("REG_EKEY" + uniqueKey);

                break;
            case "SYNC_PIN":
            case "PIN":
                Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'pin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
                registrationObj.getProfile().setEmail(registrationObj.getProfile().getFirstName() + "@gmail.com");
                registrationObj.getProfile().setExternalKey("REG_EKEY" + uniqueKey);

                break;
            case "SYNC_EXTERNAL":
            case "EXTERNAL":
                Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'externalKey' FIELD IN THE REQUEST NO DUPLICATES", true);
                registrationObj.getProfile().setEmail(registrationObj.getProfile().getFirstName() + "@gmail.com");
                registrationObj.getProfile().setPin("REG_PRO_PIN" + uniqueKey);
                break;
        }
        String __jsonRequestMessage = super.gson.toJson(registrationObj);
        Response response = super.restAssuredClient.POST(__jsonRequestMessage, urlPath, authenticationScheme);
        if (response.getStatusCode() == 400) {
            Reporter.log("Post was Unsuccessful and proper error message displayed ", true);
            Assert.assertTrue(true);
        } else if (response.getStatusCode() == 200) {
            Assert.assertTrue(false, "Post was successful and registration created successfully");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Post New Registration with URL identifier " + identifier);
        }
    }

    //Get Registration where profileQuestion Answer is NULL  - UNDER DEVELOPMENT
    @DataProvider(name = "get-profileQ", parallel = false)
    public Object[][] getProfileQue() throws Exception {
        List<HashMap<String, String>> profileQueMap = new XLSTestCaseData().getTestCaseFromXLSheet("RegQuestionFilters");
        Object[][] proFileQObj = new Object[profileQueMap.size()][4];
        for (int i = 0; i < profileQueMap.size(); i++) {
            proFileQObj[i][0] = profileQueMap.get(i).get("includeList");
            proFileQObj[i][1] = profileQueMap.get(i).get("questionObject");
            proFileQObj[i][2] = profileQueMap.get(i).get("questionName");
            proFileQObj[i][3] = profileQueMap.get(i).get("value");
        }
        return proFileQObj;
    }

    @Test(dataProvider = "get-profileQ", enabled = false, description = "get registration questions with search filters",
            groups = {"get-registrations-questions-filters", "Registration"}, priority = 2)
    public void testGET_RegistrationsWithQuestionFilters(String includeList, String QuestionsType, String questionName, String value) throws Exception {
        Reporter.log("[SCM-25002] Retrieving Registration  '" + QuestionsType + "' for Event " + USER_EVENT_CODE + " Where registration question " + questionName + "=" + value);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(authenticationScheme);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource("Registration");
        request.setAccountCode(accountCode);
        request.setEventCode(USER_EVENT_CODE);
        request.addQueryParameters("includeList", includeList);
        request.addQueryParameters(QuestionsType + "=" + questionName, value);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            List<HashMap<String, Object>> registrationCollection = response.getBody().jsonPath().getList("registrations");
            int passCount = 0;
            for (HashMap<String, Object> aRegistrationCollection : registrationCollection) {
                boolean flag;
                if (QuestionsType.equals("profileQuestions")) {
                    flag = jsonHelper.validateQuestionAnswer((HashMap) aRegistrationCollection.get("profile"), QuestionsType, questionName, value);
                } else {
                    flag = jsonHelper.validateQuestionAnswer(aRegistrationCollection, QuestionsType, questionName, value);
                }
                if (flag) passCount++;
            }
            if (passCount == registrationCollection.size()) {
                Reporter.log("The payload results did match the question value filter", true);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, " the results does not match the filter criteria");

            Assert.assertTrue(true);
        } else if (response.getStatusCode() == 404) {
            Reporter.log("No Questions found for the event : " + USER_EVENT_CODE);
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to retrieve registrations " + QuestionsType + " where registration question " + questionName + "=" + value);
        }

    }

    @Test(enabled = true, groups = {"post-registration-obj-update", "RegistrationObj"}, priority = 8, dependsOnGroups = {"post-registration-obj"})
    public void testPOST_RegistrationsMoreThanAttendeeInventorySCM31098() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(0);
        String uniqueCode = randomString(15);
        attendeeType.setAttendeeTypeCode(uniqueCode);
        attendeeType.setName("Attendee " + uniqueCode);
        AttendeeTypeCode = uniqueCode;
        attendeeType.setInventory(1);
        TestCase testCase = attendeeTypeObjSvc.createAttendeeType(accountCode, REG_EVENT_CODE, attendeeType, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("Reg4 " + randomNumber(2));
        registrationObj.getProfile().setLastName("MandatoryFieldsOnly " + randomNumber(2));
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);
        expectedCondition = new ExpectedCondition(200, null);
        testCase = registrationObjSvc.postRegistrationAndCheckStatus(accountCode, REG_EVENT_CODE, registrationObj, expectedCondition, true, "New");
        if (testCase.isPassed()) {
            REG_CODE_REQ = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + REG_CODE_REQ, true);

            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        uniqueCode = randomString(12);
        registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("Reg4 " + randomNumber(2));
        registrationObj.getProfile().setLastName("MandatoryFieldsOnly " + randomNumber(2));
        registrationObj.setAttendeeTypeCode(AttendeeTypeCode);
        expectedCondition = new ExpectedCondition(200, null);
        testCase = registrationObjSvc.postRegistrationAndCheckStatus(accountCode, REG_EVENT_CODE, registrationObj, expectedCondition, true, "Waitlist Hold");
        if (testCase.isPassed()) {
            REG_CODE_REQ = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + REG_CODE_REQ, true);

            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-reg-questionsList", parallel = false)
    public Object[][] getRegQuestionsList() throws Exception {
        Object[][] includeLists = new Object[questionsList.length][1];
        int i = 0;
        for (String item : questionsList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }


    @Test(dataProvider = "get-reg-questionsList", enabled = true)
    public void testPOSTRegistrationAndValidateDefaultRegistrationQuestionsSCM31105(String includeList) throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("TestQuesReg" + randomNumber(2));
        registrationObj.getProfile().setLastName("TestQuesReg " + randomNumber(2));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        TestCase testCase = registrationObjSvc.postRegistration(accountCode, eventCode, registrationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            REG_CODE_REQ = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + REG_CODE_REQ, true);

            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
        registrationObj.setRegistrationQuestions(testDataLoad.getRegistrationQuestionsObj());
        if (includeList.contains("registration_questions")) {
            testCase = registrationObjSvc.GETRegistrationsWithDefaultAnswerValue(accountCode, eventCode, REG_CODE_REQ, new String[]{includeList}, registrationObj);
        } else {
            registrationObj.setRegistrationQuestions(null);
            registrationObj.setTravelQuestions(testDataLoad.getRegTravelQuestionsObj());
            testCase = registrationObjSvc.GETRegistrationsWithDefaultAnswerValue(accountCode, eventCode, REG_CODE_REQ, new String[]{includeList}, registrationObj);
        }
        if (testCase.isPassed()) {
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"RegistrationObj"}, dependsOnGroups = {"post-profile-for-reg1"}, description = "Post registration using existing profile pin using same event.")
    public void testPOSTRegistrationUsingExistingProfilePinSCM32643() throws Exception {
        String uniqueCode = randomString(12);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(profilePin2);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("Reg4 " + randomNumber(2));
        registrationObj.getProfile().setLastName("MandatoryFieldsOnly " + randomNumber(2));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        boolean found = registrationObjSvc.postRegistrationWithExistingPin(accountCode, eventCodeDefault, registrationObj);
        Assert.assertTrue(found, "Registration allow to post using existing profile pin");
        Reporter.log("Registration not allow to post using existing profile pin", true);

    }

}




