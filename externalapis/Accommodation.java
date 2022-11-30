package com.certain.External.service.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.AuthenticationScheme;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import internal.qaauto.certain.platform.pojo.Accommodation.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.util.*;

@SuppressWarnings("all")
public class Accommodation extends CertainAPIBase {

    public static String __requestJSON;
    static String __responsePayloadJSON;
    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final List<RoomNights> roomNights = new ArrayList<>();
    private final RoomNights roomNight = new RoomNights();
    public String roomCode;
    private AuthenticationScheme auth = new AuthenticationScheme(USERNAME, PASSWORD);
    private List<AccommodationsObj> accommodationsObjectList = new ArrayList<>();
    private String eventCode;
    private String accountCode;
    private String eventId;
    private String hotelCode;

    public String getLocationId(String accountCode, String eventCode, String hotelCode) {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + eventCode + "/blocks";
        String locationId = null;
        Response response = restAssuredClient.GET(urlPath, auth);
        if (response.getStatusCode() == 200) {
            List<HashMap<String, Object>> data = response.getBody().jsonPath().getList("data");
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get("hotelName").toString().equalsIgnoreCase(hotelCode)) {
                    locationId = data.get(i).get("locationId").toString();
                }
            }
        } else Reporter.log("No hotels found for the event code " + eventCode);
        return locationId;
    }


    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        hotelCode = SUPPLIER_CODE;
        roomCode = ROOM_CODE;
        eventId = USER_EVENT_ID;
        try {
            BLOCK_LOCATION_ID = getLocationId(accountCode, eventCode, hotelCode);
            BLOCK_START_DATE = du.today(du.SHORTDATE);
            BLOCK_END_DATE = du.dayAfter(du.SHORTDATE, 10);
            accommodationsObjectList = testDataLoad.getAccommodationBlockObjData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforAllMethods() {
        try {
            BLOCK_LOCATION_ID = getLocationId(accountCode, eventCode, hotelCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        this.auth = null;
    }


    @Test(description = "post new accommodation block ", groups = {"post-accommodation-block", "Accommodation"})
    public void testPOST_AccommodationBlock() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks";
        Reporter.log("Posting new accommodation block for the location Id " + BLOCK_LOCATION_ID);
        accommodationsObjectList.get(0).setRoomTypeCode(roomCode);
        accommodationsObjectList.get(0).setLocationId(Integer.parseInt(BLOCK_LOCATION_ID));
        accommodationsObjectList.get(0).setEventId(Integer.parseInt(eventId));
        accommodationsObjectList.get(0).setStartDate(BLOCK_START_DATE);
        accommodationsObjectList.get(0).setEndDate(BLOCK_END_DATE);
        __requestJSON = gson.toJson(accommodationsObjectList.get(0));
        ObjectMapper obj = new ObjectMapper();
        Response response = restAssuredClient.POST(__requestJSON, urlPath, auth);
        String __responsePayloadJSON = obj.writeValueAsString(response.getBody().jsonPath().get("blockApiDTO"));
        if (response.getStatusCode() == 200) {
            ACCOMMODATION_BLOCK_ID = testDataLoad.toInt(jsonHelper.getJsonValueByKey(__responsePayloadJSON, "id").toString());
            if (jsonHelper.compareRequestWithResponsePayload(__requestJSON, __responsePayloadJSON)) {
                Reporter.log("Accommodation block  added successfully for the room type " + roomCode + " of the locationId " + BLOCK_LOCATION_ID);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Accommodation block was added but assertion failed!");
        } else {
            String errorMessage = " Failed to post new accommodation block [" + BLOCK_LOCATION_ID + " ]";
            Assert.assertTrue(false, getExceptionDetails(response) + errorMessage);
        }


    }

    @Test(description = "Get updated accommodation block ", groups = "Get-PutBlock", dependsOnGroups = {"put-accommodation-block"}, enabled = true)
    public void testGET_AccommodationBlock() throws Exception {
        Reporter.log("Posting new accommodation block for the location Id " + BLOCK_LOCATION_ID);
        Reporter.log("Returning Accommodation block Id " + ACCOMMODATION_BLOCK_ID);
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID;
        Response response = restAssuredClient.GET(urlPath, auth);

        // JsonPath jsonPath = new JsonPath(__requestJSON);

        JSONObject json = new JSONObject(response.asString());
        JSONArray data = json.getJSONArray("data");
        JSONObject jsonObject = data.getJSONObject(0);
        int matchingFields = 0;
        Reporter.log(__responsePayloadJSON);
        JSONObject json1 = new JSONObject(__responsePayloadJSON);
        json1.remove("pkEventId");
        Reporter.log(json1.toString(), true);
        Iterator<String> keysItr = json1.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = json1.get(key).toString();
            String responseValue = jsonObject.get(key).toString();

            if (value.equals(responseValue)) {
                Reporter.log("Request Key " + key + " value " + value + " matched with Response Value " + responseValue, true);
                matchingFields++;
            } else {
                Reporter.log("Request Key " + key + " value " + value + " not matched with Response Value " + responseValue, true);
            }
        }
        Reporter.log("matching fields length " + matchingFields, true);
        Reporter.log("----------json length " + json1.length(), true);
        if (matchingFields == json1.length()) {
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, "Response is not same");
    }

    @Test(description = "post new accommodation block ", groups = {"post-accommodation-block-dup", "Accommodation"}, dependsOnGroups = "post-accommodation-block")
    public void testPOST_AccommodationBlockDuplicate() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks";
        Reporter.log("Posting new accommodation block for the location Id " + BLOCK_LOCATION_ID);
        accommodationsObjectList.get(0).setRoomTypeCode(roomCode);
        accommodationsObjectList.get(0).setLocationId(Integer.parseInt(BLOCK_LOCATION_ID));
        accommodationsObjectList.get(0).setEventId(Integer.parseInt(eventId));
        accommodationsObjectList.get(0).setStartDate(BLOCK_START_DATE);
        accommodationsObjectList.get(0).setEndDate(BLOCK_END_DATE);
        String __requestJSON = gson.toJson(accommodationsObjectList.get(0));
        Response response = restAssuredClient.POST(__requestJSON, urlPath, auth);
        if (response.getStatusCode() == 400) {
            if (response.asString().contains("An Accommodation Block with this hotel and room type combination has already been created for this event")) {
                Reporter.log("Accommodation block  cannot be added because this hotel " + SUPPLIER_CODE + "and room code " + roomCode + " combination already exist");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Accommodation block validation message assertion failed!");
        } else if (response.getStatusCode() == 200) {
            Assert.assertTrue(false, "oops!...duplicate accommodation block created");
        } else
            Assert.assertTrue(false, getExceptionDetails(response) + " Failed to post accommodation block");
    }

    @Test(enabled = false, description = "post new accommodation block ", groups = {"post-accommodation-block-nights", "Accommodation"},
            dependsOnGroups = {"put-accommodation-nights"})
    public void testPOST_AccommodationBlockWithRoomNights() throws Exception {
        Reporter.log("Posting new accommodation block with room nights");
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks";
        accommodationsObjectList.get(1).setRoomTypeCode(roomCode);
        accommodationsObjectList.get(1).setLocationId(Integer.parseInt(BLOCK_LOCATION_ID));
        accommodationsObjectList.get(1).setEventId(Integer.parseInt(this.eventId));
        accommodationsObjectList.get(1).setStartDate(BLOCK_START_DATE);
        accommodationsObjectList.get(1).setEndDate(BLOCK_END_DATE);
        roomNight.setInventory(5);
        roomNight.setContracted(5);
        roomNight.setDate(BLOCK_START_DATE);
        roomNight.setRate(120.50);
        roomNights.add(roomNight);
        accommodationsObjectList.get(1).setRoomNights(roomNights);
        String __requestJSON = gson.toJson(accommodationsObjectList.get(1));
        Response response = restAssuredClient.POST(__requestJSON, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            JsonPath jp = new JsonPath(__responsePayloadJSON);
            Map<String, Object> map = jp.get("blockApiDTO");
            String __jsonRequestMsg = gson.toJson(map);
            int accommodationBlockId = testDataLoad.toInt(jsonHelper.getJsonValueByKey(__jsonRequestMsg, "blockApiDTO", "id"));
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestMsg, __responsePayloadJSON)) {
                Reporter.log("Accommodation block  " + accommodationBlockId + " added successfully for the room type " + roomCode + " of the locationId " + BLOCK_LOCATION_ID);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Accommodation block " + accommodationBlockId + " was added but assertion failed!");
        } else {
            String errorMessage = " Failed to post new accommodation block for the room code [" + roomCode + " ]";
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

    @Test(description = "post new accommodation block ", groups = {"post-accommodation-nights", "Accommodation"}, priority = 1,
            dependsOnGroups = {"put-accommodation-block"})
    public void testPOST_AccommodationBlockRoomNights() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID + "/roomnights";
        Reporter.log("Posting room nights for the accommodation block " + ACCOMMODATION_BLOCK_ID);
        Map<String, Object> accommodationRoom = new HashMap<>();
        ObjectMapper obj = new ObjectMapper();
        accommodationRoom.put("blockId", ACCOMMODATION_BLOCK_ID);
        accommodationRoom.put("roomTypeCode", roomCode);
        accommodationRoom.put("inventory", accommodationsObjectList.get(0).getDefaultInventory());
        accommodationRoom.put("date", du.dayAfter(du.SHORTDATE, 1));
        accommodationRoom.put("rate", randomNumber(2));
        String __requestJSON = gson.toJson(accommodationRoom);
        Response response = restAssuredClient.POST(__requestJSON, urlPath, auth);
        String __responsePayloadJSON = obj.writeValueAsString(response.getBody().jsonPath().get("roomNightApiDTO"));
        if (response.getStatusCode() == 200) {
            ROOM_NIGHT_ID = jsonHelper.getJsonValueByKey(__responsePayloadJSON, "id").toString();
            if (jsonHelper.compareRequestWithResponsePayload(__requestJSON, __responsePayloadJSON)) {
                Reporter.log("Room Nights created successfully for the accommodation block  [" + BLOCK_LOCATION_ID + "]", true);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Post was successful but Validation Failed!");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post room nights for accommodation block [" + BLOCK_LOCATION_ID);

        }
    }

    @DataProvider(name = "get-accommodation-dp1", parallel = false)
    public Object[][] getAccommodationBlkFilters() throws Exception {
        Object obj = this.testDataLoad.getAccommodationObjFilters(accommodationsObjectList.get(0));
        return this.testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-accommodation-dp1", description = "get accommodation block ", priority = 2, groups = {"get-accommodation-block", "Accommodation"},
            dependsOnGroups = {"post-accommodation-block"})
    public void testGET_AccommodationBlocksWithSearchFilters(String key, String value) throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks?" + key + "=" + value;
        Reporter.log("Getting accommodation blocks by search filter " + key + "=" + value);
        Response response = restAssuredClient.GET(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __blockCollection = jsonHelper.getJsonArray(__responsePayloadJSON, "data");
            int __instanceCount = jsonHelper.getInstanceCount(__responsePayloadJSON, "data", key, value);
            if (__instanceCount == __blockCollection.size()) {
                Reporter.log("Get Accommodation block details retrieved successfully by search filter " + key + "=" + value);
                Assert.assertTrue(true);
            }
        } else {
            String errorMessage = " Failed to get accommodation blocks with  search filter " + key + "=" + value;
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

    @DataProvider(name = "get-accommodations-multi-filters")
    public Object[][] getAccommodationMultipleFilters() throws Exception {
        Object obj = testDataLoad.getAccommodationObjFilters(accommodationsObjectList.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-accommodations-multi-filters", description = "get accommodation block ", priority = 2, groups = {"get-accommodation-block", "Accommodation"}, dependsOnGroups = {"post-accommodation-block"})
    public void testGET_AccommodationBlocksWithMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        Reporter.log("Getting accommodation blocks with multiple search filters ");
        List<ArrayList> mapToArray = testDataLoad.mapToArray(multipleFilters);
        ArrayList<String> keys = mapToArray.get(0);
        ArrayList values = mapToArray.get(1);
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks?" + keys.get(0) + "=" + values.get(0);
        String queryParams = "";
        for (int i = 1; i < keys.size(); i++) {
            queryParams += "&" + keys.get(i) + "=" + values.get(i);
        }
        urlPath = urlPath + queryParams;
        Response response = restAssuredClient.GET(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __blockCollection = jsonHelper.getJsonArray(__responsePayloadJSON, "data");
            int conditionsMatch = 0;
            for (int i = 0; i < keys.size(); i++) {
                int __instanceCount = jsonHelper.getInstanceCount(__responsePayloadJSON, "data", keys.get(i), values.get(i));
                if (__blockCollection.size() == __instanceCount) {
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
            Reporter.log("HTTP 404 - No Events found matching criteria...!");
            Assert.assertTrue(true);

        } else {
            String errorMessage = " Failed to get accommodation blocks with multiple search filters";
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

    @Test(description = "get accommodation block ", groups = {"get-accommodation-block", "Accommodation"}, priority = 2,
            dependsOnGroups = {"post-accommodation-block"})
    public void testGET_AccommodationBlocks() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks";
        Reporter.log("Getting accommodation block  details for the block location Id " + BLOCK_LOCATION_ID);
        String blockLocId = BLOCK_LOCATION_ID;
        Response response = restAssuredClient.GET(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            Thread.sleep(5000);
            ArrayList __blockCollection = jsonHelper.getJsonArray(__responsePayloadJSON, "data");
            Reporter.log("Calculated block collection size " + __blockCollection.size());
            Reporter.log("Block location id " + BLOCK_LOCATION_ID);
            Reporter.log("Block location id new " + blockLocId);
            int __instanceCount = jsonHelper.getInstanceCount(__responsePayloadJSON, "data", "locationId", blockLocId);
            Reporter.log("Total instance count " + __instanceCount);
            if (__instanceCount == __blockCollection.size()) {
                Reporter.log("Accommodation block details retrieved successfully by the location =" + BLOCK_LOCATION_ID);
                Assert.assertTrue(true);
            } else {
                String errorMessage = " Failed to get accommodation block  details [" + BLOCK_LOCATION_ID + " ]";
                Assert.assertTrue(false, response.getStatusLine() + errorMessage);
            }
        }
    }

    @Test(description = "get accommodation block ", groups = {"get-accommodation-block", "Accommodation"}, priority = 2,
            dependsOnGroups = {"post-accommodation-block"})
    public void testGET_AccommodationBlockDetailsById() throws Exception {
        //BLOCK_LOCATION_ID = getLocationId();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID;
        Reporter.log("Getting accommodation block  details by the blockId " + ACCOMMODATION_BLOCK_ID);
        Response response = restAssuredClient.GET(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList<Map<String, Object>> dataArray = jsonHelper.getJsonArray(__responsePayloadJSON, "data");
            ArrayList<Map<String, Object>> roomNightsArr = (ArrayList) dataArray.get(0).get("roomNights");
            int totalNights = du.countDaysBetweenTwoDates(BLOCK_START_DATE, BLOCK_END_DATE, du.SHORTDATE);
            int actualId = (Integer) roomNightsArr.get(0).get("blockId");
            Reporter.log("Block " + ACCOMMODATION_BLOCK_ID + " details retrieved correctly and total room nights created " + totalNights, true);
            if (actualId == ACCOMMODATION_BLOCK_ID && roomNightsArr.size() == (totalNights))
                Assert.assertTrue(true);
            else
                Assert.assertTrue(false);
        } else {
            String errorMessage = " Failed to get accommodation block  details by Id [" + BLOCK_LOCATION_ID;
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

    @Test(description = "get accommodation block ", groups = {"get-accommodation-block", "Accommodation"}, priority = 2,
            dependsOnGroups = {"post-accommodation-block"})
    public void testGET_AccommodationBlockDetailsForEvent() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/blocks";
        Reporter.log("Getting accommodation blocks  for the event  " + this.eventCode);
        Response response = restAssuredClient.GET(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __blockCollection = jsonHelper.getJsonArray(__responsePayloadJSON, "data");
            int __instanceCount = jsonHelper.getInstanceCount(__responsePayloadJSON, "data", "eventId", this.eventId);
            if (__instanceCount == __blockCollection.size()) {
                Reporter.log("Accommodation block details retrieved successfully by the event code=" + this.eventCode);
                Assert.assertTrue(true);
            } else {
                String errorMessage = " Failed to get accommodation blocks for the event code  [" + this.eventCode + " ] ";
                Assert.assertTrue(false, response.getStatusLine() + errorMessage);
            }
        }
    }

    @Test(description = "post new accommodation block ", groups = {"put-accommodation-nights", "Accommodation"}, priority = 3,
            dependsOnGroups = {"post-accommodation-nights"})
    public void testPUT_ReviseAccommodationBlockRoomNights() throws Exception {
        Reporter.log("Revising room nights for the accommodation block " + ACCOMMODATION_BLOCK_ID);
        //BLOCK_LOCATION_ID = getLocationId();
        ObjectMapper obj = new ObjectMapper();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID + "/roomnights/" + ROOM_NIGHT_ID;
        Map<String, Object> accommodationRoom = new HashMap<>();
        accommodationRoom.put("blockId", ACCOMMODATION_BLOCK_ID);
        accommodationRoom.put("roomTypeCode", accommodationsObjectList.get(0).getRoomTypeCode());
        accommodationRoom.put("inventory", randomNumber(3));
        accommodationRoom.put("rate", randomNumber(3));
        accommodationRoom.put("contracted", randomNumber(2));
        String __requestJSON = gson.toJson(accommodationRoom);
        Response response = restAssuredClient.PUT(__requestJSON, urlPath, auth);
        String __responsePayloadJSON = obj.writeValueAsString(response.getBody().jsonPath().get("roomNightApiDTO"));
        // System.out.println(__responsePayloadJSON);
        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__requestJSON, __responsePayloadJSON)) {
                Reporter.log("Room Nights revised successfully for the accommodation block  [" + BLOCK_LOCATION_ID + "]");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "PUT was successful but Validation Failed! \n" + __responsePayloadJSON);
        } else {
            String errorMessage = " Failed to revise room nights for accommodation block [" + BLOCK_LOCATION_ID;
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);

        }
    }

    @Test(description = "post new accommodation block ", groups = {"put-accommodation-block", "Accommodation"}, priority = 4, enabled = true,
            dependsOnGroups = {"post-accommodation-block", "post-accommodation-reservation", "update-accommodation-reservation"})
    public void testPUT_ReviseAccommodationBlock() throws Exception {
        Reporter.log("Revising Accommodation block " + ACCOMMODATION_BLOCK_ID);
        // BLOCK_LOCATION_ID = getLocationId();
        ObjectMapper obj = new ObjectMapper();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID;
        accommodationsObjectList.get(1).setLocationId(Integer.parseInt(BLOCK_LOCATION_ID));
        accommodationsObjectList.get(1).setEventId(Integer.parseInt(this.eventId));
        Map<String, Object> objectMap = testDataLoad.convertObjectToMap(accommodationsObjectList.get(0));
        objectMap.put("startDate", du.dayAfter(du.SHORTDATE, 1));
        objectMap.put("endDate", du.dayAfter(du.SHORTDATE, 5));
        String __requestJSON = gson.toJson(objectMap);
        Response response = restAssuredClient.PUT(__requestJSON, urlPath, auth);
        __responsePayloadJSON = obj.writeValueAsString(response.getBody().jsonPath().get("blockApiDTO"));
        System.out.println(__responsePayloadJSON);
        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__requestJSON, __responsePayloadJSON)) {
                Reporter.log("Accommodation Block  [" + BLOCK_LOCATION_ID + "] updated successfully ");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Accommodation block was updated but Validation Failed!");
        } else {
            String errorMessage = " Failed to update accommodation block [" + BLOCK_LOCATION_ID;
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);

        }
    }

    @Test(description = "get accommodation block ", groups = {"delete-accommodation-nights", "Accommodation"}, priority = 5,
            dependsOnGroups = {"post-accommodation-nights"})
    public void testDELETE_AccommodationBlockRoomNights() throws Exception {
        // BLOCK_LOCATION_ID = getLocationId();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID + "/roomnights/" + ROOM_NIGHT_ID;
        Reporter.log("Deleting accommodation block " + ACCOMMODATION_BLOCK_ID + " room nights " + ROOM_NIGHT_ID, true);
        Response response = restAssuredClient.DELETE(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            Reporter.log("Room night id " + ROOM_NIGHT_ID + " deleted successfully ", true);
            Assert.assertTrue(true);
        } else {
            String errorMessage = " Failed to delete accommodation block " + ACCOMMODATION_BLOCK_ID + " room nights " + ROOM_NIGHT_ID;
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

    @Test(description = "get accommodation block ", groups = {"delete-accommodation-block", "Accommodation"}, priority = 6, enabled = true,
            dependsOnGroups = {"post-accommodation-block", "post-accommodation-reservation", "delete-accommodation-reservation", "Get-PutBlock"})
    public void testDELETE_AccommodationBlockDetails() throws Exception {
        // BLOCK_LOCATION_ID = getLocationId();
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + accountCode + "/events/" + this.eventCode + "/locations/" + BLOCK_LOCATION_ID + "/blocks/" + ACCOMMODATION_BLOCK_ID;
        Reporter.log("Deleting accommodation block  details " + ACCOMMODATION_BLOCK_ID);
        Response response = restAssuredClient.DELETE(urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            int __blockId = testDataLoad.toInt(jsonHelper.getJsonValueByKey(__responsePayloadJSON, "blockApiDTO", "id"));
            String __roomCode = jsonHelper.getJsonValueByKey(__responsePayloadJSON, "blockApiDTO", "roomTypeCode");
            String __active = jsonHelper.getJsonValueByKey(__responsePayloadJSON, "blockApiDTO", "active");
            if (__blockId == ACCOMMODATION_BLOCK_ID && __active.equalsIgnoreCase("false") && __roomCode.equals(roomCode)) {
                Reporter.log("Successfully deleted the block location for the room type code : " + roomCode);
                Assert.assertTrue(true);
            }
        } else {
            String errorMessage = " Failed to delete accommodation block  details [" + BLOCK_LOCATION_ID + " ] ";
            Assert.assertTrue(false, response.getStatusLine() + errorMessage);
        }
    }

}
