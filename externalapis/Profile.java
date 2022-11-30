package com.certain.External.service.v1;

import com.certain.external.dto.profile.ProfileObj;
import com.certain.external.dto.registration.RegistrationObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.*;
import internal.qaauto.certain.platform.services.ProfileObjSvc;
import internal.qaauto.certain.platform.services.RegistrationObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

@SuppressWarnings("all")
public class Profile extends CertainAPIBase {

    private final ArrayList<String> profilePins = new ArrayList<>();
    private ProfileObjSvc profileObjSvc = new ProfileObjSvc();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private List<ProfileObj> profileList = new ArrayList<>();
    private List<com.certain.external.dto.profile.Address> addressList = new ArrayList<>();
    private List<com.certain.external.dto.profile.AltAddress> altAddressList = new ArrayList<>();
    private List<com.certain.external.dto.profile.ShipAddress> shipAddressList = new ArrayList<>();
    private ProfileObj profileObj = new ProfileObj();
    private String profilePin, profilePin2;
    private String[] orderBy = {"dateModified_asc", "dateModified_desc", "dateCreated_asc",
            "dateCreated_desc", "pkProfileId_asc", "pkProfileId_desc"};
    private String[] includeList = {"profile_questions", "associate", "assistant", "travel_info", "event_list"};
    private AuthenticationScheme authenticationScheme = new AuthenticationScheme(USERNAME, PASSWORD);

