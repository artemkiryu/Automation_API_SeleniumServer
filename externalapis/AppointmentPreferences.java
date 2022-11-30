package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AppointmentPreferencesObj;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class AppointmentPreferences extends CertainAPIBase {

    private static String targetRegCode, sourceRegCode;
    private AuthenticationScheme authenticationScheme = new AuthenticationScheme(USERNAME, PASSWORD);
    private List<AppointmentPreferencesObj> appointmentPreferencesObjList = new ArrayList<>();
    private AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private String uniqueCode = randomString(10);
    private String name;
    private String email;
    private int rank;
    private String preferenceId;
    private String organization;
    private String accountCode;
    private String eventCode;
    private String urlPath;
    private String urlRotation;
    private String registrationCode;
    private String customRotation = ROTATION_NAME;

    @org.testng.annotations.BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/registration/";
        urlRotation = SERVER_HOST + BASE_PATH + "/RegistrationRotation/" + accountCode + "/" + eventCode;
        String firstName = "REG4PREF_API";
        registrationCode = createRegistration(eventCode, firstName, firstName + "@gmail.com", "Certain", "Attendee");
    }

    //SCM-25520
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference", "AppointmentPreferences"})
    public void testPOSTAppointmentPreference() {
        uniqueCode = randomNumber(10);
        name = "tReg" + uniqueCode;
        email = uniqueCode + "@microsoft.com";
        rank = Integer.valueOf(randomNumber(1));
        organization = "Microsoft";
        rank = 2;
        sourceRegCode = createRegistration(eventCode, "sReg" + randomNumber(5), "sReg" + randomNumber(4) + "@certain.com", "Certain", "Attendee");
        targetRegCode = createRegistration(eventCode, name, email, organization, "Attendee");
        appointmentPreferencesObj.setAttendeeTypeCode("Attendee");
        appointmentPreferencesObj.setRegCode(targetRegCode);
        appointmentPreferencesObj.setRank(rank);
        appointmentPreferencesObjList.add(appointmentPreferencesObj);
        Reporter.log("[SCM-25520] Posting appointment preferences for the registration [" + sourceRegCode + "] set organization");
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjList), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                if (validatePost(sourceRegCode, appointmentPreferencesObjList)) {
                    Reporter.log("[AssertTrue] Successfully created new appointment preferences with target registrationCode [" + targetRegCode + "]");
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(false, "[AssertFailed] ]Expected message [Preference has been added successfully] not exist in payload...");
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create appointment preferences with target registrationCode [" + targetRegCode + "]");
    }

    //SCM-25520
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-multi", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesMultiple() {
        Reporter.log("[SCM-25520] Posting  multiple preferences for the registration [" + registrationCode + "]");
        int noOfPreferences = 2;
        Reporter.log("Posting preferences for the reg [" + registrationCode + "] more than the max preferences");
        ArrayList<String> registrationCodes = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < noOfPreferences; i++) {
            String regCode = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@cisco.com", "CISCO" + index, "Attendee");
            registrationCodes.add(regCode);
        }
        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode("Attendee");
            preference.setRegCode(regCode);
            preference.setRank(index);
            index++;
            appointmentPreferencesObjs.add(preference);
        }
        java.lang.String url = urlPath + registrationCode + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                if (validatePost(registrationCode, appointmentPreferencesObjs)) {
                    Reporter.log("[AssertTrue] Successfully created preferences with 3 registrations", true);
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(false, "[AssertFailed] ]Expected message [Preference has been added successfully] not exist in payload...");
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create appointment preferences for the registrationCode [" + registrationCode + "]");
    }

    //SCM-25973
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-moreThan-allowed", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesMoreThanAllowedPref() {
        int noOfPreferences = 4;
        Reporter.log("[SCM-25973] Posting preferences for the reg [" + registrationCode + "] more than the max preferences");
        ArrayList<String> registrationCodes = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < noOfPreferences; i++) {
            String regCode = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@cisco.com", "CISCO" + index, "Attendee");
            registrationCodes.add(regCode);
        }

        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode("Attendee");
            preference.setRegCode(regCode);
            preference.setRank(index);
            index++;
            appointmentPreferencesObjs.add(preference);
        }

        java.lang.String url = urlPath + registrationCode + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("You have already selected maximum number of preferences")) {
                Reporter.log("[AssertTrue] Cannot post preference more than the max configured in template", true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed post appointment preferences for the registrationCode [" + registrationCode + "]");
    }

    //SCM-25972
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-moreThan-allowed-perAttendee", "AppointmentPreferences"})
    public void testPOSTPreferencesMoreThanAllowedPrefPerAttendeeSameOrg() {
        int noOfPreferences = 3;
        String sourceReg = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@certain.com", "Certain", "Attendee");
        Reporter.log("[SCM-25972] Posting preferences for the reg [" + sourceReg + "] more than the allowed preferences per attendee type same org");
        ArrayList<String> registrationCodes = new ArrayList<>();
        for (int i = 0; i < noOfPreferences; i++) {
            String regCode = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@cisco.com", "CISCO", "Attendee");
            registrationCodes.add(regCode);
        }
        int rank = 1;
        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode("Attendee");
            preference.setRegCode(regCode);
            preference.setRank(rank);
            rank++;
            appointmentPreferencesObjs.add(preference);
        }

        java.lang.String url = urlPath + sourceReg + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("You are trying to select more than the allowed preferences for the same organization")) {
                Reporter.log("[AssertTrue] Cannot select more than the allowed preferences for the same organization", true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed post appointment preferences for the registrationCode [" + sourceReg + "]");
    }

    //SCM-25972
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-moreThan-allowed-perAttendee", "AppointmentPreferences"})
    public void testPOSTPreferencesMoreThanAllowedPrefPerAttendeeDiffOrg() {
        int noOfPreferences = 3;
        String sourceReg = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@certain.com", "Certain", "Attendee");
        Reporter.log("[SCM-25972] Posting preferences for the reg [" + sourceReg + "] more than the allowed preferences per attendee type different org");
        ArrayList<String> registrationCodes = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < noOfPreferences; i++) {
            String regCode = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@cisco.com", "CISCO" + index, "Attendee");
            registrationCodes.add(regCode);
            index++;
        }

        int rank = 1;
        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode("Attendee");
            preference.setRegCode(regCode);
            preference.setRank(rank);
            rank++;
            appointmentPreferencesObjs.add(preference);
        }

        java.lang.String url = urlPath + sourceReg + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                if (validatePost(sourceReg, appointmentPreferencesObjs)) {
                    Reporter.log("[AssertTrue] post preference was successful", true);
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(false, "[AssertFailed] Expected message 'Preference has been added successfully' not exist in payload...");
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed post appointment preferences for the registrationCode [" + sourceReg + "]");
    }

    //SCM-26089
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-noOrg", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesOrgNull() {
        Reporter.log("[SCM-26089] Creating new appointment preferences for the registration [" + sourceRegCode + "] set organization");
        String uniqueString = randomString(15);
        String sourceReg = createRegistration(eventCode, uniqueString, uniqueString + "@qatest.com", null, "Attendee");
        String targetReg = createRegistration(eventCode, uniqueString, uniqueString + "@qatest.com", null, "Attendee");
        java.lang.String url = urlPath + sourceReg + "/preferences";
        List<AppointmentPreferencesObj> appointmentPreferencesObjListOrgNull = new ArrayList<>();
        AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
        appointmentPreferencesObj.setAttendeeTypeCode("Attendee");
        appointmentPreferencesObj.setRegCode(targetReg);
        appointmentPreferencesObj.setRank(Integer.valueOf(randomNumber(1)));
        appointmentPreferencesObjListOrgNull.add(appointmentPreferencesObj);
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjListOrgNull), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                Reporter.log("[AssertTrue] Successfully created new appointment preferences for the registrationCode [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "[AssertFailed] ]Expected message [Preference has been added successfully] not exist in payload...");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-self-preference", "AppointmentPreferences"})
    public void testPOSTSelfAppointmentPreferences() {
        Reporter.log("Creating new appointment preferences for the registration [" + sourceRegCode + "] set organization");
        java.lang.String url = urlPath + registrationCode + "/preferences";
        List<AppointmentPreferencesObj> appointmentPreferencesObjListOrgNull = new ArrayList<>();
        AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
        appointmentPreferencesObj.setAttendeeTypeCode("Attendee");
        appointmentPreferencesObj.setRegCode(registrationCode);
        appointmentPreferencesObj.setRank(Integer.valueOf(randomNumber(1)));
        appointmentPreferencesObjListOrgNull.add(appointmentPreferencesObj);
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjListOrgNull), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("You are not allowed to select yourself as a preference")) {
                Reporter.log("[AssertTrue] got proper error message  [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create self appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-invalid-attendee", "AppointmentPreferencesObj"}, dependsOnGroups = {"post-preference"})
    public void testPOSTAppointmentPreferencesInvalidAttendee() {
        Reporter.log("[SCM-25969] Creating new appointment preferences for the registration [" + sourceRegCode + "] set organization");
        java.lang.String url = urlPath + registrationCode + "/preferences";
        List<AppointmentPreferencesObj> appointmentPreferencesObjListOrgNull = new ArrayList<>();
        AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
        appointmentPreferencesObj.setAttendeeTypeCode("InvalidAttendee");
        appointmentPreferencesObj.setRegCode(targetRegCode);
        appointmentPreferencesObj.setRank(Integer.valueOf(randomNumber(1)));
        appointmentPreferencesObjListOrgNull.add(appointmentPreferencesObj);
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjListOrgNull), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("Attendee Type code that you've mentioned does not exist")) {
                Reporter.log("[AssertTrue] got proper error message  [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create self appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-invalid-regCode", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesInvalidRegCode() {
        Reporter.log("[SCM-25969] Creating new appointment preferences for the registration [" + registrationCode + "] set organization");
        java.lang.String url = urlPath + registrationCode + "/preferences";
        List<AppointmentPreferencesObj> appointmentPreferencesObjListOrgNull = new ArrayList<>();
        AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
        appointmentPreferencesObj.setAttendeeTypeCode("Attendee");
        appointmentPreferencesObj.setRegCode("123-0910-2190");
        appointmentPreferencesObj.setRank(Integer.valueOf(randomNumber(1)));
        appointmentPreferencesObjListOrgNull.add(appointmentPreferencesObj);
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjListOrgNull), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("Registration with registration code 123-0910-2190 does not exist")) {
                Reporter.log("[AssertTrue] got proper error message  [" + registrationCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create self appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    //SCM-25969
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-regCode-noAttendeeType", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesRegCodeAttendeeNotAssigned() {
        Reporter.log("Creating new appointment preferences for the registration [" + registrationCode + "] set organization");
        java.lang.String url = urlPath + registrationCode + "/preferences";
        String uniqueString = randomString(10);
        String targetReg = createRegistration(eventCode, uniqueString, uniqueString + "@qatest.com", null, null);
        List<AppointmentPreferencesObj> appointmentPreferencesObjListOrgNull = new ArrayList<>();
        AppointmentPreferencesObj appointmentPreferencesObj = new AppointmentPreferencesObj();
        appointmentPreferencesObj.setAttendeeTypeCode("Attendee");
        appointmentPreferencesObj.setRegCode(targetReg);
        appointmentPreferencesObj.setRank(Integer.valueOf(randomNumber(1)));
        appointmentPreferencesObjListOrgNull.add(appointmentPreferencesObj);
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjListOrgNull), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("Registration code " + targetReg + " does not belong to any attendee type")) {
                Reporter.log("[AssertTrue] got proper error message  [" + registrationCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create self appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-preferences", "AppointmentPreferences"}, dependsOnGroups = {"post-preference"})
    public void testGETAppointmentPreferencesByRegCode() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        Reporter.log("[SCM-25519] Getting appointment preferences for the registration [" + sourceRegCode + "]");
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        Reporter.log("Response   :" + response.asString(), true);
        if (response.getStatusCode() == HTTP200) {
            preferenceId = jsonHelper.getJsonValueByKey(response.asString(), "$", "id");
            ArrayList<HashMap<String, Object>> preferencesList = jsonHelper.getJsonArray(response.asString(), "$");
            HashMap<String, Object> registrationMap = (HashMap<String, Object>) preferencesList.get(0).get("registration");
            HashMap<String, Object> targetRegMap = (HashMap<String, Object>) preferencesList.get(0).get("targetRegistration");
            if (registrationMap.get("code").equals(sourceRegCode) && targetRegMap.get("code").equals(targetRegCode) && (Integer) preferencesList.get(0).get("rank") == rank) {
                Reporter.log("[AssertTrue] Retrieved appointment preferences for the registrationCode [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] get was successful but validation failed...");

        } else if (response.getStatusCode() == HTTP404) {
            Reporter.log("[AssertTrue] No appointment preferences found for the registrationCode [" + sourceRegCode + "]");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to get appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    public boolean validatePost(String sourceReg, List<AppointmentPreferencesObj> appointmentPreferences) {
        boolean passed = false;
        java.lang.String url = urlPath + sourceReg + "/preferences";
        Reporter.log("Getting appointment preferences for the registration [" + sourceReg + "]", true);
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        Reporter.log("Response   :" + response.asString(), true);
        int targetMatch = 0;
        if (response.getStatusCode() == HTTP200) {
            ArrayList<HashMap<String, Object>> preferencesList = jsonHelper.getJsonArray(response.asString(), "$");
            HashMap<String, Object> registrationMap = (HashMap<String, Object>) preferencesList.get(0).get("registration");
            Reporter.log("Source Registration [code] Expected=" + sourceReg + " Actual=" + registrationMap.get("code"), true);
            for (HashMap<String, Object> eachPreference : preferencesList) {
                for (AppointmentPreferencesObj appointmentPreferencesObj : appointmentPreferences) {
                    HashMap<String, Object> targetRegMap = (HashMap<String, Object>) eachPreference.get("targetRegistration");
                    if (targetRegMap.get("code").equals(appointmentPreferencesObj.getRegCode())) {
                        Reporter.log("Target Registration [code] Expected=" + appointmentPreferencesObj.getRegCode() + " Actual=" + targetRegMap.get("code"), true);
                        targetMatch++;
                    }
                }
            }

            if (registrationMap.get("code").equals(sourceReg) && appointmentPreferences.size() == targetMatch) {
                passed = true;
            }
        }
        return passed;
    }

    @DataProvider(name = "get-preferences-filters", parallel = false)
    private Object[][] getPreferenceSearchFilters() throws Exception {
        Object object = testDataLoad.getPreferenceSearchFilters(name, email, organization, rank);
        return testDataLoad.getKeyValuePairFromObject(object);
    }

    //SCM-25518
    @org.testng.annotations.Test(dataProvider = "get-preferences-filters", enabled = true, groups = {"get-preferences", "AppointmentPreferences"},
            dependsOnGroups = {"post-preference"})
    public void testGETAppointmentPreferencesByRegCodeWithSearchFilter(String filter, String value) throws Exception {
        java.lang.String url = urlPath + sourceRegCode + "/preferences?" + filter + "=" + value;
        Reporter.log("[SCM-25518] Getting appointment preferences for the registration [" + sourceRegCode + "]");
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            Reporter.log("[AssertTrue] Retrieved appointment preferences for the registrationCode [" + sourceRegCode + "]");
            ArrayList<HashMap<String, Object>> preferencesList = jsonHelper.getJsonArray(response.asString(), "$");
            int instanceCount = 0;
            if (filter.equals("name") || filter.equals("email") || filter.equals("organization")) {
                if (filter.equals("name"))
                    filter = "firstName";

                ArrayList<HashMap<String, Object>> preferences = jsonHelper.getJsonArray(response.asString(), "$");
                for (HashMap<String, Object> eachPreference : preferences) {
                    HashMap<String, Object> eachRegObj = (HashMap) eachPreference.get("targetRegistration");
                    HashMap<String, Object> eachProfile = (HashMap) eachRegObj.get("profile");
                    if (eachProfile.get(filter).toString().equals(value)) {
                        instanceCount++;
                    }
                }
            } else {
                instanceCount = jsonHelper.getInstanceCount(response.asString(), "$", filter, value);
            }
            if (preferencesList.size() == instanceCount) {
                Reporter.log("FIELD [" + filter + "] matching records = " + instanceCount);
                Assert.assertTrue(true);
            } else {
                Reporter.log("FIELD [" + filter + "] did not match in payload expected value [" + value + "]");
                Assert.assertTrue(false);
            }
        } else if (response.getStatusCode() == HTTP404) {
            Reporter.log("[AssertTrue] No appointment preferences found for the matching criteria");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to get appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @DataProvider(name = "get-preferences-multi-filters")
    private Object[][] preferencesMultiSearchFilters() throws Exception {
        Object obj = testDataLoad.getPreferenceSearchFilters(name, email, organization, rank);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-preferences-multi-filters", enabled = true, groups = {"get-preferences", "AppointmentPreferences"},
            dependsOnGroups = {"post-preference"})
    public void testGETAppointmentPreferencesByRegCodeWithMultipleSearchFilter(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("[SCM-25518] Getting appointment preferences for the registration [" + sourceRegCode + "] with multiple search filters");
        List<ArrayList> mapToArray = testDataLoad.mapToArray(multipleFilters);
        ArrayList<String> keys = mapToArray.get(0);
        ArrayList values = mapToArray.get(1);
        java.lang.String url = urlPath + sourceRegCode + "/preferences?" + keys.get(0) + "=" + values.get(0);
        String queryParams = "";
        for (int i = 1; i < keys.size(); i++) {
            queryParams += "&" + keys.get(i) + "=" + values.get(i);
        }
        url = url + queryParams;
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            Reporter.log("[AssertTrue] Retrieved appointment preferences for the registrationCode [" + sourceRegCode + "]");
            ArrayList<HashMap<String, Object>> preferencesList = jsonHelper.getJsonArray(response.asString(), "$");
            int conditionsMatch = 0;
            int instanceCount = 0;
            for (int i = 0; i < keys.size(); i++) {
                String revisedKey = keys.get(i);
                instanceCount = 0;
                if (keys.get(i).equals("name") || keys.get(i).equals("email") || keys.get(i).equals("organization")) {
                    if (keys.get(i).equals("name"))
                        revisedKey = "firstName";
                    ArrayList<HashMap<String, Object>> preferences = jsonHelper.getJsonArray(response.asString(), "$");
                    for (HashMap<String, Object> eachPreference : preferences) {
                        HashMap<String, Object> eachRegObj = (HashMap) eachPreference.get("targetRegistration");
                        HashMap<String, Object> eachProfile = (HashMap) eachRegObj.get("profile");
                        if (eachProfile.get(revisedKey).toString().equals(values.get(i)))
                            instanceCount++;
                    }
                } else {
                    instanceCount = jsonHelper.getInstanceCount(response.asString(), "$", keys.get(i), values.get(i));
                }

                if (preferencesList.size() == instanceCount) {
                    Reporter.log("Key [" + revisedKey + "=" + values.get(i) + "] total matching records  " + instanceCount, true);
                    conditionsMatch++;
                } else {
                    Reporter.log("FIELD [" + revisedKey + "] did not match in payload expected value [" + values.get(i) + "]");
                    Assert.assertTrue(false);
                }
            }
            if (conditionsMatch == keys.size())
                Assert.assertTrue(true);
            else
                Assert.assertTrue(false, " One/more filter value did not match with payload field ");

        } else if (response.getStatusCode() == HTTP404) {
            Reporter.log("[AssertTrue] No appointment preferences found for the matching criteria");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to get appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    //SCM-25519
    @org.testng.annotations.Test(enabled = true, groups = {"get-preferences-id", "AppointmentPreferences"}, dependsOnGroups = {"get-preferences"})
    public void testGETAppointmentPreferencesByPreferenceId() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences/" + preferenceId;
        Reporter.log("[SCM-25519] Getting appointment preferences for the registration [" + sourceRegCode + "]");
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        Reporter.log("Response   :" + response.asString(), true);
        if (response.getStatusCode() == HTTP200) {
            Reporter.log("[AssertTrue] Retrieved appointment preferences for the registrationCode [" + sourceRegCode + "] by preference id [" + preferenceId + "]");
            Assert.assertTrue(true);
        } else if (response.getStatusCode() == HTTP404) {
            Reporter.log("[AssertTrue] No appointment preferences found for the registrationCode [" + sourceRegCode + "] by preference id [" + preferenceId + "]");
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to get appointment preferences for the registrationCode [" + sourceRegCode + "] by preference id [" + preferenceId + "]");
    }

    //SCM-25975
    @org.testng.annotations.Test(enabled = true, groups = {"put-preferences-self", "AppointmentPreferences"}, dependsOnGroups = "get-preferences")
    public void testPUTUpdateAppointmentPreferencesInvalidPreferenceId() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        Reporter.log("[SCM-25975] Updating appointment preferences for the registration [" + sourceRegCode + "]");
        List<HashMap<String, Object>> preferences = new ArrayList<>();
        HashMap<String, Object> preference = new HashMap<>();
        preference.put("id", "1239018");
        preference.put("attendeeTypeCode", appointmentPreferencesObj.getAttendeeTypeCode());
        preference.put("regCode", targetRegCode);
        preference.put("blackList", false);
        preference.put("rank", randomNumber(1));
        preferences.add(preference);
        io.restassured.response.Response response = restAssuredClient.PUT(gson.toJson(preferences), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("The given preference id does not exist")) {
                Reporter.log("[AssertTrue] got proper error message  [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to update appointment preferences to self  [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"put-preferences-self", "AppointmentPreferences"}, dependsOnGroups = "get-preferences")
    public void testPUTUpdateAppointmentPreferencesToSelf() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        Reporter.log("Updating appointment preferences for the registration [" + sourceRegCode + "]");
        List<HashMap<String, Object>> preferences = new ArrayList<>();
        HashMap<String, Object> preference = new HashMap<>();
        preference.put("id", preferenceId);
        preference.put("attendeeTypeCode", appointmentPreferencesObj.getAttendeeTypeCode());
        preference.put("regCode", sourceRegCode);
        preference.put("blackList", false);
        preference.put("rank", randomNumber(1));
        preferences.add(preference);

        io.restassured.response.Response response = restAssuredClient.PUT(gson.toJson(preferences), url, authenticationScheme);
        if (response.getStatusCode() == HTTP400) {
            if (response.asString().contains("You are not allowed to select yourself as a preference")) {
                Reporter.log("[AssertTrue] got proper error message  [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, "[AssertFailed] assertion failed ");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to update appointment preferences to self  [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"put-preferences", "AppointmentPreferences"}, dependsOnGroups = "get-preferences")
    public void testPUTUpdateAppointmentPreferences() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        Reporter.log("[SCM-25521] Updating appointment preferences for the registration [" + sourceRegCode + "]");
        List<HashMap<String, Object>> preferences = new ArrayList<>();
        HashMap<String, Object> preference = new HashMap<>();
        preference.put("id", preferenceId);
        preference.put("attendeeTypeCode", appointmentPreferencesObj.getAttendeeTypeCode());
        preference.put("regCode", targetRegCode);
        preference.put("blackList", true);
        preference.put("rank", randomNumber(1));
        preferences.add(preference);

        io.restassured.response.Response response = restAssuredClient.PUT(gson.toJson(preferences), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been updated successfully")) {
                Reporter.log("[AssertTrue] Successfully updated new appointment preferences for the registrationCode [" + sourceRegCode + "]");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "put was successful, but assertion failed [Expected=Preference has been updated successfully]");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to update appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    @org.testng.annotations.Test(enabled = true, groups = {"delete-preferences", "AppointmentPreferences"}, dependsOnGroups = "put-preferences")
    public void testDELETEAppointmentPreferences() {
        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        Reporter.log("[SCM-25523] Removing appointment preferences for the registration [" + sourceRegCode + "]");
        io.restassured.response.Response response = restAssuredClient.DELETE(url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            Assert.assertEquals(response.getBody().jsonPath().get("message"), "Preference has been deleted successfully", " Deleted successfully");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to delete/remove appointment preferences for the registrationCode [" + sourceRegCode + "]");
    }

    public boolean AssignRotationToRegistration(String regCode, String rotation) throws Exception {
        boolean flag = false;
        com.certain.external.dto.rotation.RegistrationRotationObj registrationRotationObj = new com.certain.external.dto.rotation.RegistrationRotationObj();
        Reporter.log("Assigning Rotation to the Registration  " + regCode);
        registrationRotationObj.setName(rotation);
        java.lang.String url = urlRotation + "/" + regCode;
        Response response = super.restAssuredClient.POST(gson.toJson(registrationRotationObj), url, authenticationScheme);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == HTTP200) {
            if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(registrationRotationObj), __jsonResponseMessage)) {
                Reporter.log("Successfully Assigned the Rotation to to the Registration Code " + regCode);
                flag = true;
            }
        }
        return flag;
    }

    //SCM-25520
    @org.testng.annotations.Test(enabled = true, groups = {"post-preference-custom-template", "AppointmentPreferences"})
    public void testPOSTAppointmentPreferencesMultipleCustomTemplate() throws Exception {
        String sourceReg = createRegistration(eventCode, "tReg" + randomNumber(5), "sReg" + randomNumber(4) + "@certain.com", "MYORG", "Attendee");
        int noOfPreferences = 2;
        Reporter.log("Posting preferences for the reg [" + sourceReg + "] more than the max preferences");
        ArrayList<String> registrationCodes = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < noOfPreferences; i++) {
            String regCode = createRegistration(eventCode, "tReg" + randomNumber(5), "tReg" + randomNumber(4) + "@cisco.com", "CISCO" + index, "Attendee");
            registrationCodes.add(regCode);
            if (!AssignRotationToRegistration(regCode, customRotation)) {
                Assert.assertTrue(false, " Failed to assign rotation to registration " + regCode);
            }
        }
        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode("Attendee");
            preference.setRegCode(regCode);
            preference.setRank(index);
            index++;
            appointmentPreferencesObjs.add(preference);
        }
        java.lang.String url = urlPath + sourceReg + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);
        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                if (validatePost(sourceReg, appointmentPreferencesObjs)) {
                    Reporter.log("[AssertTrue] Successfully created preferences with 3 registrations", true);
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(false, "[AssertFailed] ]Expected message [Preference has been added successfully] not exist in payload...");
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create appointment preferences for the registrationCode [" + sourceReg + "]");
    }

    private Object PostPreference(String eventCode, String sourceRegCode, List<HashMap<String, String>> userProfiles, boolean sameOrg, String rotation) throws Exception {
        ArrayList<String> registrationCodes = new ArrayList<>();
        String org = "tORG";
        Reporter.log("[SCM-25520] Posting appointment preferences for the registration [" + sourceRegCode + "] set organization");

        //create target registrations
        for (int i = 0; i < userProfiles.size(); i++) {
            if (!sameOrg)
                org += (i + 1);

            String regCode = createRegistration(eventCode, userProfiles.get(i).get("name"), userProfiles.get(i).get("email"), org, userProfiles.get(i).get("attendee"));
            registrationCodes.add(regCode);
            if (rotation != null) {
                if (AssignRotationToRegistration(regCode, rotation)) {
                    Reporter.log("User assigned to custom template rotation " + rotation);
                }
            }
            registrationCodes.add(regCode);
        }

        //create preferences
        int index = 0;
        List<AppointmentPreferencesObj> appointmentPreferencesObjs = new ArrayList<>();
        for (String regCode : registrationCodes) {
            AppointmentPreferencesObj preference = new AppointmentPreferencesObj();
            preference.setAttendeeTypeCode(userProfiles.get(index).get("Attendee"));
            preference.setRegCode(regCode);
            preference.setRank(index + 1);
            index++;
            appointmentPreferencesObjs.add(preference);
        }

        java.lang.String url = urlPath + sourceRegCode + "/preferences";
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(appointmentPreferencesObjs), url, authenticationScheme);

        if (response.getStatusCode() == HTTP200) {
            if (response.asString().contains("Preference has been added successfully")) {
                if (validatePost(sourceRegCode, appointmentPreferencesObjList)) {
                    Reporter.log("[AssertTrue] Successfully created new appointment preferences with target registrationCode [" + targetRegCode + "]");
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(false, "[AssertFailed] ]Expected message [Preference has been added successfully] not exist in payload...");
            } else Assert.assertTrue(false, "validation failed");
        } else
            Assert.assertTrue(false, "[AssertFalse] Failed to create appointment preferences with target registrationCode [" + targetRegCode + "]");
        return new Object();
    }

}
