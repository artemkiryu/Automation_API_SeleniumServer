package com.certain.usecases;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.locations.LocationsObject;
import internal.qaauto.certain.platform.pojo.speakers.SpeakersObject;
import io.restassured.response.Response;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;


import org.testng.Assert;
import org.testng.Reporter;
import internal.qaauto.certain.platform.pojo.sessions.Occurrences;
import internal.qaauto.certain.platform.pojo.sessions.SessionObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
class UCAPICommon extends CertainAPIBase {

    private final AuthenticationScheme auth = new AuthenticationScheme(USERNAME, PASSWORD);

    public TestCase PostSession(String eventCode, SessionObject sessionObject, ExpectedCondition expectedCondition, String accountCode) throws Exception {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions";
        Reporter.log("Posting new session to the event " + eventCode);
        String __jsonRequestBody = gson.toJson(sessionObject);
        Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __jsonResponseMessage = response.asString();

        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (response.getStatusCode() == 200) {
                if (__jsonResponseMessage.contains(expectedCondition.getMessage())) {
                    testCase.setMessage("Session " + sessionObject.getName() + " added successfully to the event " + eventCode);
                    TestCase validate = validateSessionPayload(eventCode, sessionObject, expectedCondition, accountCode);
                    if (validate.isPassed()) {
                        Reporter.log(validate.getMessage());
                        testCase.setSessionInstanceId(validate.getSessionInstanceId());
                        testCase.setPassed(true);
                        testCase.setSessionInstanceIds(validate.getSessionInstanceIds());
                    } else {
                        Reporter.log(validate.getMessage());
                        testCase.setPassed(false);
                    }
                }

            } else {
                if (__jsonResponseMessage.contains(expectedCondition.getMessage())) {
                    testCase.setMessage("Session Post was failed as expected");
                    testCase.setPassed(true);
                } else {
                    testCase.setMessage("Session Post was failed and message validation not as expected");
                    testCase.setPassed(false);
                }

            }

        } else
            testCase.setMessage(response.getStatusLine() + " Failed to post Session");
        return testCase;
    }

    public int[] getSessionAvailableCount(String eventCode, int instanceId, String accountCode) {
        int[] counts = new int[3];
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + instanceId;
        Reporter.log("Getting Session Remaining Count " + instanceId);
        Response response = restAssuredClient.GET(urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            counts[0] = (int) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "capacity");
            counts[1] = (int) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registered");
            counts[2] = (int) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "remaining");
        } else
            Reporter.log(response.getStatusLine() + " Failed to get session instance details for the session instance " + instanceId);

        return counts;
    }

    private TestCase validateSessionPayload(String eventCode, SessionObject sessionObject, ExpectedCondition expectedCondition, String accountCode) throws Exception {
        TestCase testNGTestCase = new TestCase();
        String sessionCode = sessionObject.getSessionCode();
        Reporter.log("Getting Session instanceId... ", true);
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions?sessionCode=" + sessionCode;
        Response response = restAssuredClient.GET(urlPath, auth);
        if (response.getStatusCode() == 200) {
            List<Map<String, Object>> sessionArray = (response.getBody().jsonPath().getList("sessions"));
            ArrayList<String> instanceIds = new ArrayList<>();

            for (Map<String, Object> eachInstance : sessionArray)
                instanceIds.add(eachInstance.get("instanceId").toString());

            testNGTestCase.setSessionInstanceIds(instanceIds);

//            reporter.log("Got SessionId(s) " + instanceIds.toString() + "...Now Getting Sessions by Instance Id", true);
//            urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + instanceIds.get(0);
//            reporter.log("URL: " + urlPath);
//            response = restAssuredClient.GET(urlPath, auth);
//
//            String __jsonResponseMessage1 = response.asString();
//            reporter.log("Status: " + response.statusLine());
//            reporter.log("Payload: " + __jsonResponseMessage1, true);

            Map<String, Object> modifiedRequestJson = new TestDataLoad().convertObjectToMap(sessionObject);
//            deleteKeysFromMap(modifiedRequestJson, new String[]{"sessionType", "name", "sessionLevel", "eventTrack", "abstractDes", "status", "description", "sessionLabel", "isFeeDiscountable", "noOfInstances"});
            deleteKeysFromMap(modifiedRequestJson, new String[]{"sessionType", "name", "sessionLevel", "eventTrack", "notes", "abstractDes", "capacity", "duration", "status", "description", "sessionLabel", "isFeeDiscountable", "noOfInstances"});
            modifiedRequestJson.put("typeName", sessionObject.getSessionType());
            modifiedRequestJson.put("sessionTitle", sessionObject.getName());
            modifiedRequestJson.put("level", sessionObject.getSessionLevel());
            modifiedRequestJson.put("trackName", sessionObject.getEventTrack());
            modifiedRequestJson.put("abstractDesc", sessionObject.getAbstractDes());
            if (sessionObject.getOccurances() != null) {

                for (Occurrences occurrences : sessionObject.getOccurances()) {
                    modifiedRequestJson.put("locationCode", modifiedRequestJson.get("locationCode"));
                    modifiedRequestJson.put("capacity", occurrences.getCapacity());
                    modifiedRequestJson.put("overrideCapacity", Boolean.valueOf(occurrences.isOverrideCapacity()));
                    // du.convertDateFromString(occurrences.getStartTime(),du.UC_LONG_DATE1);
                    // du.convertUCDate(occurrences.getStartTime(),"MM/dd/yyyy h:mm a", "MM/dd/yyyy HH:mm:ss");
                    String startTime = du.convertUCDate(occurrences.getStartTime(), "MM/dd/yyyy h:mm a", "MM/dd/yyyy HH:mm:ss");
                    // String endTime = du.convertUCDate(occurrences.getEndTime(), "MM/dd/yyyy h:mm a", "MM/dd/yyyy HH:mm:ss");
                    modifiedRequestJson.put("startTime", startTime);
                    if (occurrences.getEndTime() != null) {
                        String endTime = du.convertUCDate(occurrences.getEndTime(), "MM/dd/yyyy h:mm a", "MM/dd/yyyy HH:mm:ss");
                        modifiedRequestJson.put("endTime", endTime);
                        // modifiedRequestJson.put("duration", du.dateDifference(startTime, endTime));
                    } else if (occurrences.getEndTime() == null && modifiedRequestJson.get("duration") == null) {
                        //  modifiedRequestJson.put("duration", "60");
                    }
                    modifiedRequestJson.remove("instances");
                }
            }
            modifiedRequestJson.remove("ceuCredits");
            modifiedRequestJson.remove("createSource");
            Reporter.log("Modified Request Params:= " + modifiedRequestJson);

            if (response.getStatusCode() == expectedCondition.getStatusCode()) {
//                if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(modifiedRequestJson), __jsonResponseMessage1)) {
                if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(modifiedRequestJson), gson.toJson(sessionArray.get(0)))) {
                    testNGTestCase.setMessage("Session " + sessionCode + " Validation Success");
                    testNGTestCase.setPassed(true);
                } else
                    testNGTestCase.setMessage(" Session " + sessionCode + " Validation Failed..Unmatched Keys ");
            } else {
                testNGTestCase.setMessage(response.getStatusLine() + " Failed to Get Session " + sessionCode + " Details ");
            }
        }
        return testNGTestCase;
    }

    public TestCase POSTSpeaker(String eventCode, SpeakersObject speakersObject, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/speakers";
        Reporter.log("Creating New Speaker " + speakersObject.getEmail() + " to the Event " + eventCode);
        String __jsonRequestBody = super.gson.toJson(speakersObject);
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponseMessage.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Speaker [" + speakersObject.getEmail() + "] added successfully to event " + eventCode);
                testNGTestCase.setPassed(true);
            } else {
                testNGTestCase.setMessage("Post was successful but Validation Failed please check the test results ");
            }
        } else {
            testNGTestCase.setMessage(response.getStatusLine() + " Failed to post new Speaker [" + speakersObject.getEmail() + " ]");
        }
        return testNGTestCase;
    }

    public int GETSpeakerId(String eventCode, String filter, String value, String accountCode) {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/speakers?" + filter + "=" + value;
        int speakerId = 0;

        Reporter.log("Getting speaker id by first Name " + filter);
        Response response = super.restAssuredClient.GET(urlPath, this.auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            speakerId = Integer.parseInt(jsonHelper.getJsonValueByKey(__jsonResponseMessage, "data", "speakerId"));
        }
        return speakerId;
    }

    public TestCase PostLocation(String eventCode, LocationsObject locationsObject, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/locations";

        Reporter.log("Posting new location to the Event Code " + eventCode);
        String locationCode = locationsObject.getLocationCode();
        String jsonRequestBody = gson.toJson(locationsObject);
        Response response = restAssuredClient.POST(jsonRequestBody, urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponseMessage.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Location [" + locationCode + "] added successfully to event " + eventCode);
                testNGTestCase.setPassed(true);
            } else
                testNGTestCase.setMessage("Post Location was successful but assertion failed ");
        } else {
            testNGTestCase.setMessage(response.getStatusLine() + " Failed to post new location [" + locationCode + " ] ");
        }
        return testNGTestCase;
    }

    public TestCase ScheduleSession(String eventCode, String locationCode, int sessionInstanceId, String startDateTime, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        Map<String, String> jsonBody = new HashMap<>();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/locations/" + locationCode + "/sessions/" + sessionInstanceId;

        Reporter.log("Scheduling Session " + sessionInstanceId + " at location " + locationCode + " at " + startDateTime + "  ");
        jsonBody.put("locationCode", locationCode);
        jsonBody.put("startDate", startDateTime);
        String __jsonRequestBody = gson.toJson(jsonBody);
        Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __jsonResponse = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponse.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Session is successfully scheduled at Location [" + sessionInstanceId + "]");
                testNGTestCase.setPassed(true);
            } else
                testNGTestCase.setMessage("Session scheduled but assertion failed");
        } else {
            testNGTestCase.setMessage(response.getStatusLine() + " Failed to get Location details with filter locationId [" + locationCode + "]");
        }
        return testNGTestCase;
    }

    public TestCase assignSessionToSpeaker(String eventCode, int speakerId, String sessionCode, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionCode + "/speakers/" + speakerId;

        Reporter.log("Assigning Session " + sessionCode + " to the Speaker " + speakerId);
        String __jsonRequestBody = "null";
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, this.auth);
        String __jsonResponse = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponse.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Speaker assigned to the session successfully");
                testNGTestCase.setPassed(true);
            } else {
                testNGTestCase.setMessage("Post successful but assertion  failed");
            }
        } else {
            testNGTestCase.setMessage(response.getStatusLine() + " Failed to assign session " + sessionCode + " " + " to the speaker " + speakerId);
        }
        return testNGTestCase;
    }

    public TestCase AssignRegistrationToSession(String eventCode, String registrationCode, int sessionInstance, ExpectedCondition expectedCondition, String accountCode) throws Exception{

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionInstance + "/registrations";

        Map<String, String> jsonBody = new HashMap<>();
        Reporter.log("Assigning Registration Session " + sessionInstance + " to the Registration " + registrationCode);
        jsonBody.put("registrationCode", registrationCode);
        String __jsonRequestBody = gson.toJson(jsonBody);
        Thread.sleep(5000);
        Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __jsonResponse = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponse.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Successfully Assigned the Session " + sessionInstance + " to the Registration " + registrationCode);
                testNGTestCase.setPassed(true);
            } else {
                testNGTestCase.setMessage(response.getStatusLine() + " Failed to Assign Session to Registration [" + registrationCode + "]");
                testNGTestCase.setPassed(false);
                return testNGTestCase;
            }

        } else if (__jsonResponse.contains("No availability for this session. Can't add registration to session.")) {
            testNGTestCase.setMessage("No availability for this session. Can't add registration to session.");
            testNGTestCase.setStatusCode(response.getStatusCode());
            testNGTestCase.setPassed(false);
            return testNGTestCase;
        }
        return testNGTestCase;
    }

    public TestCase UpdateRegistrationSessionStatus(String eventCode, String registrationCode, String status, int sessionInstance, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionInstance + "/registrations";
        Reporter.log("Updating Registration " + registrationCode + " Session Status to " + status);

        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("registrationCode", registrationCode);
        jsonBody.put("status", status);
        String __jsonRequestBody = gson.toJson(jsonBody);
        Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __jsonResponse = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponse.contains(expectedCondition.getMessage())) {
                testNGTestCase.setMessage("Successfully Changed the Registration Session Status " + status);
                testNGTestCase.setPassed(true);
            } else
                testNGTestCase.setMessage(response.getStatusLine() + " Failed to Update Registration Session Status to [" + status + "]");
        }
        return testNGTestCase;
    }

    public TestCase CancelRegistrationSession(String eventCode, int sessionInstanceId, String registrationCode, ExpectedCondition expectedCondition, String accountCode) {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionInstanceId + "/registrations/" + registrationCode;
        Reporter.log("Cancelling /Removing sessions from registration " + registrationCode);
        Response response = restAssuredClient.DELETE(urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            if (__jsonResponseMessage.contains(expectedCondition.getMessage())) {
                testCase.setMessage("Successfully removed the session from the registration " + registrationCode);
                testCase.setPassed(true);
            } else
                testCase.setMessage("Response Message validation failed Expected: " + expectedCondition.getMessage());
        } else {
            testCase.setMessage(response.getStatusLine() + " Failed to Cancel Registration Session ");
        }
        return testCase;
    }

    public TestCase PublishSession(String eventCode, String sessionCode, String attendees, String accountCode) {

        TestCase testNGTestCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionCode + "/publish";
        Reporter.log("Publishing the session code = " + sessionCode);
        Map<String, Object> attendeesMap = new HashMap<>();

        if (attendees.contains(",")) {
            Object[] attendeeArray = attendees.split(",");
            attendeesMap.put("attendeetypes", attendeeArray);
        } else
            attendeesMap.put("attendeetypes", attendees);

        String jsonRequestBody = gson.toJson(attendeesMap);
        Response response = restAssuredClient.PUT(jsonRequestBody, urlPath, auth);
        String jsonPayload = response.asString();
        if (response.getStatusCode() == 200) {
            if (jsonPayload.contains("Session is published successfully")) {
                Reporter.log("Session [" + sessionCode + "] published successfully");
                testNGTestCase.setMessage("Session published successfully ");
                testNGTestCase.setPassed(true);
            } else
                testNGTestCase.setMessage(" Post was successful but assertion failed");
        } else {
            Assert.assertTrue(false, " Failed to publish session [" + sessionCode + "] " + getExceptionDetails(response));
        }
        return testNGTestCase;
    }

    private Map deleteKeysFromMap(Map hashMap, String[] keys) {
        if (hashMap != null) {
            for (String key : keys) {
                hashMap.remove(key);
            }
        }
        return hashMap;
    }

    public TestCase PostRegistration(String eventCode, String attendee, HashMap profile, ExpectedCondition expectedCondition, String accountCode) {

        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + eventCode;
        Map<String, Object> regJson = new HashMap<>();
        regJson.put("attendeeTypeCode", attendee);
        regJson.put("attendeeType", attendee);
        regJson.put("isAccepted", true);
        regJson.put("isConfirmed", true);
        regJson.put("badgeName", profile.get("firstName"));
        regJson.put("profile", profile);
        String requestMsg = gson.toJson(regJson);
        Response response = restAssuredClient.POST(requestMsg, urlPath, auth);
        String payload = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            String regCode = jsonHelper.getJsonValueByKey(payload, "registrationCode").toString();
            testCase.setRegistrationCode(regCode);
            testCase.setPassed(true);
            testCase.setMessage("Registration successfully created " + regCode);
        } else {
            testCase.setMessage("Failed to create registration for the user provided event code");
        }
        return testCase;
    }

    public TestCase UpdateRegistration(String eventCode, String registrationCode, HashMap regObj, ExpectedCondition expectedCondition, String accountCode) {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + eventCode + "/" + registrationCode;
        String requestMsg = gson.toJson(regObj);
        Response response = restAssuredClient.POST(requestMsg, urlPath, auth);
        String payload = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            String regCode = jsonHelper.getJsonValueByKey(payload, "registrationCode").toString();
            testCase.setRegistrationCode(regCode);
            testCase.setPassed(true);
            testCase.setMessage("Registration " + regCode + " successfully updated ");
        } else {
            testCase.setMessage("Failed to update registration details ");
        }
        return testCase;
    }

    public TestCase GetRegistration(String eventCode, String filter, String value, ExpectedCondition expectedCondition, String accountCode) throws Exception {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + BASE_PATH + "/Registration/" + accountCode + "/" + eventCode + "?" + filter + "=" + value;
        String keyName = "";
        if (filter.contains("like")) {
            keyName = filter.substring(0, filter.length() - 5);
        }
        Response response = restAssuredClient.GET(urlPath, auth);
        String payload = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            ArrayList<Map<String, Object>> regList = jsonHelper.getJsonArray(payload, "registrations");
            int __profileInstanceCount = jsonHelper.getInstanceCount(payload, "registrations.profile", filter, value);
            Map<String, Object> eachRegProfile;
            testCase.setStatusCode(200);
            if (__profileInstanceCount > 1) {
                ArrayList names = new ArrayList();
                for (Map<String, Object> eachReg : regList) {
                    eachRegProfile = (Map) eachReg.get("profile");
                    names.add(eachRegProfile.get(keyName));
                }
                testCase.setMessage("There are " + __profileInstanceCount + " matching Profile(s) Found \n" + names.toString());
                testCase.setPassed(true);

            } else if (__profileInstanceCount == 1) {
                testCase.setMessage("There is one matching Profile(s) Found with registrationCode: " + regList.get(0).get("registrationCode"));
                testCase.setRegistrationCode(regList.get(0).get("registrationCode").toString());
                testCase.setPassed(true);
            }

        } else if (response.getStatusCode() == 404) {
            testCase.setMessage("No profiles found matching criteria");
            testCase.setPassed(true);
            testCase.setStatusCode(404);
        } else
            testCase.setMessage("Error getting registrations list - " + response.getStatusLine());

        return testCase;
    }

    public TestCase GETRegistrationSession(String eventCode, String sessionCode, String registrationCode, String keyToVerify, ExpectedCondition expectedCondition, String accountCode) {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/registrations/" + registrationCode + "/sessions";
        Reporter.log("Getting Registration Sessions of Registration code " + registrationCode);
        Response response = super.restAssuredClient.GET(urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            testCase.setStatusCode(200);
            String __actualRegCode = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registrationCode").toString();
            String __actualSessionCode = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "instances", "sessionCode");
            if (keyToVerify != null) {
                String __actualKeyToVerify = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "instances", keyToVerify);
            }

            if (__actualRegCode.equals(registrationCode) && __actualSessionCode.equals(sessionCode)) {
                testCase.setMessage("Registration session retrieved of registration code " + registrationCode);
                testCase.setPassed(true);
            } else
                testCase.setMessage("Registration details retrieved but validation failed");

        } else if (response.getStatusCode() == 404) {
            testCase.setStatusCode(response.getStatusCode());
            testCase.setMessage("registration " + registrationCode + " is not registered with the session " + sessionCode);
            testCase.setPassed(true);
        } else {
            testCase.setMessage(response.getStatusLine() + " Failed to get Registration session retrieved of registration code " + registrationCode);
        }
        return testCase;
    }

    public TestCase GETSessionRegistrations(String eventCode, int sessionInstanceId, String registrationCode, ExpectedCondition expectedCondition, String accountCode) {
        TestCase testCase = new TestCase();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/sessions/" + sessionInstanceId + "/registrations";
        Reporter.log("Getting session registrations by Instance id = " + sessionInstanceId);
        Response response = restAssuredClient.GET(urlPath, auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == expectedCondition.getStatusCode()) {
            int __actualInstanceId = (int) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "instanceId");
            ArrayList<Map<String, Object>> registrations = jsonHelper.getJsonArray(__jsonResponseMessage, "registrations");
            String regStatus = registrations.get(0).get("registrationStatus").toString();
            boolean isFound = false;
            for (Map<String, Object> registration : registrations) {
                if (registration.get("registrationCode").equals(registrationCode)) {
                    isFound = true;
                }
            }
            if (isFound && __actualInstanceId == sessionInstanceId && regStatus.equals("Registered")) {
                testCase.setMessage("The registrationCode " + registrationCode + " is already registered with the session");
                testCase.setPassed(true);
            } else {
                testCase.setMessage("The registration " + registrationCode + " not registered with the session " + sessionInstanceId);
                testCase.setStatusCode(404);
            }

        } else if (response.getStatusCode() == 404) {
            testCase.setStatusCode(404);

        } else {
            Assert.assertTrue(false, response.getStatusLine() + " failed get session details by session instance id");
        }
        return testCase;
    }

}
