package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.RequestSpecDataType;
import internal.qaauto.certain.platform.pojo.RequestSpecification;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;


@SuppressWarnings("all")
public class AccommodationReservation extends CertainAPIBase {

    private final String businessObject = "Accommodation";
    private final boolean isPassed = false;
    private final AuthenticationScheme auth = new AuthenticationScheme();
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private int hotelRegistrationId;
    private List<com.certain.external.dto.accommodationReservation.AccommodationReservation> accommodationReservationsList = null;
    private String registrationCode;
    private String accountCode;
    private String eventCode;
    public String roomCode;
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        roomCode = ROOM_CODE;
        auth.setUsername(USERNAME);
        auth.setPassword(PASSWORD);
        try {
            String name = "REG4ACMRES_API";
            registrationCode = createRegistration(eventCode, name, name + "@gmail.com", "Microsoft", "Attendee");
            Reporter.log("-------------------" + registrationCode, true);
            Thread.sleep(5000);
            accommodationReservationsList = testDataLoad.getAccommodationReservationObjData();
            accommodationReservationsList.get(0).setEventCode(eventCode);
            accommodationReservationsList.get(0).setRegistrationCode(registrationCode);
            accommodationReservationsList.get(0).setRoomCode(roomCode);
            accommodationReservationsList.get(0).setHotelCode(SUPPLIER_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(description = "POST new AccommodationReservation", groups = {"post-accommodation-reservation", "AccommodationReservation"}, dependsOnGroups = "post-accommodation-block")
    public void testPOST_AccommodationReservation() throws Exception {
        Reporter.log("Creating new AccommodationReservationObj for the registration: " + registrationCode);
        Map<String, Object> objectMap = testDataLoad.convertObjectToMap(accommodationReservationsList.get(0));
        String arrivalDate = du.dayAfter(du.SHORTDATE_ZERO_HOURS, 2);
        String departDate = du.dayAfter(du.SHORTDATE_ZERO_HOURS, 4);
        String dateConfirmed = du.today(du.SHORTDATE_ZERO_HOURS);
        objectMap.put("arrivalDate", arrivalDate);
        objectMap.put("departDate", departDate);
        objectMap.put("dateConfirmed", dateConfirmed);
        accommodationReservationsList.get(0).setArrivalDate(du.convertDateFromString(arrivalDate));
        accommodationReservationsList.get(0).setDepartDate(du.convertDateFromString(departDate));
        accommodationReservationsList.get(0).setRoomCode(roomCode);
        String __jsonRequestMessage = super.gson.toJson(objectMap);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
        request.setRegistrationCode(registrationCode);
        request.setRequestBody(__jsonRequestMessage);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.POST(requestSpec);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            hotelRegistrationId = (int) jsonHelper.getJsonValueByKey(__jsonResponseMessage, "registrationHotelId");
            accommodationReservationsList.get(0).setDateCreated(du.convertDateFromString(jsonHelper.getJsonValueByKey(__jsonResponseMessage, "dateCreated").toString()));
            accommodationReservationsList.get(0).setDateCreated(du.convertDateFromString(jsonHelper.getJsonValueByKey(__jsonResponseMessage, "dateModified").toString()));

            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage)) {
                Reporter.log("AccommodationReservationObj added successfully for the registration code = " + registrationCode);
                testGET_AccommodationReservationsByRegistrationCode();
                if (isPassed)
                    Assert.assertTrue(true, "AccommodationReservationObj added successfully for the registration code = " + registrationCode);
            } else {
                Reporter.log("AccommodationReservationObj added successfully but validation failed...please check the response");
                Assert.assertTrue(false, "AccommodationReservationObj added successfully but validation failed...please check the response");
            }
        } else {
            String ErrorMsg = " Failed to post accommodation reservation to the registration code " + registrationCode;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }
    }

    @Test(description = "get accommodation reservations by ACCOUNT_CODE", groups = {"get-accommodation-reservation", "AccommodationReservation"},
            priority = 2, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByAccountCode() throws Exception {
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);

        //build and send request spec
        Reporter.log("Fetching Accommodation Reservations by the account code = " + accountCode);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();

        //validate response and assert
        if (response.getStatusCode() == 200) {
            ArrayList accommodationReservationsList = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            int __count = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "accountCode", accountCode);

            if (accommodationReservationsList.size() == __count) {
                Reporter.log("Successfully fetched AccommodationReservation by the account code = " + accountCode);
                Assert.assertTrue(true, "Successfully fetched AccommodationReservation by the account code = " + accountCode);
            } else {
                Reporter.log("Status OK but response validation failed, please check the response");
                Assert.assertTrue(false, "Status OK but response validation failed, please check the response");
            }

        } else {
            String ErrorMsg = " Failed to get accommodation reservations by account " + accountCode +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @DataProvider(name = "get-accommodationRes", parallel = false)
    public Object[][] getAccommodationRes() throws Exception {
        Object obj = testDataLoad.getAccommodationReservationObjFilters(accommodationReservationsList.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-accommodationRes", description = "get AccommodationReservations by ACCOUNT_CODE with filters", groups = {"get-accommodation-reservation", "AccommodationReservation"},
            priority = 3, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByAccountCodeWithFilters(String key, String value) throws Exception {

        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.addQueryParameters(key, value);

        //build and send request spec
        Reporter.log("Fetching Accommodation Reservations by the ACCOUNT_CODE = " + accountCode + " with filter " + key + "=" + value);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            ArrayList __accommodationCollection = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            int __instanceCountAct = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "accountCode", accountCode);
            int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", key, value);
            Reporter.log("Total collection size=" + __accommodationCollection.size() + " matching records=" + __instanceCount);

            if (__accommodationCollection.size() == __instanceCount && __accommodationCollection.size() == __instanceCountAct) {
                Reporter.log("Successfully fetched Accommodation Reservations by the ACCOUNT_CODE = " + accountCode + " with filter " + key + "=" + value);
                Assert.assertTrue(true, "Successfully fetched Accommodation Reservations by the ACCOUNT_CODE = " + accountCode + " with filter " + key + "=" + value);
            } else {
                Reporter.log("Collection Size: " + __accommodationCollection.size() + " Matching Records: " + __instanceCount + " acc: " + __instanceCountAct);
                Assert.assertTrue(false, "Status OK but response validation failed, please check the response " +
                        "\nUrl :" + requestSpec.url + "\nThere are only " + __instanceCount + " matching records of " + __accommodationCollection.size());
            }
        } else {
            String ErrorMsg = "Failed to retrieve Accommodation Reservations by account code " + accountCode + " with search filter " + key + "=" + value +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @Test(dataProvider = "get-accommodationRes", description = "get AccommodationReservations by  eventCode with filters", groups = {"get-accommodation-reservation", "Accommodations"},
            priority = 4, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByEventCodeWithFilters(String key, String value) throws Exception {
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
        request.addQueryParameters(key, value);

        //build and send request spec
        Reporter.log("Fetching Accommodation Reservations by the eventCode = " + eventCode + " with filter " + key + "=" + value);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            ArrayList __accommodationCollection = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            int __instanceCountEvt = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "eventCode", eventCode);
            int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", key, value);
            Reporter.log("Total collection size=" + __accommodationCollection.size() + " matching records=" + __instanceCount);

            if (__accommodationCollection.size() == __instanceCount && __accommodationCollection.size() == __instanceCountEvt) {
                Reporter.log("Successfully fetched Accommodation Reservations by the eventCode = " + eventCode + " with filter " + key + "=" + value);
                Assert.assertTrue(true, "Successfully fetched Accommodation Reservations by the eventCode = " + eventCode + " with filter " + key + "=" + value);
            } else {
                Reporter.log("Collection Size: " + __accommodationCollection.size() + " Matching Records: " + __instanceCount);
                Assert.assertTrue(false, "Status OK but response validation failed, please check the response \nURL: " + requestSpec.url);

            }
        } else {
            String ErrorMsg = " Failed to retrieve Accommodation Reservations by event code " + eventCode + " with search filter " + key + "=" + value +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @DataProvider(name = "get-accommodation-reservations-multi-filters")
    public Object[][] getAccommodationResMultipleFilters() throws Exception {
        Object obj = testDataLoad.getAccommodationReservationObjFilters(accommodationReservationsList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-accommodation-reservations-multi-filters", description = "get AccommodationReservations by  eventCode with filters", groups = {"get-accommodation-reservation", "Accommodations"},
            priority = 5, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByEventCodeWithMultipleFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Getting accommodation reservations list for the event " + eventCode + " with multiple search filter");
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
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
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            ArrayList __accommodationReservationList = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            Reporter.log("Total Records Matching eventCode : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "eventCode", eventCode), true);
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", keys.get(i), values.get(i));
                if (__accommodationReservationList.size() == __instanceCount) {
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
            Reporter.log("HTTP 404 - No Accommodation Reservations found matching criteria...!");
            Assert.assertTrue(true);

        } else {
            String ErrorMsg = " Failed to retrieve Accommodation Reservations by event code " + eventCode + " with search filter";
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @Test(dataProvider = "get-accommodation-reservations-multi-filters", description = "get AccommodationReservations by  eventCode with filters", groups = {"get-accommodation-reservation", "Accommodations"},
            priority = 5, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByAccountCodeWithMultipleFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Getting accommodation reservations list for the account " + accountCode + " with multiple search filter");
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
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
        int statusCode = response.getStatusCode();
        //validate response and assert
        if (statusCode == 200) {
            ArrayList __accommodationReservationList = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            Reporter.log("Total Records Matching eventCode : " + jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "ACCOUNT_CODE", accountCode), true);
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", keys.get(i), values.get(i));
                if (__accommodationReservationList.size() == __instanceCount) {
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
            Reporter.log("HTTP 404 - No Accommodation Reservations found matching criteria...!");
            Assert.assertTrue(true);

        } else {
            String ErrorMsg = " Failed to retrieve Accommodation Reservations by event code " + eventCode + " with search filter";
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @Test(description = "get AccommodationReservations by eventCode", groups = {"get-accommodation-reservation", "AccommodationReservation"},
            priority = 2, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByEventCode() throws Exception {
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);

        //build and send request spec
        Reporter.log("Fetching Accommodation Reservations by the event code =  " + eventCode);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            ArrayList accommodationReservationsList = jsonHelper.getJsonArray(__jsonResponseMessage, "accommodationReservations");
            int __count = jsonHelper.getInstanceCount(__jsonResponseMessage, "accommodationReservations", "eventCode", eventCode);

            if (accommodationReservationsList.size() == __count) {
                Reporter.log("Successfully fetched Accommodations for the event code = " + eventCode);
                Assert.assertTrue(true, "Successfully fetched Accommodations for the event code = " + eventCode);
            } else {
                Reporter.log("Status OK but response validation failed, please check the response");
                Assert.assertTrue(false, "Status OK but response validation failed, please check the response");
            }
        } else {
            String ErrorMsg = " Failed to get accommodation reservations by event " + eventCode +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @Test(description = "get AccommodationReservations by registrationCode", groups = {"get-accommodation-reservation", "AccommodationReservation"},
            priority = 2, dependsOnGroups = "post-accommodation-reservation")
    public void testGET_AccommodationReservationsByRegistrationCode() throws Exception {
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
        request.setRegistrationCode(registrationCode);

        //build and send request spec
        Reporter.log("Fetching Accommodation Reservations by the registration code = " + registrationCode);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.GET(requestSpec);
        String __jsonResponseMessage = response.asString();
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            String __actualAccountCode = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "accommodationReservations", "accountCode");
            String __actualEventCode = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "accommodationReservations", "eventCode");
            String __actualRegCode = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "accommodationReservations", "registrationCode");

            if (__actualAccountCode.equals(accountCode) && __actualEventCode.equals(eventCode) && __actualRegCode.equals(registrationCode)) {
                Reporter.log("Successfully fetched Accommodations for the registration code = " + registrationCode);
                Assert.assertTrue(true, "Successfully fetched Accommodations for the registration code = " + registrationCode);
            } else {
                Reporter.log("Status OK but response validation failed, please check the response");
                Assert.assertTrue(false, "Status OK but response validation failed, please check the response");
            }
        } else {
            String ErrorMsg = " Failed to get accommodation reservations by registration code " + registrationCode +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }

    @Test(description = "update AccommodationReservation", groups = {"update-accommodation-reservation", "AccommodationReservation"},
            priority = 6, dependsOnGroups = "post-accommodation-reservation")
    public void testPOST_UpdateAccommodationReservation() throws Exception {
        Map<String, Object> objectMap = testDataLoad.convertObjectToMap(accommodationReservationsList.get(1));
        objectMap.put("registrationCode", registrationCode);
        objectMap.put("eventCode", eventCode);
        objectMap.put("roomCode", ROOM_CODE);
        objectMap.put("hotelCode", SUPPLIER_CODE);
        objectMap.put("arrivalDate", du.setForwardDate(new Date(), 2, "yyyy-MM-dd'T'00:00:00"));
        objectMap.put("departDate", du.setForwardDate(new Date(), 3, "yyyy-MM-dd'T'00:00:00"));
        objectMap.put("dateConfirmed", du.today("yyyy-MM-dd'T'00:00:00"));
        accommodationReservationsList.get(0).setRoomCode(roomCode);
        String __jsonRequestMessage = gson.toJson(objectMap);
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(SERVER_HOST);
        request.setBasePath(BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
        request.setRegistrationCode(registrationCode);
        request.setRegistrationHotelId(Integer.toString(this.hotelRegistrationId));
        request.setRequestBody(__jsonRequestMessage);
        Reporter.log("updating AccommodationReservationObj: " + __jsonRequestMessage);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.POST(requestSpec);
        String __jsonResponseMessage = response.asString();

        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMessage, __jsonResponseMessage)) {
                Reporter.log("AccommodationReservationObj revised successfully for the registration code = " + registrationCode);
                Assert.assertTrue(true);
            } else {
                Reporter.log("AccommodationReservationObj revised successfully but validation failed...please check the response");
                Assert.assertTrue(false, "AccommodationReservationObj revised successfully but validation failed...please check the response");
            }
        } else {
            String ErrorMsg = " Failed to update accommodation reservations registration Hotel id " + this.hotelRegistrationId +
                    "\nUrl    : " + requestSpec.url +
                    "\nPayload: " + __jsonRequestMessage +
                    "\nActual : " + __jsonResponseMessage;
            Assert.assertTrue(false, "FAIL:  " + response.getStatusLine() + ErrorMsg);
        }
    }

    @Test(description = "delete AccommodationReservation", groups = {"delete-accommodation-reservation", "AccommodationReservation"},
            priority = 7, dependsOnGroups = "post-accommodation-reservation")
    public void testDELETE_AccommodationReservation() throws Exception {
        //create WS request specification
        RequestSpecDataType request = new RequestSpecDataType();
        request.setBaseUri(super.SERVER_HOST);
        request.setBasePath(super.BASE_PATH);
        request.setAuthenticationScheme(auth);
        request.setContentType(super.DEFAULT_CONTENT_TYPE);
        request.setCollectionResource(businessObject);
        request.setAccountCode(accountCode);
        request.setEventCode(eventCode);
        request.setRegistrationCode(registrationCode);
        request.setRegistrationHotelId(Integer.toString(this.hotelRegistrationId));

        //build and send request spec
        Reporter.log("deleting Accommodation Reservations by the registration code = " + registrationCode);
        RequestSpecification requestSpec = new RequestSpecification(request);
        requestSpec.buildV1();
        Response response = super.restAssuredClient.DELETE(requestSpec);
        String __jsonResponseMessage = response.asString();
        int statusCode = response.getStatusCode();

        //validate response and assert
        if (statusCode == 200) {
            Reporter.log("Successfully deleting Accommodations for the registration code = " + registrationCode);
            Assert.assertTrue(true);
        } else {
            String ErrorMsg = " Failed to delete accommodation reservations registration hotel id " + this.hotelRegistrationId +
                    "\nUrl   : " + requestSpec.url +
                    "\nActual: " + __jsonResponseMessage;
            Assert.assertTrue(false, response.getStatusLine() + ErrorMsg);
        }

    }
}
