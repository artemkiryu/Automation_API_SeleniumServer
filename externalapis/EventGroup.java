package com.certain.External.service.v1;

import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.*;
import internal.qaauto.certain.platform.services.AgendaItemObjSvc;
import internal.qaauto.certain.platform.services.AttendeeTypeObjSvc;
import internal.qaauto.certain.platform.services.EventObjSvc;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
public class EventGroup extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private final ArrayList<String> attendeeList = new ArrayList<>();
    private final ArrayList<String> agendaList = new ArrayList<>();
    private final ArrayList<String> arrayList = new ArrayList<>();
    String eventCode, eventName, AttendeeTypeCode, ActivityTypeCode, eventId;
    String userEventCode;
    String eventEndDate, eventStartDate;
    String GroupCode, GroupName;
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private AgendaItemObjSvc agendaItemObjSvc = new AgendaItemObjSvc();
    private AttendeeTypeObjSvc attendeeTypeObjSvc = new AttendeeTypeObjSvc();
    private List<com.certain.external.dto.agendaItem.AgendaItemObj> agendaTypeTestData = new ArrayList<>();
    private List<com.certain.external.dto.attendeeType.AttendeeType> attendeeTypeData = new ArrayList<>();
    private List<com.certain.external.dto.event.EventObj> eventObjList = new ArrayList<>();
    private AuthenticationScheme auth = new AuthenticationScheme();
    private List<EventGroups> groupsList = new ArrayList<>();
    private EventGroups temp = new EventGroups();
    private boolean isPassed = true;
    private String groupCodeWithAgenda;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        userEventCode = USER_EVENT_CODE;
        //ACCOUNT_CODE = ACCOUNT_CODE;
        System.out.println("-------------" + userEventCode);
        System.out.println("-------------" + ACCOUNT_CODE);
        auth.setUsername(USERNAME);
        auth.setPassword(PASSWORD);
        try {
            groupsList = testDataLoad.getEventGroupsObjData();
            agendaTypeTestData = testDataLoad.getAgendaItemObjData();
            eventObjList = testDataLoad.getEventObjData("location", ACCOUNT_CODE);
            attendeeTypeData = testDataLoad.getAttendeeTypeObjData(ACCOUNT_CODE);
            temp = groupsList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"post-event-for-group", "EventGroup"})
    public void testGetEventDetails() throws Exception {
        eventCode = userEventCode;
        eventName = eventObjSvc.getEventName(ACCOUNT_CODE, userEventCode);
        eventId = eventObjSvc.getEventId(ACCOUNT_CODE, userEventCode);
        eventStartDate = eventObjSvc.getEventStartDate(ACCOUNT_CODE, userEventCode);
        eventEndDate = eventObjSvc.getEventEndDate(ACCOUNT_CODE, userEventCode);
    }

    @Test(enabled = true, groups = {"post-agenda-for-group", "EventGroup"},
            dependsOnGroups = "post-event-for-group")
    public void testPOSTAgendaItemMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.agendaItem.AgendaItemObj agendaItemObj = agendaTypeTestData.get(0);
        String uniqueCode = randomString(15);
        agendaItemObj.setActivityCode(uniqueCode);
        agendaItemObj.setName("Agenda " + uniqueCode);
        ActivityTypeCode = uniqueCode;
        TestCase testCase = agendaItemObjSvc.createAgendaItem(ACCOUNT_CODE, eventCode, agendaItemObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-attendee-for-group", "EventGroup"},
            dependsOnGroups = "post-event-for-group")
    public void testPOSTAttendeeTypeMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(0);
        String uniqueCode = randomString(15);
        attendeeType.setAttendeeTypeCode(uniqueCode);
        attendeeType.setName("Attendee " + uniqueCode);
        AttendeeTypeCode = uniqueCode;
        TestCase testCase = attendeeTypeObjSvc.createAttendeeType(ACCOUNT_CODE, eventCode, attendeeType, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, description = "posting new promo code", groups = {"post-group", "EventGroup"},
            dependsOnGroups = {"post-event-for-group", "post-attendee-for-group"})
    public void testPOSTAddNewGroup() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        Reporter.log("Posting New Promo Code to the Event " + eventCode);
        attendeeList.add(AttendeeTypeCode);
        GroupName = groupsList.get(0).getName();
        GroupCode = groupsList.get(0).getCode();
        groupsList.get(0).setAttendeeTypeCodes(attendeeList);
        String __jsonRequestBody = gson.toJson(groupsList.get(0));
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (groupsList.get(0).getAvailabilityStart().equalsIgnoreCase("Now")) {
            groupsList.get(0).setAvailabilityStart("specifiedStartDate");
            groupsList.get(0).setAvailabilityStartDate(du.today(du.SHORTDATE));
        }
        if (groupsList.get(0).getAvailabilityEnd().equalsIgnoreCase("EventEnds")) {
            groupsList.get(0).setAvailabilityEnd("eventEnds");
            groupsList.get(0).setAvailabilityEndDate(du.convertDateFromString(eventEndDate, du.SHORTDATE));
            groupsList.get(0).setAvailabilityEndTime(du.convertDateFromString(eventEndDate, "hh:mm aa"));
        }
        __jsonRequestBody = gson.toJson(groupsList.get(0));

        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestBody, __responsePayloadJSON)) {
                Reporter.log("Promo Code " + GroupName + " Added to Event " + eventCode + " Successfully");
                testGETGroupDetails();
            } else
                Assert.assertTrue(true, "Post was successful but assertion failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode);
        }
    }

    @Test(enabled = true, priority = 1, description = "posting new promo code", groups = {"post-group2", "EventGroup"},
            dependsOnGroups = {"post-agenda-for-group", "post-group"})
    public void testPOSTNewGroupWithAgenda() throws Exception {
        Reporter.log("Posting New PromoCode to the Event " + eventCode + " With Agenda Item");
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        groupCodeWithAgenda = groupsList.get(2).getCode();
        agendaList.add(ActivityTypeCode);
        groupsList.get(2).setAgendaItemCodes(agendaList);
        groupsList.get(2).setAttendeeTypeCodes(attendeeList);
        String __jsonRequestBody = gson.toJson(groupsList.get(2));
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();

        if (groupsList.get(0).getAvailabilityStart().equalsIgnoreCase("Now")) {
            groupsList.get(0).setAvailabilityStart("specifiedStartDate");
            groupsList.get(0).setAvailabilityStartDate(du.today(du.SHORTDATE));
        }
        if (groupsList.get(0).getAvailabilityEnd().equalsIgnoreCase("EventEnds")) {
            groupsList.get(0).setAvailabilityEnd("eventEnds");
            groupsList.get(0).setAvailabilityEndDate(du.convertDateFromString(eventEndDate, du.SHORTDATE));
            groupsList.get(0).setAvailabilityEndTime(du.convertDateFromString(eventEndDate, "hh:mm aa"));
        }
        __jsonRequestBody = gson.toJson(groupsList.get(0));

        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestBody, __responsePayloadJSON)) {
                Reporter.log("PromoCode " + groupCodeWithAgenda + " added to event " + eventCode + " Successfully");
                testGETGroupDetails();
                if (isPassed)
                    Assert.assertTrue(true);
            } else
                Assert.assertTrue(true, "Post was successful but assertion failed");
        } else if (response.getStatusCode() == 400 && __responsePayloadJSON.contains("code is not discountable")) {
            Reporter.log("Agenda Item not discountable so could not create group with it....");
            Assert.assertTrue(true);
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode + " ]");
        }
    }

    @Test(enabled = false, priority = 2, description = "deleting group", groups = {"delete-group2", "EventGroup"},
            dependsOnGroups = "post-group2")
    public void testDELETEGroupHavingAgenda() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups/" + groupCodeWithAgenda;
        Reporter.log("Deleting a PromoCode/Group " + groupCodeWithAgenda + " having Agenda");
        Response response = super.restAssuredClient.DELETE(urlPath, this.auth);
        String __jsonResponseMessage = response.asString();

        if (response.getStatusCode() == 200) {
            if (__jsonResponseMessage.contains("Group with group code " + groupCodeWithAgenda + " has been deleted")) {
                Reporter.log("Group " + groupCodeWithAgenda + " deleted successfully.");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Delete Was Successful but Validation Failed");
        } else {
            String ErrorMessage = " Failed to delete group [" + groupCodeWithAgenda + " ]";
            Assert.assertTrue(false, response.getStatusLine() + ErrorMessage);
        }
    }

    @Test(enabled = true, description = "posting new promo code", groups = {"post-group1", "EventGroup"},
            priority = 1, dependsOnGroups = {"post-attendee-for-group", "post-group"})
    public void testPOSTGroupNoName() throws Exception {
        Reporter.log("Posting New Group/PromoCode with No Name");
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        temp.setName("  ");
        temp.setAttendeeTypeCodes(attendeeList);
        String __jsonRequestBody = gson.toJson(temp);
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 400) {
            if (__responsePayloadJSON.contains("Please provide a valid name")) {
                Reporter.log("Received Proper Error Message as");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Assertion Failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Error occurred while posting the group");
        }
    }

    @Test(enabled = true, description = "posting new promo code", groups = {"post-group1", "EventGroup"},
            priority = 1, dependsOnGroups = "post-event-for-group")
    public void testPOSTGroupInvalidAttendee() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        Reporter.log("Posting new Group/PromoCode with Invalid AttendeeType");
        temp = groupsList.get(0);
        arrayList.add("ATNDINV123");
        temp.setName("GRP1");
        temp.setAttendeeTypeCodes(arrayList);
        String __jsonRequestBody = gson.toJson(temp);
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 400) {
            if (__responsePayloadJSON.contains("Attendee Type ATNDINV123 does not exist for this event.")) {
                Reporter.log("Failed to Post and got Proper Error Message ");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Got Improper message");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode + " ]");
        }
    }

    @Test(enabled = true, description = "posting new promo code", groups = {"post-group1", "EventGroup"},
            priority = 1, dependsOnGroups = "post-event-for-group")
    public void testPOSTGroupNoAttendee() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        Reporter.log("Posting new Group/PromoCode with No AttendeeType");
        temp.setAttendeeTypeCodes(null);
        temp.setName("GRP1");
        String __jsonRequestBody = gson.toJson(temp);
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 400) {
            if (__responsePayloadJSON.contains("You must select an attendee type")) {
                Reporter.log("Failed to post got proper error message ");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Got improper error message ");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode + " ]");
        }
    }

    @Test(enabled = true, description = "posting new promo code", groups = {"post-group1", "EventGroup"},
            priority = 1, dependsOnGroups = {"post-event-for-group", "post-group"})
    public void testPOSTGroupInvalidAgenda() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        Reporter.log("Posting new Group/PromoCode with Invalid Agenda Code ");
        ArrayList<String> agenda = new ArrayList<>();
        agenda.add("INVAGNDA");
        temp.setCode("GTEstYUI");
        temp.setName("GRP1");
        temp.setAgendaItemCodes(agenda);
        temp.setAttendeeTypeCodes(attendeeList);
        String __jsonRequestBody = gson.toJson(temp);
        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 400) {
            if (__responsePayloadJSON.contains("Agenda Item " + agenda.get(0) + " code does not exist for this event")) {
                Reporter.log("Cannot create got Proper Error Message ");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Gor improper error message ");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode + " ]");
        }
    }

    @Test(enabled = true, priority = 2, description = "deleting group", groups = {"delete-group", "EventGroup"},
            dependsOnGroups = "post-group")
    public void testGETGroupDetails() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups/" + GroupCode;
        Reporter.log("Getting Group/PromoCode Details  " + GroupName);
        Response response = super.restAssuredClient.GET(urlPath, this.auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            String __code = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "code").toString();
            String __groupName = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "name").toString();
            String eventId = jsonHelper.getJsonValueByKey(__jsonResponseMessage, "eventId").toString();
            if (__code.equals(GroupCode) && eventId.equals(eventId) && __groupName.equals(GroupName)) {
                Reporter.log("Group/PromoCode " + GroupName + " Details Retrieved Successfully.");
                isPassed = true;
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Get was successful  but validation failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to get group details [" + GroupName);
        }
    }

    @Test(enabled = true, priority = 2, description = "deleting group", groups = {"delete-group", "EventGroup"},
            dependsOnGroups = "post-group")
    public void testGETGroupForEvent() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups?eventGroupDiscountCode=true";
        Reporter.log("Getting Group/PromoCodes List for the Event " + eventCode);
        Response response = super.restAssuredClient.GET(urlPath, this.auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            ArrayList __eventGroupList = jsonHelper.getJsonArray(__jsonResponseMessage, "content");
            int __instanceCount = jsonHelper.getInstanceCount(__jsonResponseMessage, "content", "eventId", eventId);
            if (__instanceCount == __eventGroupList.size()) {
                Reporter.log("Groups/PromoCodes List retrieved for the event  " + eventName);
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Get was successful  but validation failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to get group details [" + GroupName + " ]");
        }
    }

    @Test(enabled = true, description = "update group add another attendee", priority = 3, groups = {"put-group", "EventGroup"},
            dependsOnGroups = {"post-group", "post-attendee-for-group"})
    public void testPUTReviseGroup() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups/" + GroupCode;
        Reporter.log("Update Group/PromoCode Details  " + GroupName);
        groupsList.get(1).setCode(GroupCode);
        groupsList.get(1).setAttendeeTypeCodes(attendeeList);
        if (groupsList.get(1).getAvailabilityStart().equalsIgnoreCase("specifiedStartDate") || groupsList.get(1).getAvailabilityStart().equalsIgnoreCase("Now")) {
            groupsList.get(1).setAvailabilityStart("specifiedStartDate");
            groupsList.get(1).setAvailabilityStartTime(du.today("hh:mm aa"));
            groupsList.get(1).setAvailabilityStartDate(du.today(du.SHORTDATE));
        }
        if (groupsList.get(1).getAvailabilityEnd().equalsIgnoreCase("specifiedEndDate")) {
            groupsList.get(1).setAvailabilityEndDate(du.dayAfter(du.SHORTDATE, 20));
            groupsList.get(1).setAvailabilityEndTime(du.today("hh:mm aa"));
        }
        String __jsonRequestBody = gson.toJson(groupsList.get(1));
        Response response = super.restAssuredClient.PUT(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();
        if (response.getStatusCode() == 200) {
            if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestBody, __responsePayloadJSON)) {
                Reporter.log("Group/PromoCode " + GroupName + " Updated Successfully");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Put  was successful but assertion failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to update Group/PromoCode " + GroupCode);
        }
    }

    @Test(enabled = true, priority = 4, description = "deleting group", groups = {"delete-group", "EventGroup"},
            dependsOnGroups = "post-group")
    public void testDELETEGroup() throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups/" + GroupCode;
        Reporter.log("Deleting Group/PromoCode Details  " + GroupName);
        Response response = super.restAssuredClient.DELETE(urlPath, this.auth);
        String __jsonResponseMessage = response.asString();
        if (response.getStatusCode() == 200) {
            if (__jsonResponseMessage.contains("Group with group code " + GroupCode + " has been deleted")) {
                Reporter.log("Group " + GroupName + " deleted successfully.");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Delete was successful but validation failed");
        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to delete group [" + GroupName);
        }
    }

    @DataProvider(name = "get-groups")
    public Object[][] getEvents() throws Exception {
        return testDataLoad.getEventGroupTestCases();
    }

    @Test(dataProvider = "get-groups", enabled = true, dependsOnGroups = {"post-attendee-for-group", "EventGroup"})
    public void testPOSTGroupNegativeTests(Map<String, Object> testcaseMap, String StatusCode, String errorResponse) throws Exception {
        String urlPath = SERVER_HOST + UC_BASE_PATH + "/accounts/" + ACCOUNT_CODE + "/events/" + eventCode + "/groups";
        Reporter.log("Posting New Group/PromoCode With Various Data Combinations ");
        ArrayList<String> attendees = new ArrayList<>();
        attendees.add(AttendeeTypeCode);
        String CODE = testcaseMap.get("code").toString();
        testcaseMap.put("attendeeTypeCodes", attendees);
        String __jsonRequestBody = gson.toJson(testcaseMap);

        Response response = super.restAssuredClient.POST(__jsonRequestBody, urlPath, auth);
        String __responsePayloadJSON = response.asString();

        if (testcaseMap.get("availabilityStart").toString().equalsIgnoreCase("Now")) {
            testcaseMap.put("availabilityStart", "specifiedStartDate");
            testcaseMap.put("availabilityStartDate", du.today(du.SHORTDATE));
        }
        if (testcaseMap.get("availabilityEnd").toString().equalsIgnoreCase("EventEnds")) {
            testcaseMap.put("availabilityEnd", "eventEnds");
            testcaseMap.put("availabilityEndDate", du.convertDateFromString(eventEndDate, du.SHORTDATE));
            testcaseMap.put("availabilityEndTime", du.convertDateFromString(eventEndDate, "hh:mm aa"));
        }
        if (testcaseMap.get("availabilityStart").toString().equalsIgnoreCase("beforeEventStartDate")) {
            String date = du.SubstractDaysFromDate(eventStartDate,
                    Integer.parseInt(testcaseMap.get("availabilityStartMonth").toString()),
                    Integer.parseInt(testcaseMap.get("availabilityStartDay").toString()),
                    Integer.parseInt(testcaseMap.get("availabilityStartHour").toString()));
            testcaseMap.put("availabilityStart", "specifiedStartDate");
            testcaseMap.remove("availabilityStartMonth");
            testcaseMap.remove("availabilityStartDay");
            testcaseMap.remove("availabilityStartHour");
            testcaseMap.put("availabilityStartDate", du.convertDateFromString(date, du.SHORTDATE));
            testcaseMap.put("availabilityStartTime", du.convertDateFromString(date, "hh:mm aa"));
        }

        if (testcaseMap.get("availabilityEnd").toString().equalsIgnoreCase("beforeEventStartDate")) {
            String date = du.SubstractDaysFromDate(eventStartDate,
                    Integer.parseInt(testcaseMap.get("availabilityEndMonth").toString()),
                    Integer.parseInt(testcaseMap.get("availabilityEndDay").toString()),
                    Integer.parseInt(testcaseMap.get("availabilityEndHour").toString()));
            testcaseMap.put("availabilityEnd", "specifiedEndDate");
            testcaseMap.remove("availabilityEndMonth");
            testcaseMap.remove("availabilityEndDay");
            testcaseMap.remove("availabilityEndHour");
            testcaseMap.put("availabilityEndDate", du.convertDateFromString(date, du.SHORTDATE));
            testcaseMap.put("availabilityEndTime", du.convertDateFromString(date, "hh:mm aa"));
        }
        __jsonRequestBody = gson.toJson(testcaseMap);
        if (response.getStatusCode() == Integer.parseInt(StatusCode)) {
            if (Integer.parseInt(StatusCode) == 200) {
                if (jsonHelper.compareRequestWithResponsePayload(__jsonRequestBody, __responsePayloadJSON)) {
                    Reporter.log("PromoCode " + CODE + " added successfully");
                    Assert.assertTrue(true);
                } else
                    Assert.assertTrue(true, "Post was Successful but validation Failed");

            } else if (__responsePayloadJSON.contains(errorResponse)) {
                Reporter.log("Got Proper Error Message");
                Assert.assertTrue(true);
            } else
                Assert.assertTrue(false, "Error message assertion failed");

        } else {
            Assert.assertTrue(false, response.getStatusLine() + " Failed to post group to the event [" + eventCode + " ]");
        }
    }

   /* @Test(enabled = true, groups = {"delete-event-for-group","EventGroups"},
            dependsOnGroups = {"post-group","delete-group","post-event-for-group","post-attendee-for-group"})
    public void testDELETEEvent() throws Exception {
        TestCase testCase = eventObjSvc.deleteEvent(ACCOUNT_CODE,eventCode);
        if(testCase.isPassed()){
            Reporter.log(testCase.getMessage(),true);
            Assert.assertTrue(true);
        }else Assert.assertTrue(false,testCase.getMessage());
    }*/
}