    private String accountCode;
    private String eventCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;
        try {
            profileList = testDataLoad.getProfileObjData("PrivateFinancial,Media,PrivatePersonal,Assistant,Associate");
            addressList = testDataLoad.getAddressObjData();
            altAddressList = testDataLoad.getAltAddressObjData();
            shipAddressList = testDataLoad.getShipAddressObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"post-profile-required", "ProfileObj"})
    public void testPOSTProfileRequiredFields() throws Exception {
        String uniqueCode = randomString(15);
        ProfileObj profileObj = profileList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(uniqueCode);
        profileObj.setAddress(addressList.get(0));
        profileObj.setAltAddress(altAddressList.get(0));
        profileObj.setShipAddress(shipAddressList.get(0));
        profilePin2 = profileObj.getProfilePin();
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"post-profile-info", "ProfileObj"})
    public void testPOSTProfile() throws Exception {
        String uniqueCode = randomString(15);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj = profileList.get(0);
        profileObj.setProfilePin(uniqueCode);
        profileObj.setAddress(addressList.get(0));
        profileObj.setAltAddress(altAddressList.get(0));
        profileObj.setShipAddress(shipAddressList.get(0));
        profilePin = profileObj.getProfilePin();
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            profileObj.setDateCreated(testDataLoad.toDate((String) jsonHelper.getJsonValueByKey(testCase.getPayload(), "dateCreated")));
            profileObj.setDateModified(testDataLoad.toDate((String) jsonHelper.getJsonValueByKey(testCase.getPayload(), "dateModified")));
            profileObj.setPkProfileId(jsonHelper.getJsonValueByKey(testCase.getPayload(), "pkProfileId").toString());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-profile-info", "ProfileObj"},
            dependsOnGroups = "post-profile-info")
    public void testGETProfilesByPin() throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, profilePin, null);
        if (testCase.isPassed()) {
            Reporter.log("Profile [" + profilePin + "] details retrieved successfully", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profiles-includeList", parallel = false)
    public Object[][] getProfilesIncludeList() throws Exception {
        Object[][] includeLists = new Object[includeList.length][1];
        int i = 0;
        for (String item : includeList) {
            includeLists[i][0] = item;
            i++;
        }
        return includeLists;
    }

    @Test(dataProvider = "get-profiles-includeList", enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesWithIncludeList(String includeList) throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, new String[]{includeList}, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesWithIncludeListAll() throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, new String[]{"profile_questions", "associate", "assistant", "travel_info", "event_list"}, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(dataProvider = "get-profiles-includeList", enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesWithIncludeListUsingMaxResultsAndStartIndex(String includeList) throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, new String[]{includeList}, null, null, 5, 2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesWithIncludeListMaxResults() throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, new String[]{"assistant", "associate"}, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesWithIncludeListMaxResultsAndStartIndex() throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, new String[]{"assistant", "associate"}, null, null, 5, 3);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profiles1", parallel = false)
    public Object[][] getProfiles() throws Exception {
        Object obj = testDataLoad.getProfileObjFilters(profileList.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-profiles1", enabled = true, groups = {"get-profile-info", "ProfileObj"},
            dependsOnGroups = "post-profile-info")
    public void testGETProfilesWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profiles-multi-filters")
    public Object[][] getProfileMultipleFilters() throws Exception {
        Object obj = testDataLoad.getProfileObjFilters(profileList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-profiles-multi-filters", enabled = true, groups = {"get-profile-info", "ProfileObj"},
            dependsOnGroups = "post-profile-info")
    public void testGETProfilesWithMultipleSearchFilters(HashMap<String, Object> searchFilters) throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, null, searchFilters, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No profiles found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profiles-orderby", parallel = false)
    public Object[][] getProfilesOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "get-profiles-orderby", enabled = true, groups = {"get-profile-info", "ProfileObj"})
    public void testGETProfilesOrderBy(String orderBy) throws Exception {
        TestCase testCase = profileObjSvc.getProfile(ACCOUNT_CODE, null, null, orderBy, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "update-profile-multiple", parallel = false)
    public Object[][] profileUpdateData() {
        Object[][] usersData = new Object[profileList.size() - 2][1];
        int j = 0;
        try {
            for (int i = 2; i < profileList.size(); i++) {
                Map<String, Object> __jsonBody = testDataLoad.convertObjectToMap(profileList.get(i));
                __jsonBody.remove("profilePin");
                __jsonBody.remove("ACCOUNT_CODE");
                usersData[j][0] = __jsonBody;
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersData;
    }

    @Test(dataProvider = "update-profile-multiple", enabled = true, groups = {"update-profile-required", "ProfileObj"},
            dependsOnGroups = "post-profile-required", priority = 3)
    public void testPOSTUpdateProfileMultiple(Map<String, Object> profileObject) throws Exception {
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profilePin2, profileObject);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"update-profile-info", "ProfileObj"},
            dependsOnGroups = "post-profile-info", priority = 4)
    public void testPOSTUpdateProfile() throws Exception {
        profileObj = profileList.get(1);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(profilePin);
        profileObj.setAddress(addressList.get(0));
        TestCase testCase = profileObjSvc.updateProfile(ACCOUNT_CODE, profilePin, profileObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"delete-profile-info", "ProfileObj"},
            dependsOnGroups = "update-profile-info", priority = 5)
    public void testDELETEProfile() throws Exception {
        TestCase testCase = profileObjSvc.deleteProfile(ACCOUNT_CODE, profilePin);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"delete-profile-required", "ProfileObj"},
            dependsOnGroups = "update-profile-required")
    public void testDELETEProfileReq() throws Exception {
        TestCase testCase = profileObjSvc.deleteProfile(ACCOUNT_CODE, profilePin2);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "post-profile-identifiers", parallel = false)
    public Object[][] postProfileIdentifiers() throws Exception {
        Object[][] identifiers = new Object[6][2];
        identifiers[0][0] = "SYNC_EMAIL";
        identifiers[0][1] = profileList.get(6);
        identifiers[1][0] = "SYNC_PIN";
        identifiers[1][1] = profileList.get(7);
        identifiers[2][0] = "SYNC_EXTERNAL";
        identifiers[2][1] = profileList.get(8);
        identifiers[3][0] = "EMAIL";
        identifiers[3][1] = profileList.get(9);
        identifiers[4][0] = "PIN";
        identifiers[4][1] = profileList.get(10);
        identifiers[5][0] = "EXTERNAL";
        identifiers[5][1] = profileList.get(11);
        return identifiers;
    }

    @Test(dataProvider = "post-profile-identifiers", enabled = true, description = "get profile", groups = {"post-profile-pos", "ProfileObj"},
            priority = 1)
    public void testPOST_NewProfileWithIdentifiersPositive(String identifier, ProfileObj profileObj) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Profile/" + ACCOUNT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        profileObj.setAddress(addressList.get(0));

        if (identifier.equals("SYNC_EMAIL") || identifier.equals("EMAIL")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setProfilePin(null);
            profileObj.setExternalKey(null);
        }

        if (identifier.equals("SYNC_PIN") || identifier.equals("PIN")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'profilePin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setEmail(null);
            profileObj.setExternalKey(null);
        }

        if (identifier.equals("SYNC_EXTERNAL") || identifier.equals("EXTERNAL")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'externalKey' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setProfilePin(null);
            profileObj.setEmail(null);
        }

        String ____jsonBody = gson.toJson(profileObj);
        io.restassured.response.Response response = restAssuredClient.POST(____jsonBody, urlPath, authenticationScheme);

        if (response.getStatusCode() == 200) {
            String profileId = (String) jsonHelper.getJsonValueByKey(response.asString(), "pkProfileId");
            profilePins.add(profileId);
            Reporter.log("Comparing Payload with Request Data : ", true);
            if (jsonHelper.compareRequestWithResponsePayload(____jsonBody, response.asString())) {
                Reporter.log("Data posted returned correctly...\nProfile Created successfully: " + profileObj.getFirstName() + " with profile Id:" + profileId, true);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Post was successful but data comparison failed...Keys that did not match the request ");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post new profile " + profileObj.getFirstName());
        }
    }

    @Test(dataProvider = "post-profile-identifiers", enabled = true, description = "get profile", groups = {"post-profile-pos1", "ProfileObj"},
            priority = 2, dependsOnGroups = "post-profile-pos")
    public void testPOST_ProfileWithIdentifierFoundAndUpdate(String identifier, ProfileObj profileObj) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Profile/" + ACCOUNT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        String uniqueNum = randomNumber(3);
        String pin = profileObj.getProfilePin();
        String emailId = profileObj.getEmail();
        String exKey = profileObj.getExternalKey();
        profileObj.setProfilePin(null);
        profileObj.setEmail(null);
        profileObj.setExternalKey(null);

        if (identifier.equals("SYNC_EMAIL") || identifier.equals("EMAIL")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setFirstName("FNUpdateForEmail" + uniqueNum);
            profileObj.setLastName("LNUpdateForEmail" + uniqueNum);
            profileObj.setEmail(emailId);
        }
        if (identifier.equals("SYNC_PIN") || identifier.equals("PIN")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'profilePin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setFirstName("FNUpdateForPin" + uniqueNum);
            profileObj.setLastName("LNUpdateForPin" + uniqueNum);
            profileObj.setProfilePin(pin);
        }
        if (identifier.equals("SYNC_EXTERNAL") || identifier.equals("EXTERNAL")) {
            Reporter.log("POSTING PROFILE HAVING USING " + identifier + " AND THE 'externalKey' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setFirstName("FNUpdateForExternal" + uniqueNum);
            profileObj.setLastName("LNUpdateForExternal" + uniqueNum);
            profileObj.setExternalKey(exKey);
        }

        String __jsonRequestBody = super.gson.toJson(profileObj);
        io.restassured.response.Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, authenticationScheme);

        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            String profileId = (String) jsonHelper.getJsonValueByKey(__responsePayloadJSON, "pkProfileId");
            boolean found = false;
            for (String profilePin : profilePins) {
                if (profilePin.equalsIgnoreCase(profileId)) {
                    found = true;
                }
            }
            if (found) {
                String fName = jsonHelper.getJsonValueByKey(__responsePayloadJSON, "firstName").toString();
                String lName = jsonHelper.getJsonValueByKey(__responsePayloadJSON, "lastName").toString();
                if (fName.equals(profileObj.getFirstName()) && lName.equals(profileObj.getLastName())) {
                    Reporter.log("Profile Id " + profileId + " already exist and it got updated ", true);
                    Assert.assertTrue(true);
                } else {
                    Reporter.log("Profile Id " + profileId + " already exist in database, but did not update the profile ", true);
                    Assert.assertTrue(true);
                }
            } else {
                Assert.assertTrue(false, "Profile Id not found in database looks like new profile created with id " + profileId);
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post new profile " + profileObj.getFirstName());
        }
    }

    @Test(dataProvider = "post-profile-identifiers", enabled = true, description = "delete profile", groups = {"post-profile2", "ProfileObj"},
            priority = 3)
    public void testPOSTProfileWithIdentifiersNegative(String identifier, ProfileObj profieObj1) throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/Profile/" + ACCOUNT_CODE + "?profileIdentifier=" + identifier + "&allowduplicates=no";
        String uniqueKey = randomNumber(5);
        ProfileObj profileObj;
        profileObj = profileList.get(1);
        profileObj.setFirstName("PRONEG " + uniqueKey);
        profileObj.setProfilePin(null);
        profileObj.setEmail(null);
        profileObj.setExternalKey(null);

        if (identifier.equals("SYNC_EMAIL") || identifier.equals("EMAIL")) {
            Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'email' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setProfilePin("CERTAPI" + uniqueKey);
            profileObj.setExternalKey("EKEY" + uniqueKey);
        }

        if (identifier.equals("SYNC_PIN") || identifier.equals("PIN")) {
            Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'pin' FIELD IN THE REQUEST AND NO DUPLICATES", true);
            profileObj.setEmail(profileObj.getFirstName() + "@gmail.com");
            profileObj.setExternalKey("EKEY" + uniqueKey);
        }

        if (identifier.equals("SYNC_EXTERNAL") || identifier.equals("EXTERNAL")) {
            Reporter.log("POST PROFILE USING " + identifier + " WITHOUT HAVING 'externalKey' FIELD IN THE REQUEST NO DUPLICATES", true);
            profileObj.setEmail(profileObj.getFirstName() + "@gmail.com");
            profileObj.setProfilePin("CERTAPI" + uniqueKey);
        }

        String __jsonRequestBody = super.gson.toJson(profileObj);
        io.restassured.response.Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, authenticationScheme);

        if (response.getStatusCode() == 400) {
            Reporter.log("Request Failed with Data fields missing error");
            Assert.assertTrue(true);

        } else if (response.getStatusCode() == 200) {
            Assert.assertTrue(false, "Post was successful, returned 200 OK");

        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post new profile");
        }
    }

    @Test(enabled = true, groups = {"post-profile-required", "ProfileObj"}, description = "Post Profile twice using same pin")
    public void testPOSTProfileRequiredFieldsSCM31067() throws Exception {
        //POST Profile
        String uniqueCode = randomString(15);
        ProfileObj profileObj = profileList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(uniqueCode);
        profileObj.setAddress(addressList.get(0));
        profileObj.setAltAddress(altAddressList.get(0));
        profileObj.setShipAddress(shipAddressList.get(0));
        profilePin2 = profileObj.getProfilePin();
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        int count = profileObjSvc.getProfileUsingPin(ACCOUNT_CODE, profilePin2);
        if (count > 1) {
            Assert.assertFalse(false, "Profile does not exist with same pin");
            Reporter.log("Profile exist with same pin", true);
        } else {
            Assert.assertTrue(true, "Profile exist with same pin");
            Reporter.log("Profile does not exist with same pin", true);
        }

    }

    @Test(enabled = true, groups = {"post-profileanonymize-required"})
    public void testPOSTProfileAnonymization() throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/ProfileAnonymize/" + ACCOUNT_CODE;
        String uniqueCode = randomString(15);
        ProfileObj profileObj = profileList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(uniqueCode);
        profileObj.setAddress(addressList.get(0));
        profileObj.setAltAddress(altAddressList.get(0));
        profileObj.setShipAddress(shipAddressList.get(0));
        String profilePin;
        profilePin = profileObj.getProfilePin();
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        String pkProfileId = null;
        if (testCase.isPassed()) {
            pkProfileId = (String) jsonHelper.getJsonValueByKey(testCase.getPayload(), "pkProfileId");
            Reporter.log("Value of pk profile id " + pkProfileId, true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-------------POST Profile Anonymization -----------------------------------------", true);

        ArrayList<String> pkProfileIdList = new ArrayList<>();
        String a[] = pkProfileId.split(",");
        for (String s : a) {
            pkProfileIdList.add(s);
        }

        ProfileAnonymizationObj profileAnonymizationObj = new ProfileAnonymizationObj();
        profileAnonymizationObj.setPkProfileId(pkProfileIdList);

        String __jsonRequestBody = super.gson.toJson(profileAnonymizationObj);
        io.restassured.response.Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, authenticationScheme);
        boolean found = false;
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            if (__responsePayloadJSON.contains("profiles successfully anonymized.")) {
                Assert.assertTrue(true, "Profiles anonymized successfully");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post profile anonymization" + pkProfileId);
        }

        Reporter.log("---------------Get Profile Anonymization and verify pk profile id------------------------", true);

        urlPath = urlPath + "?limit=-1";
        response = restAssuredClient.GET(urlPath, authenticationScheme);
        String newpkProfileId = null;
        List<String> newpkProfileIdList = new ArrayList<>();
        Map<String, String> filterData = new HashMap<>();
        List<HashMap<String, Object>> profilesAnonyList;
        if (response.getStatusCode() == 200) {
            profilesAnonyList = jsonHelper.getList(response.asString(), "results");
            for (int i = 0; i < profilesAnonyList.size(); i++) {
                String profileIds = (String) profilesAnonyList.get(i).get("pkProfileID");
                newpkProfileIdList.add(profileIds);
            }
            Reporter.log("Value of pk profile id " + newpkProfileIdList, true);
            if (newpkProfileIdList.contains(pkProfileId)) {
                Assert.assertTrue(true, "Profile anonymized with matching pkprofile id " + pkProfileId);
            } else {
                Assert.assertTrue(false, "Profile anonymized with matching pkprofile id but not displaying in Get Response" + pkProfileId);

            }
        } else {
            Assert.assertFalse(false, "Profiles are not getting anonymized");
        }

    }

    @Test(enabled = true, groups = {"post-profileanonymizemultiple-required"})
    public void testPOSTMultipleProfileAnonymization() throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/ProfileAnonymize/" + ACCOUNT_CODE;
        String uniqueCode = randomString(15);
        ProfileObj profileObj = profileList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(uniqueCode);
        String profilePin;
        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        String pkProfileId = null;
        ArrayList<String> pkProfileIdList = new ArrayList<>();
        if (testCase.isPassed()) {
            pkProfileId = (String) jsonHelper.getJsonValueByKey(testCase.getPayload(), "pkProfileId");
            pkProfileIdList.add(pkProfileId);
            Reporter.log("Value of pk profile id " + pkProfileId, true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        uniqueCode = randomString(15);
        profileObj.setProfilePin(uniqueCode);
        testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, true);
        if (testCase.isPassed()) {
            pkProfileId = (String) jsonHelper.getJsonValueByKey(testCase.getPayload(), "pkProfileId");
            pkProfileIdList.add(pkProfileId);
            Reporter.log("Value of pk profile id " + pkProfileId, true);
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-------------POST Profile Anonymization -----------------------------------------", true);

        ProfileAnonymizationObj profileAnonymizationObj = new ProfileAnonymizationObj();
        profileAnonymizationObj.setPkProfileId(pkProfileIdList);

        String __jsonRequestBody = super.gson.toJson(profileAnonymizationObj);
        io.restassured.response.Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, authenticationScheme);
        boolean found = false;
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            if (__responsePayloadJSON.contains("profiles successfully anonymized.")) {
                Assert.assertTrue(true, "Profiles anonymized successfully");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post profile anonymization" + pkProfileId);
        }

        Reporter.log("---------------Get Profile Anonymization and verify pk profile id------------------------", true);

        urlPath = urlPath + "?limit=-1";
        response = restAssuredClient.GET(urlPath, authenticationScheme);
        String newpkProfileId = null;
        List<String> newpkProfileIdList = new ArrayList<>();
        Map<String, String> filterData = new HashMap<>();
        List<HashMap<String, Object>> profilesAnonyList;
        if (response.getStatusCode() == 200) {
            profilesAnonyList = jsonHelper.getList(response.asString(), "results");
            for (int i = 0; i < profilesAnonyList.size(); i++) {
                String profileIds = (String) profilesAnonyList.get(i).get("pkProfileID");
                newpkProfileIdList.add(profileIds);
            }
            Reporter.log("Value of pk profile id " + newpkProfileIdList, true);
            boolean flag = false;
            for (String s : pkProfileIdList) {
                flag = false;
                if (newpkProfileIdList.contains(s)) {
                    flag = true;
                }
            }
            if (flag) {
                Assert.assertTrue(flag, "Profile anonymized with matching pkprofile id " + pkProfileId);
            } else {
                Assert.assertTrue(flag, "Profile are not getting anonymized with matching pkprofile id " + pkProfileId);
            }
        } else {
            Assert.assertFalse(false, "Profiles are not getting anonymized");
        }

    }

    @Test(enabled = true, groups = {"post-profileReg-required"})
    public void testPOSTProfileAnonymizationWithRegistration() throws Exception {
        String urlPath = SERVER_HOST + BASE_PATH + "/ProfileAnonymize/" + ACCOUNT_CODE;
        String uniqueCode = randomString(12);
        List<RegistrationObj> registrationList = testDataLoad.getRegistrationObjData(ACCOUNT_CODE);
        RegistrationObj registrationObj = registrationList.get(0);
        registrationObj.getProfile().setPin(uniqueCode);
        registrationObj.getProfile().setExternalKey("EK" + uniqueCode);
        registrationObj.getProfile().setFirstName("Reg4 " + randomNumber(2));
        registrationObj.getProfile().setLastName("MandatoryFieldsOnly " + randomNumber(2));
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        RegistrationObjSvc registrationObjSvc = new RegistrationObjSvc();
        TestCase testCase = registrationObjSvc.postRegistration(ACCOUNT_CODE, eventCode, registrationObj, expectedCondition, true);
        String regCode = null;
        String pkProfileId = null;
        ArrayList<String> pkProfileIdList = new ArrayList<>();
        if (testCase.isPassed()) {
            regCode = testCase.getRegistrationCode();
            Reporter.log(testCase.getMessage() + "\nregistrationCode = " + regCode, true);
            HashMap<String, Object> profileAbj = (HashMap) jsonHelper.getJsonValueByKey(testCase.getPayload(), "profile");
            pkProfileId = profileAbj.get("pkProfileId").toString();
            pkProfileIdList.add(pkProfileId);
            Reporter.log("Value of pk profile id " + pkProfileId, true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

        Reporter.log("-------------POST Profile Anonymization -----------------------------------------", true);

        ProfileAnonymizationObj profileAnonymizationObj = new ProfileAnonymizationObj();
        profileAnonymizationObj.setPkProfileId(pkProfileIdList);

        String __jsonRequestBody = super.gson.toJson(profileAnonymizationObj);
        io.restassured.response.Response response = restAssuredClient.POST(__jsonRequestBody, urlPath, authenticationScheme);
        boolean found = false;
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            if (__responsePayloadJSON.contains("profiles successfully anonymized.")) {
                Assert.assertTrue(true, "Profiles anonymized successfully");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post profile anonymization" + pkProfileId);
        }

        Reporter.log("---------------Get Profile Anonymization and verify pk profile id------------------------", true);

        urlPath = urlPath + "?limit=-1";
        response = restAssuredClient.GET(urlPath, authenticationScheme);
        String newpkProfileId = null;
        List<String> newpkProfileIdList = new ArrayList<>();
        List<String> newRegCodeIdsList = new ArrayList<>();
        Map<String, String> filterData = new HashMap<>();
        List<HashMap<String, Object>> profilesAnonyList;

        if (response.getStatusCode() == 200) {
            profilesAnonyList = jsonHelper.getList(response.asString(), "results");
            for (int i = 0; i < profilesAnonyList.size(); i++) {
                String profileIds = (String) profilesAnonyList.get(i).get("pkProfileID");
                ArrayList registration = (ArrayList) profilesAnonyList.get(i).get("registrations");
                for (int j = 0; j < registration.size(); j++) {
                    HashMap<String, String> values = (HashMap) registration.get(j);
                    String regCodes = values.get("registrationCode").toString();
                    Reporter.log("-------Value of reg codes ------" + regCodes);
                    newRegCodeIdsList.add(regCodes);
                }
                newpkProfileIdList.add(profileIds);

            }
            Reporter.log("Value of pk profile id " + newpkProfileIdList, true);
            Reporter.log("Value of Reg codes  " + newRegCodeIdsList, true);

            if (newpkProfileIdList.contains(pkProfileId) && newRegCodeIdsList.contains(regCode)) {
                Assert.assertTrue(true, "Profile anonymized with matching pkprofile id " + pkProfileId + "registration code " + regCode);
            } else {
                Assert.assertTrue(false, "Profile are not getting anonymized with matching pkprofile id " + pkProfileId + "registration code " + regCode);

            }
        } else {
            Assert.assertFalse(false, "Profile are not getting anonymized with matching pkprofile id with status code 200");
        }
    }

    @DataProvider(name = "get-profileAnonymize", parallel = false)
    public Object[][] getProfileAnonymizeFilters() throws Exception {
        Object obj = testDataLoad.getProfileAnonymizeObjFilters();
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-profileAnonymize", enabled = true, groups = {"get-profileanonymize-info"},
            dependsOnGroups = {"post-profileanonymizemultiple-required", "post-profileanonymize-required"}, priority = 9)
    public void testGETProfileAnonymizeWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = profileObjSvc.getProfileAnonymize(ACCOUNT_CODE, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profileAnonymize1", parallel = false)
    public Object[][] getProfileAnonymizeMultipleFilters() throws Exception {
        Object obj = testDataLoad.getProfileAnonymizeObjFilters();
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @Test(dataProvider = "get-profileAnonymize1", enabled = true, groups = {"get-profileanonymize-info"},
            priority = 10, dependsOnGroups = "post-profileanonymizemultiple-required")
    public void testGETProfileAnonymizeWithMultipleSearchFilters(HashMap<String, Object> searchFilters) throws Exception {
        TestCase testCase = profileObjSvc.getProfileAnonymize(ACCOUNT_CODE, searchFilters, 0, 0, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @DataProvider(name = "get-profileAnonymize2", parallel = false)
    public Object[][] getProfileAnonymizeMultipleFiltersOnlyDate() throws Exception {
        Object obj = testDataLoad.getProfileAnonymizeObjFiltersOnlyDate();
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @Test(dataProvider = "get-profileAnonymize2", enabled = true, groups = {"get-profileanonymize-info"},
            priority = 11)
    public void testGETProfileAnonymizeWithMultipleSearchFiltersOnlyDate(HashMap<String, Object> searchFilters) throws Exception {
        TestCase testCase = profileObjSvc.getProfileAnonymize(ACCOUNT_CODE, searchFilters, 0, 0, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @Test(enabled = true, groups = {"post-profile-RegQues"}, priority = 1, dependsOnGroups = {"post-profile-pos1", "update-profile-required"})
    public void testPOSTProfileWithProfileQuestions() throws Exception {
        String uniqueCode = randomString(12);

        profileList = testDataLoad.getProfileObjData("PrivateFinancial,Media,PrivatePersonal");
        ProfileObj profileObj = profileList.get(0);

        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        profileObj.setProfilePin(uniqueCode);
        profileObj.setProfileQuestions(testDataLoad.profileQuestionsObj());

        TestCase testCase = profileObjSvc.postProfile(ACCOUNT_CODE, profileObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }
}