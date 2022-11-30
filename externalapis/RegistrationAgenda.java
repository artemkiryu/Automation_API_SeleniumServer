package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.RequestSpecDataType;
import internal.qaauto.certain.platform.pojo.RequestSpecification;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;


@SuppressWarnings("all")
public class RegistrationAgenda extends CertainAPIBase {

    private final String businessObject = "RegistrationAgenda";
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private AuthenticationScheme auth = new AuthenticationScheme();
    private List<com.certain.external.dto.registrationagenda.RegistrationAgenda> registrationAgendaList;
    private boolean isPassed = false;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        auth.setUsername(USERNAME);
        auth.setPassword(PASSWORD);
        try {
            registrationAgendaList = testDataLoad.getRegistrationAgendaObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        this.auth = null;
    }

    @Test(enabled = true, description = "testAddNewRegistrationAgendaItem", groups = {"post-registrationAgenda", "RegistrationAgenda"},
            dependsOnGroups = {"post-event-for-reg", "post-attendee-for-reg", "post-registration-obj", "post-agenda-for-reg"})
    public void testPOST_RegistrationAgendaItemSCM32986() throws Exception {
        Reporter.log("Posting Agenda Item to the Registration Code  " + REGISTRATION_CODE);
        registrationAgendaList.get(0).setActivityCode(ACTIVITY_CODE);
        registrationAgendaList.get(0).setRegistrationCode(REGISTRATION_CODE);
        registrationAgendaList.get(0).setEventCode(REG_EVENT_CODE);
        String __jsonRequestMessage = super.gson.toJson(registrationAgendaList.get(0));
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.setRequestBody(__jsonRequestMessage);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.POST(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __actualRegCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registrationCode");
            registrationAgendaList.get(0).setDateCreated(testDataLoad.toDate((String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "dateCreated")));
//                registrationAgendaList.get(0).setDateModified((String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "dateModified"));
            registrationAgendaList.get(0).setStartDate(testDataLoad.toDate((String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "startDate")));
            registrationAgendaList.get(0).setEndDate(testDataLoad.toDate((String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "endDate")));
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage) && __actualRegCode.equals(REGISTRATION_CODE)) {
                testGET_RegistrationsAgendaByRegCode();
                if (isPassed)
                    Reporter.log("Registration Agenda Added to Registration Code" + REGISTRATION_CODE);
                Assert.assertTrue(true);
            } else {
                Reporter.log("Post RegistrationAgenda was successful but Assertion Failed ");
                Assert.assertTrue(false, "Status OK but response validation failed..please verify the response");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Assign Registration Agenda to the RegistrationCode = " + REGISTRATION_CODE);
        }
        response = super.restAssuredClient.POST(requestSpec);
        if (response.getStatusCode() == 400) {
            __jsonResponseMessage = response.asString();
            if (__jsonResponseMessage.contains("HTTP Status 400 -  Specified AgendaItem is already associated with current registration, if you want to update Registration Agenda details please use the Update api.")) {
                Reporter.log("Not able to assign same agenda twice" + REGISTRATION_CODE);
                Assert.assertTrue(true);
            } else {
                Reporter.log("Able to assign same agenda twice" + REGISTRATION_CODE);
                Assert.assertTrue(false);
            }
        }

    }

    @DataProvider(name = "get-regAgenda", parallel = false)
    public Object[][] getRegAgendaFilters() throws Exception {
        return testDataLoad.getKeyValuePairFromObject(testDataLoad.getRegistrationAgendaObjFilters(registrationAgendaList.get(0)));
    }

    @Test(dataProvider = "get-regAgenda", enabled = true, description = "get registration agendsa details by search filters", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationAgendasByRegCodeWithFilters(String key, String value) throws Exception {
        Reporter.log("Getting RegistrationAgenda list by Registration Code " + REGISTRATION_CODE + " with search filter = " + key + "=" + value);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.addQueryParameters(key, value);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __profileList = jsonHelper.getJsonArray(__jsonResponseMessage, "registrationAgendas");
            int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", key, value);
            if (__profileList.size() == __instanceCount) {
                Reporter.log("Successfully Retrieved RegistrationAgendas by Search QueryBuilder [" + key);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "RegistrationAgendas Retrieved But Assertion Failed");

        } else if (response.getStatusCode() == 404) {
            Reporter.log("No Registration Agendas Found Matching Criteria " + key + "=" + value);
            Assert.assertTrue(true);

        } else {
            Assert.assertTrue(false, response.getStatusLine() + "Failed to Get RegistrationAgendas With Search QueryBuilder [" + key + "=" + value + " ]");
        }

    }

    @Test(dataProvider = "get-regAgenda", enabled = true, description = "get registration agendsa details by search filters", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationAgendasByEventCodeWithFilters(String key, String value) throws Exception {
        Reporter.log("Getting RegistrationAgendas By Event Code  " + REG_EVENT_CODE + " With Search QueryBuilder = " + key + "=" + value);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.addQueryParameters(key, value);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __regList = jsonHelper.getJsonArray(__jsonResponseMessage, "registrationAgendas");
            int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", key, value);
            if (__regList.size() == __instanceCount) {
                Reporter.log("RegistrationAgendas Retrieved For Event Code With Search QueryBuilder [" + key + "=" + value + " ]");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "RegistrationAgendas Retrieved For Event Code With Search QueryBuilder [" + key + "=" + value + " ] but Assertion Failed");

        } else if (response.getStatusCode() == 404) {
            Reporter.log("No Registrations Agenda Items Found Matching Criteria " + key + "=" + value);
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Get RegistrationAgenda Details with Search QueryBuilder [" + key + "=" + value + " ]");
        }

    }

    @DataProvider(name = "get-regAgenda-multi-filters", parallel = false)
    public Object[][] getEventsMultipleFilters() throws Exception {
        Object obj = testDataLoad.getRegistrationAgendaObjFilters(registrationAgendaList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-regAgenda-multi-filters", enabled = true, description = "get registration agendsa details by search filters", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationAgendasByEventCodeWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Getting RegistrationAgendas By Event Code  " + REG_EVENT_CODE + " With Search QueryBuilder");
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        List<ArrayList> mapToArray = testDataLoad.mapToArray(multipleFilters);
        ArrayList<String> keys = mapToArray.get(0);
        ArrayList values = mapToArray.get(1);
        for (int i = 0; i < keys.size(); i++) {
            request.addQueryParameters(keys.get(i), values.get(i));
        }
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __regAgendaList = jsonHelper.getJsonArray(__jsonResponseMessage, "registrationAgendas");
            Reporter.log("Total Records Matching ACCOUNT_CODE : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "ACCOUNT_CODE", ACCOUNT_CODE), true);
            Reporter.log("Total Records Matching eventCode   : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "eventCode", REG_EVENT_CODE), true);
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", keys.get(i), values.get(i));
                if (__regAgendaList.size() == __instanceCount) {
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
            Reporter.log("HTTP 404 - No Registration Agenda found matching criteria...!");
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Get RegistrationAgenda Details with multi Search QueryBuilder");
        }

    }

    @Test(dataProvider = "get-regAgenda-multi-filters", enabled = true, description = "get registration agendsa details by search filters", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationAgendasByRegCodeWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Getting RegistrationAgendas By registration code  " + REGISTRATION_CODE + " With multiple Search Filters");
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        List<ArrayList> mapToArray = testDataLoad.mapToArray(multipleFilters);
        ArrayList<String> keys = mapToArray.get(0);
        ArrayList values = mapToArray.get(1);
        for (int i = 0; i < keys.size(); i++) {
            request.addQueryParameters(keys.get(i), values.get(i));
        }

        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __regAgendaList = jsonHelper.getJsonArray(__jsonResponseMessage, "registrationAgendas");
            Reporter.log("Total Records Matching ACCOUNT_CODE     : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "ACCOUNT_CODE", ACCOUNT_CODE), true);
            Reporter.log("Total Records Matching eventCode       : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "eventCode", REG_EVENT_CODE), true);
            Reporter.log("Total Records Matching registrationCode: " + jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "registrationCode", REGISTRATION_CODE), true);
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", keys.get(i), values.get(i));
                if (__regAgendaList.size() == __instanceCount) {
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
            Reporter.log("HTTP 404 - No Registration Agenda found matching criteria...!");
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Get RegistrationAgenda details for the registration code  " + REGISTRATION_CODE + " With multiple Search Filters");
        }

    }

    @Test(enabled = true, description = "testGetRegistrationsAgendaByRegCode", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationsAgendaByRegCode() throws Exception {
        Reporter.log("Getting Registration Agenda Items by the Registration code = " + REGISTRATION_CODE);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            if (__jsonResponseMessage.contains(ACCOUNT_CODE) && __jsonResponseMessage.contains(REG_EVENT_CODE) && __jsonResponseMessage.contains(REGISTRATION_CODE)) {
                Reporter.log("Successfully Get Registration Agenda Items  for the Registration Code = " + REGISTRATION_CODE);
                isPassed = true;
                Assert.assertTrue(true);
            } else {
                Reporter.log("Get RegistrationAgenda Was Success but Assertion Failed");
                Assert.assertTrue(false);
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to get RegistrationAgenda for the Registration Code [" + REGISTRATION_CODE + " ]");
        }
    }

    @Test(enabled = true, description = "testGetRegistrationsAgendaByEventCode", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationsAgendaByEventCode() throws Exception {
        Reporter.log("Getting Registration Agenda Items by the Event Code   " + REG_EVENT_CODE);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __registrationAgendaList = jsonHelper.getJsonArray(__jsonResponseMessage, "registrationAgendas");
            int __matchCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "registrationAgendas", "eventCode", REG_EVENT_CODE);

            if (__registrationAgendaList.size() == __matchCount) {
                Reporter.log("Successfully Retrieved Registration Agenda Items  for the Event Code " + REG_EVENT_CODE);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Response Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Get RegistrationAgenda for the Event Code [" + REG_EVENT_CODE + " ]");
        }

    }

    @Test(enabled = true, description = "testGetRegistrationsAgendaByActivityCode", groups = {"get-registrationAgenda", "RegistrationAgenda"},
            priority = 2, dependsOnGroups = "post-registrationAgenda")
    public void testGET_RegistrationsAgendaByActivityCode() throws Exception {
        Reporter.log("Getting Registration Agenda Items by the activityCode =  " + ACTIVITY_CODE);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.setActivityCode(ACTIVITY_CODE);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __actualRegCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registrationCode");
            String __actualActivityCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "activityCode");
            String __actualEventCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "eventCode");
            if (__actualEventCode.equals(REG_EVENT_CODE) && __actualRegCode.equals(REGISTRATION_CODE) && __actualActivityCode.equals(ACTIVITY_CODE)) {
                Reporter.log("Successfully Get Registration Agenda Items  for the activityCode = " + ACTIVITY_CODE);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Response Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to get RegistrationAgenda Details for the activityCode [" + ACTIVITY_CODE + " ]");
        }

    }

    @Test(enabled = true, description = "testUpdateRegistrationAgendaItem", groups = {"update-registrationAgenda", "RegistrationAgenda"},
            priority = 3, dependsOnGroups = "post-registrationAgenda")
    public void testPOST_UpdateRegistrationAgendaItem() throws Exception {
        Reporter.log("Updating Registration Agenda Item  " + ACTIVITY_CODE);
        registrationAgendaList.get(1).setActivityCode(ACTIVITY_CODE);
        registrationAgendaList.get(1).setEventCode(REG_EVENT_CODE);
        registrationAgendaList.get(1).setRegisteredQuantity(2);
        registrationAgendaList.get(1).setAvailableQuantity(1);
        String __jsonRequestMessage = super.gson.toJson(registrationAgendaList.get(1));
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.setActivityCode(ACTIVITY_CODE);
        request.setRequestBody(__jsonRequestMessage);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.POST(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __actualActivityCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "activityCode");
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage) && __actualActivityCode.equals(ACTIVITY_CODE)) {
                Reporter.log("Registration Agenda Updated Successfully " + ACTIVITY_CODE);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Status OK but Response Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Update RegistrationAgenda [" + ACTIVITY_CODE + " ]");
        }
    }


    @Test(enabled = true, description = "testUpdateRegistrationAgendaItem", groups = {"update-registrationAgenda-cancel", "RegistrationAgenda"},
            priority = 3, dependsOnGroups = "post-registrationAgenda")
    public void testPOST_UpdateRegistrationAgendaItemStatusAndQty() throws Exception {
        Reporter.log("Updating Registration Agenda Item  " + ACTIVITY_CODE);
        registrationAgendaList.get(1).setActivityCode(ACTIVITY_CODE);
//        registrationAgendaList.get(1).setEventCode(REG_EVENT_CODE);
        registrationAgendaList.get(1).setRegisteredQuantity(1);
        registrationAgendaList.get(1).setAvailableQuantity(1);
        registrationAgendaList.get(1).setStatus("ATTENDED");
        String __jsonRequestMessage = super.gson.toJson(registrationAgendaList.get(1));
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.setActivityCode(ACTIVITY_CODE);
        request.setRequestBody(__jsonRequestMessage);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.POST(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __actualActivityCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "activityCode");
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage) && __actualActivityCode.equals(ACTIVITY_CODE)) {
                Reporter.log("Registration Agenda Updated Successfully " + ACTIVITY_CODE);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Status OK but Response Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Update RegistrationAgenda [" + ACTIVITY_CODE + " ]");
        }
    }


    @Test(enabled = true, description = "testDeleteRegistrationsAgendaItem", groups = {"delete-registrationAgenda", "RegistrationAgenda"},
            priority = 5, dependsOnGroups = "post-registrationAgenda")
    public void testDELETE_RegistrationAgendaItem() throws Exception {
        Reporter.log("Deleting Registration Agenda Item " + ACTIVITY_CODE + " From Registration Code " + REGISTRATION_CODE);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(ACCOUNT_CODE);
        request.setEventCode(REG_EVENT_CODE);
        request.setRegistrationCode(REGISTRATION_CODE);
        request.setActivityCode(ACTIVITY_CODE);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.DELETE(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __actualRegCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registrationCode");
            String __actualActivityCode = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "activityCode");
            String status = (String) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "status");
            if (__actualRegCode.equals(REGISTRATION_CODE) && __actualActivityCode.equals(ACTIVITY_CODE) && status.equals("CANCELLED")) {
                Reporter.log("Successfully Deleted Agenda Items  " + ACTIVITY_CODE + " From the Registration " + REGISTRATION_CODE);
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false, "Response Assertion Failed");
            }
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to Delete Agenda Items  " + ACTIVITY_CODE + " From the Registration " + REGISTRATION_CODE);
        }

    }
}
