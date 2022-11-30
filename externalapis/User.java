package com.certain.External.service.v1;

import com.certain.external.dto.user.UserObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.*;
import internal.qaauto.certain.platform.tom.ConfigProperties;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.util.*;

@SuppressWarnings("all")
public class User extends CertainAPIBase {

    private final String businessObject = "User";
    private final AuthenticationScheme authenticationScheme = new AuthenticationScheme();
    private final TestDataLoad testDataLoad = new TestDataLoad();
    final private String urlPath = SERVER_HOST + BASE_PATH + "/User";
    final private String[] requiredFields = {"name", "email", "username"};
    final private String[] orderBy = {"dateModified_asc", "dateModified_desc", "dateCreated_asc", "dateCreated_desc", "name_asc", "name_desc", "username_asc", "username_desc"};
    private LinkedList<UserObj> userObjList;
    private boolean isPassed = false;
    private Map<String, String> userRequiredFieldsOnlyData = new LinkedHashMap<>();
    private String userName;
    private String USERNAME, USERNAME_REQ;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        if (SERVER_HOST.toString().contains("certainazure1")) {
            authenticationScheme.setUsername("neha.goyalrootaccount@infoobjects.com");
            authenticationScheme.setPassword("Neha@1234");
        } else {
            authenticationScheme.setUsername("system");
            authenticationScheme.setPassword("%CC7L60!08g8");
        }
        try {
            userObjList = testDataLoad.getUserObjData(ACCOUNT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "get-users-orderby", parallel = false)
    public Object[][] getUsersOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "get-users-orderby", enabled = true, groups = {"get-events-obj", "User"})
    public void testGETUsersOfAccountOrderBy(String orderBy) throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, orderBy, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria...", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postUser", "User"})
    public void testPOSTUser() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        userObjList.get(0);
        TestCase testCase = postUser(ACCOUNT_CODE, null, userObjList.get(0), expectedCondition, false);
        USERNAME = userObjList.get(0).getUsername();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            UserObj userObj = (UserObj) testCase.getDtoObject();
            userObjList.get(0).setDateModified(userObj.getDateModified());
            userObjList.get(0).setDateCreated(userObj.getDateCreated());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
        //Post 5 users
        for (int i = 7; i < 11; i++) {
            testCase = postUser(ACCOUNT_CODE, null, userObjList.get(i), expectedCondition, false);
            USERNAME = userObjList.get(i).getUsername();
            if (testCase.isPassed()) {
                Reporter.log(testCase.getMessage(), true);
                UserObj userObj = (UserObj) testCase.getDtoObject();
                userObjList.get(i).setDateModified(userObj.getDateModified());
                userObjList.get(i).setDateCreated(userObj.getDateCreated());
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getUser", "User"})
    public void testGETUsersOfAccount() throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, null, null, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria...", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getUser", "User"})
    public void testGETUsersOfAccountUsingMaxResult() throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, null, null, null, 5, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria...", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getUser", "User"})
    public void testGETUsersOfAccountUsingMaxResultWithStartIndex() throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, null, null, null, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria...", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getUser", "User"}, dependsOnGroups = "postUser")
    public void testGETUserDetails() throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, USERNAME);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = "updateUser", dependsOnGroups = "postUser", priority = 4)
    public void testPOSTUpdateUserDetails() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        UserObj userObj = userObjList.get(0);
        userObj.setUsername(USERNAME_REQ);
        userObj.setName(userObj.getName() + " updated");
        TestCase testCase = postUser(ACCOUNT_CODE, USERNAME_REQ, userObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-users", parallel = false)
    public Object[][] getUserData() throws Exception {
        Object obj = testDataLoad.getUserObjFilters(userObjList.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-users", enabled = true, groups = {"getUser", "User"}, priority = 2, dependsOnGroups = "postUser")
    public void testGETUserWithSearchFilter(String key, String value) throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, key, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-users-multi-filters")
    public Object[][] getUsersMultipleFilters() throws Exception {
        Object obj = testDataLoad.getUserObjFilters(userObjList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-users-multi-filters", enabled = false, groups = {"getUser", "User"}, priority = 2, dependsOnGroups = "postUser")
    public void testGETUserWithMultipleSearchFilter(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = getUsers(ACCOUNT_CODE, multipleFilters, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No Users found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(dataProvider = "get-users-multi-filters", enabled = true, groups = {"get-user-filters", "User"}, priority = 2, dependsOnGroups = "postUser")
    public void testGETUserWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Retrieving user details by search filter ");
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(authenticationScheme);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        List<ArrayList> mapToArray = testDataLoad.mapToArray(multipleFilters);
        ArrayList<String> keys = mapToArray.get(0);
        ArrayList values = mapToArray.get(1);
        for (int i = 0; i < keys.size(); i++) {
            request.addQueryParameters(keys.get(i), values.get(i));
        }
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __responsePayload = response.asString();
        if (response.getStatusCode() == 200) {
            Reporter.log("Total Records Matching ACCOUNT_CODE : " + jsonHelper.getInstanceCount(__responsePayload, "users", "ACCOUNT_CODE", ACCOUNT_CODE), true);
            ArrayList __userList = jsonHelper.getJsonArray(__responsePayload, "users");
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__responsePayload, "users", keys.get(i), values.get(i));
                if (__instanceCount == __userList.size()) {
                    Reporter.log("Key [" + keys.get(i) + "] total matching records = " + __instanceCount, true);
                    conditionsMatch++;
                } else
                    Reporter.log("Key [" + keys.get(i) + "] did not match the payload value", true);
            }
            if (conditionsMatch == keys.size())
                Assert.assertTrue(true);
            else
                Assert.assertTrue(false, " One/more filter value did not match with payload field ");

        } else if (response.getStatusCode() == 404) {
            Reporter.log("HTTP 404 - No User found matching criteria...!");
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + "Failed to get user details with search filter");
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"deleteUser", "User"}, dependsOnGroups = "postUser", priority = 4)
    public void testDELETEUser() throws Exception {
        TestCase testCase = deleteUser(ACCOUNT_CODE, USERNAME);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postUserReq", "User"})
    public void testPOSTUserUsingMandatoryFields() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        UserObj userObj = userObjList.get(0);
        userObj.setUsername(randomString(10) + "@email.com");
        TestCase testCase = postUser(ACCOUNT_CODE, null, userObj, expectedCondition, true);
        USERNAME_REQ = userObj.getUsername();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "updateUserDp1", parallel = false)
    public Object[][] userData() {
        Object[][] usersData = new Object[userObjList.size() - 2][1];
        int j = 0;
        try {
            for (int i = 2; i < userObjList.size(); i++) {
                userObjList.get(i).setUuid(null);
                userObjList.get(i).setUsername(null);
                usersData[j][0] = userObjList.get(i);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersData;
    }

    @org.testng.annotations.Test(dataProvider = "updateUserDp1", enabled = true, groups = {"updateUserReq", "User"}, dependsOnGroups = "postUserReq", priority = 3)
    public void testPOSTUpdateUserDetailsMulitple(UserObj userObj) throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        userObj.setUsername(USERNAME_REQ);
        TestCase testCase = postUser(ACCOUNT_CODE, USERNAME_REQ, userObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 403 && userObj.getUserTypeName().equals("System Master")) {
            Reporter.log("Cannot change user himself to 'System Master' got proper error message", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @org.testng.annotations.Test(enabled = true, groups = {"deleteUser", "User"}, priority = 5, dependsOnGroups = "updateUserReq")
    public void testDELETEUserReq() throws Exception {
        TestCase testCase = deleteUser(ACCOUNT_CODE, USERNAME_REQ);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    //USER COMMON METHODS

    public TestCase postUser(String accountCode, String username, UserObj userObj, ExpectedCondition expectedCondition, boolean requiredOnly) throws Exception {
        String url = urlPath + "/" + accountCode;

        if (username != null) {
            url += "/" + username;
            userObj.setUsername(null);
            userObj.setDateCreated(null);
            userObj.setDateModified(null);
            userObj.setUuid(null);
            Reporter.log("updating user details [" + userObj.getUsername() + "] for the account " + accountCode, true);
        } else
            Reporter.log("posting new user details [" + userObj.getUsername() + "] to the account " + accountCode, true);

        TestCase testCase = new TestCase();
        java.util.Map<String, Object> locationHashMap = testDataLoad.convertObjectToMap(userObj);
        locationHashMap.remove("uuid");
        java.util.Map<String, Object> ___jsonObject = new HashMap<>();

        if (requiredOnly) {
            for (String field : requiredFields) {
                if (locationHashMap.containsKey(field)) {
                    ___jsonObject.put(field, locationHashMap.get(field));
                }
            }
        } else ___jsonObject = locationHashMap;
        io.restassured.response.Response response = restAssuredClient.POST(gson.toJson(___jsonObject), url, authenticationScheme);
        testCase.setStatusCode(response.getStatusCode());
        testCase.setPayload(response.asString());

        if (response.getStatusCode() == expectedCondition.getStatusCode()) {

            if (expectedCondition.getStatusCode() == 200) {
                testCase.setDtoObject(userObj);
                userObj.setDateCreated(du.convertDateFromString(response.getBody().jsonPath().get("dateCreated").toString()));
                userObj.setDateModified(du.convertDateFromString(response.getBody().jsonPath().get("dateModified").toString()));
                if (jsonHelper.compareRequestWithResponsePayload(gson.toJson(___jsonObject), response.asString())) {
                    testCase.setMessage("User [" + userObj.getEmail() + "] added/updated successfully ");
                    testCase.setPassed(true);
                } else testCase.setMessage("User [" + userObj.getEmail() + "] posted but payload validation failed");
            } else {
                testCase.setMessage("user post/update was failed and got proper response as expected");
                testCase.setPassed(true);
            }
        } else {
            testCase.setMessage(response.getStatusLine() + " Failed to post/update user [" + userObj.getEmail() + "] to the account " + ACCOUNT_CODE);
            testCase.setPassed(false);
        }
        return testCase;
    }

    public TestCase deleteUser(String accountCode, String username) throws Exception {
        String url = urlPath + "/" + accountCode + "/" + username;
        Reporter.log("deleting user details [" + username + "] from the account " + accountCode);
        TestCase testCase = new TestCase();
        io.restassured.response.Response response = restAssuredClient.DELETE(url, authenticationScheme);
        testCase.setStatusCode(response.getStatusCode());

        if (response.getStatusCode() == 200) {
            if (response.getBody().jsonPath().get("username").equals(username)
                    && response.getBody().jsonPath().get("accountCode").equals(ACCOUNT_CODE)
                    && !(Boolean) response.getBody().jsonPath().get("isActive")) {
                testCase.setMessage("User details [" + username + "] deleted successfully ");
                testCase.setPassed(true);
            } else testCase.setMessage("user details user [" + username + "] deleted but assertion failed..");
        } else testCase.setMessage("Failed to delete user [" + username + "]");

        return testCase;
    }

//    public TestCase getUsers(String ACCOUNT_CODE, String username, String filter, String value, int MAX_RESULTS, int START_INDEX) throws Exception {
//
//        if (ACCOUNT_CODE == null)
//            throw new NullPointerException("ACCOUNT_CODE cannot be blank...you must specify");
//        String url = urlPath + "/" + ACCOUNT_CODE;
//
//        if (username != null) {
//            url += "/" + username;
//            Reporter.log("retrieving user details [" + username + "]", true);
//        } else {
//            Reporter.log("retrieving user details for the account " + ACCOUNT_CODE, true);
//            if (filter != null)
//                url += "?" + filter + "=" + value;
//
//            if (MAX_RESULTS > 0)
//                if (url.contains("?")) {
//                    url += "&max_results=" + MAX_RESULTS;
//                } else url += "?max_results=" + MAX_RESULTS;
//
//            if (START_INDEX > 0)
//                if (url.contains("?")) {
//                    url += "&start_index=" + START_INDEX;
//                } else url += "?start_index=" + START_INDEX;
//        }
//
//        TestCase testCase = new TestCase();
//        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
//        testCase.setStatusCode(response.getStatusCode());
//        testCase.setPayload(response.toString());
//
//        if(response.getStatusCode() == 200) {
//
//            if(username!=null){
//                if(response.getBody().jsonPath().get("ACCOUNT_CODE").equals(ACCOUNT_CODE)
//                        && response.getBody().jsonPath().get("username").equals(username)){
//                    Reporter.log("user ["+username+"] details retrieved successfully");
//                    testCase.setPassed(true);
//                }else {
//                    testCase.setMessage("user [" + username + "] details retrieved but response validation failed ");
//                    testCase.setPassed(false);
//                    return testCase;
//                }
//            }else {
//                List<HashMap<String,Object>> usersList = response.getBody().jsonPath().getList("users");
//                int acctMatchingCount = jsonHelper.getInstanceCount(response.asString(),"users", "ACCOUNT_CODE", ACCOUNT_CODE);
//                if (acctMatchingCount == usersList.size()) {
//                    Reporter.log("total [" + acctMatchingCount + "] users found for the [ACCOUNT_CODE=" + ACCOUNT_CODE + "]",true);
//                    testCase.setPassed(true);
//                }else  {
//                    testCase.setMessage("one or more records did not match the ACCOUNT_CODE = " + ACCOUNT_CODE);
//                    testCase.setPassed(false);
//                    return testCase;
//                }
//
//                if (filter != null) {
//                    int filterInstanceCount = jsonHelper.getInstanceCount(response.asString(),"users", filter, value);
//                    if (filterInstanceCount == usersList.size()) {
//                        Reporter.log("total [" + filterInstanceCount + "] users found for the search filter criteria ["+filter+"="+value+"]", true);
//                        testCase.setPassed(true);
//                    } else {
//                        testCase.setMessage("one or more records did not match search filter criteria ["+filter+"="+value+"]");
//                        testCase.setPassed(false);
//                        return testCase;
//                    }
//                }
//
//                if (MAX_RESULTS > 0) {
//                    if ((Integer) response.getBody().jsonPath().get("maxResults") == MAX_RESULTS && usersList.size() == MAX_RESULTS) {
//                        Reporter.log("users completeCollectionSize = " + jsonHelper.getJsonValueByKey(response.asString(), "completeCollectionSize"),true);
//                        Reporter.log("users list satisfied the max results criteria [max_results="+MAX_RESULTS+"]",true);
//                        testCase.setPassed(true);
//                    }else  {
//                        testCase.setMessage("users list did not satisfy the max results criteria [max_results="+ MAX_RESULTS+"]");
//                        testCase.setPassed(false);
//                        return testCase;
//                    }
//                }
//
//                if (START_INDEX > 0) {
//                    if ((Integer) jsonHelper.getJsonValueByKey(response.asString(),"startingIndex") == START_INDEX) {
//                        Reporter.log("users list satisfied the starting index criteria [start_index="+START_INDEX+"]",true);
//                        testCase.setPassed(true);
//                    }else  {
//                        testCase.setMessage("users list did not satisfy the starting index criteria [start_index=" +START_INDEX+"]");
//                        testCase.setPassed(false);
//                        return testCase;
//                    }
//                }
//            }
//        }else if(response.getStatusCode() == 404){
//            testCase.setMessage(response.getStatusLine()+" No Users found matching criteria");
//            testCase.setPassed(true);
//
//        }else {
//            testCase.setMessage(response.getStatusLine()+" failed to retrieve user information ");
//            testCase.setPassed(false);
//            return testCase;
//        }
//        return testCase;
//    }

    public TestCase getUsers(String accountCode, String userName) throws Exception {
        return getUsers(accountCode, userName, null, null, 0, 0);
    }

    public TestCase getUsers(String accountCode, int MAX_RESULTS, int START_INDEX) throws Exception {
        return getUsers(accountCode, userName, null, null, MAX_RESULTS, START_INDEX);
    }

    public TestCase getUsers(String accountCode, String orderBy, int MAX_RESULTS, int START_INDEX) throws Exception {
        return getUsers(accountCode, userName, null, orderBy, MAX_RESULTS, START_INDEX);
    }

    public TestCase getUsers(String accountCode, String filter, String value, int MAX_RESULTS, int START_INDEX) throws Exception {
        HashMap<String, Object> searchFilters = new HashMap<>();
        searchFilters.put(filter, value);
        return getUsers(accountCode, null, searchFilters, null, 0, 0);
    }

    public TestCase getUsers(String accountCode, HashMap<String, Object> searchFilters, int MAX_RESULTS, int START_INDEX) throws Exception {
        return getUsers(accountCode, null, searchFilters, null, 0, 0);
    }

    public TestCase getUsers(String accountCode, String userName, HashMap<String, Object> searchFilters, String orderBy, int MAX_RESULTS, int START_INDEX) throws Exception {

        if (accountCode == null)
            throw new NullPointerException("ACCOUNT_CODE cannot be blank...you must specify");

        String url = urlPath + "/" + accountCode;
        if (userName != null)
            url += "/" + userName;

        ArrayList<String> filters = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();

        if (searchFilters != null) {
            List<ArrayList> filtersList = testDataLoad.mapToArray(searchFilters);
            filters = filtersList.get(0);
            values = filtersList.get(1);
            url += "?" + filters.get(0) + "=" + values.get(0);

            if (filters.size() > 1) {
                for (int j = 0; j < filters.size(); j++) {
                    url += "&" + filters.get(j) + "=" + values.get(j);
                }
            }
        }

        if (orderBy != null) {
            if (url.contains("?")) {
                url += "&order_by" + orderBy;
            } else url += "?order_by=" + orderBy;
        }

        if (MAX_RESULTS > 0) {
            if (url.contains("?")) {
                url += "&max_results=" + MAX_RESULTS;
            } else url += "?max_results=" + MAX_RESULTS;
        }

        if (START_INDEX > 0) {
            if (url.contains("?")) {
                url += "&start_index=" + START_INDEX;
            } else url += "?start_index=" + START_INDEX;
        }

        TestCase testCase = new TestCase();
        io.restassured.response.Response response = restAssuredClient.GET(url, authenticationScheme);
        testCase.setStatusCode(response.getStatusCode());
        testCase.setPayload(response.toString());

        if (response.getStatusCode() == 200) {

            if (userName != null) {
                if (response.getBody().jsonPath().get("accountCode").equals(accountCode)
                        && response.getBody().jsonPath().get("username").equals(userName)) {
                    Reporter.log("user [" + userName + "] details retrieved successfully");
                    testCase.setPassed(true);
                } else {
                    testCase.setMessage("user [" + userName + "] details retrieved but response validation failed ");
                    testCase.setPassed(false);
                    return testCase;
                }
            } else {
                List<HashMap<String, Object>> usersList = response.getBody().jsonPath().getList("users");
                int acctMatchingCount = jsonHelper.getInstanceCount(response.asString(), "users", "accountCode", accountCode);

                if (acctMatchingCount == usersList.size()) {
                    Reporter.log("total [" + acctMatchingCount + "] users found for the [ACCOUNT_CODE=" + accountCode + "]", true);
                    testCase.setPassed(true);
                } else {
                    testCase.setMessage("one or more records did not match the ACCOUNT_CODE = " + accountCode);
                    testCase.setPassed(false);
                    return testCase;
                }

                if (searchFilters != null) {
                    int didNotMatch = 0;

                    for (int i = 0; i < filters.size(); i++) {

                        int filterMatchCount = jsonHelper.getInstanceCount(response.asString(), "users", filters.get(i), values.get(i));
                        if (filterMatchCount == usersList.size()) {
                            Reporter.log("FIELD [" + filters.get(i) + "] match count [" + filterMatchCount + "]", true);
                        } else {
                            Reporter.log("FIELD [" + filters.get(i) + "] did not match ", true);
                            didNotMatch++;
                        }

                    }
                    if (didNotMatch > 0) {
                        testCase.setMessage("One or more search filter criteria did not match with the response");
                        testCase.setPassed(false);
                        return testCase;
                    } else {
                        Reporter.log("get users with search filters passed", true);
                        testCase.setPassed(true);
                    }

                }

                if (orderBy != null) {
                    if (jsonHelper.isResultsSorted(usersList, orderBy)) {
                        Reporter.log("get Location with orderBy [" + orderBy + "] successful", true);
                        testCase.setPassed(true);
                    } else {
                        testCase.setMessage("[AssertFailed] get Location with orderBy [" + orderBy + "] did not return in order");
                        testCase.setPassed(false);
                        return testCase;
                    }
                }

                if (MAX_RESULTS > 0) {
                    if ((Integer) response.getBody().jsonPath().get("maxResults") == MAX_RESULTS && usersList.size() <= MAX_RESULTS) {
                        Reporter.log("users completeCollectionSize = " + jsonHelper.getJsonValueByKey(response.asString(), "completeCollectionSize"), true);
                        Reporter.log("users list satisfied the max results criteria [max_results=" + MAX_RESULTS + "]", true);
                        testCase.setPassed(true);
                    } else {
                        testCase.setMessage("users list did not satisfy the max results criteria [max_results=" + MAX_RESULTS + "]");
                        testCase.setPassed(false);
                        return testCase;
                    }
                }

                if (START_INDEX > 0) {
                    if ((Integer) jsonHelper.getJsonValueByKey(response.asString(), "startingIndex") == START_INDEX) {
                        Reporter.log("users list satisfied the starting index criteria [start_index=" + START_INDEX + "]", true);
                        testCase.setPassed(true);
                    } else {
                        testCase.setMessage("users list did not satisfy the starting index criteria [start_index=" + START_INDEX + "]");
                        testCase.setPassed(false);
                        return testCase;
                    }
                }

            }
        } else if (response.getStatusCode() == 404) {
            testCase.setMessage(response.getStatusLine() + " No Users found matching criteria");
            testCase.setPassed(true);

        } else {
            testCase.setMessage(response.getStatusLine() + " failed to retrieve user information ");
            testCase.setPassed(false);
            return testCase;
        }
        return testCase;
    }


}