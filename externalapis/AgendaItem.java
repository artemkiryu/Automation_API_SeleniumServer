package com.certain.External.service.v1;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.AgendaItemObjSvc;
import internal.qaauto.certain.platform.services.EventObjSvc;
import internal.qaauto.certain.platform.services.LocationObjSvc;
import internal.qaauto.certain.platform.services.SpeakerObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class AgendaItem extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    List<com.certain.external.dto.agendaItem.Speaker> speakers = new ArrayList<>();
    String eventCode;
    String locationCode;
    int speakerCode;
    String ACTIVITY_TYPE_CODE;
    String ACTIVITY_TYPE_CODE_REQ;
    private AgendaItemObjSvc agendaItemObjSvc = new AgendaItemObjSvc();
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private LocationObjSvc locationObjSvc = new LocationObjSvc();
    private SpeakerObjSvc speakerObjSvc = new SpeakerObjSvc();
    private List<com.certain.external.dto.agendaItem.AgendaItemObj> agendaTypeTestData = new ArrayList<>();
    private List<com.certain.external.dto.event.EventObj> eventObjList = new ArrayList<>();
    private List<com.certain.external.dto.speakerobj.SpeakerObj> speakerObjs = new ArrayList<>();
    private List<com.certain.external.dto.location.LocationObj> locationObjs = new ArrayList<>();
    private int accountId;
    private String[] orderBy = {"isActivity_asc", "isActivity_desc", "isAgenda_asc", "isAgenda_desc", "isRequired_asc", "isRequired_desc",
            "startDate_asc", "startDate_desc", "endDate_asc", "endDate_desc", "noPrint_asc", "noPrint_desc", "allowWaitlist_asc", "allowWaitlist_desc",
            "notified_asc", "notified_desc", "omitDate_asc", "omitDate_desc", "hideOnSchedule_asc", "hideOnSchedule_desc", "hideRating_asc", "hideRating_desc",
            "printTicket_asc", "printTicket_desc", "isActive_asc", "isActive_desc", "activityCode_asc", "activityCode_desc", "email_asc", "email_desc"};

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountId = ACCOUNTID;
        try {
            agendaTypeTestData = testDataLoad.getAgendaItemObjDataWithAllValues();
            speakerObjs = testDataLoad.getSpeakersObjData(ACCOUNT_CODE);
            eventObjList = testDataLoad.getEventObjData("location", ACCOUNT_CODE);
            locationObjs = testDataLoad.getLocationObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-location-for-agenda", "AgendaItem"})
    public void testAddNewLocation() throws Exception {
        com.certain.external.dto.location.LocationObj locationObj = locationObjs.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        locationObj.setName("Location " + uniqueCode);
        locationObj.setLocationCode(uniqueCode);
        locationCode = locationObj.getLocationCode();
        TestCase testCase = locationObjSvc.createLocation(ACCOUNT_CODE, locationObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-event-for-agenda", "AgendaItem"})
    public void testAddNewEvent() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        TestCase testCase = eventObjSvc.postEvent(ACCOUNT_CODE, eventObj, expectedCondition, true);
        eventCode = eventObj.getEventCode();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-speaker-for-agenda", "AgendaItem"}, dependsOnGroups = "post-event-for-agenda")
    public void testAddNewSpeaker() throws Exception {
        com.certain.external.dto.speakerobj.SpeakerObj speakerObj = speakerObjs.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        speakerObj.setEventCode(eventCode);
        speakerObj.setEmail(uniqueCode + "@email.com");
        TestCase testCase = speakerObjSvc.createSpeaker(ACCOUNT_CODE, eventCode, speakerObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerCode = Integer.parseInt(testCase.getSpeakerCode());
            com.certain.external.dto.agendaItem.Speaker speaker = new com.certain.external.dto.agendaItem.Speaker();
            speaker.setSpeakerCode(Long.valueOf(speakerCode));
            speaker.setFirstName(speakerObj.getFirstName());
            speaker.setLastName(speakerObj.getLastName());
            speaker.setBio(speakerObj.getBio());
            speakers.add(speaker);
//            agendaTypeTestData.get(0).setSpeaker(speakers);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-agenda-item", "AgendaItem"},
            dependsOnGroups = {"post-event-for-agenda", "post-speaker-for-agenda"})
    public void testPOSTAgendaItem() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.agendaItem.AgendaItemObj agendaItemObj = agendaTypeTestData.get(0);
        String uniqueCode = randomString(15);
        agendaItemObj.setActivityCode(uniqueCode);
        agendaItemObj.setSpeaker(speakers);
        agendaItemObj.setTag(testDataLoad.getAgendaItemTagsData("Tag1,Tag2"));
        ACTIVITY_TYPE_CODE = uniqueCode;
        TestCase testCase = agendaItemObjSvc.createAgendaItem(ACCOUNT_CODE, eventCode, agendaItemObj, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());

    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-agenda-item-required", "AgendaItem"})
    public void testPOSTAgendaItemMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.agendaItem.AgendaItemObj agendaItemObj = agendaTypeTestData.get(0);
        String uniqueCode = randomString(15);
        agendaItemObj.setActivityCode(uniqueCode);
        agendaItemObj.setName("Agenda " + uniqueCode);
        ACTIVITY_TYPE_CODE_REQ = uniqueCode;
        TestCase testCase = agendaItemObjSvc.createAgendaItem(ACCOUNT_CODE, eventCode, agendaItemObj, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemDetails() throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, ATTENDEE_TYPE_CODE);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-agendas-orderby", parallel = false)
    public Object[][] getEventsOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @org.testng.annotations.Test(dataProvider = "get-agendas-orderby", enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"})
    public void testGETAgendaItemForEventOrderBy(String orderByField) throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, orderByField, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemForEvent() throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }


    @org.testng.annotations.Test(enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemForEventUsingMaxResults() throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-agenda-filters", parallel = false)
    public Object[][] getAttendees() throws Exception {
        Object obj = testDataLoad.getAgendaItemObjFilters(agendaTypeTestData.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-agenda-filters", enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemForEventUsingSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-agenda-filters-multiple")
    public Object[][] getAgendaItemMultipleFilters() throws Exception {
        Object obj = testDataLoad.getAgendaItemObjFilters(agendaTypeTestData.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-agenda-filters-multiple", enabled = true, priority = 2, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "post-agenda-item")
    public void testGETAgendaItemForEventUsingSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = agendaItemObjSvc.getAgendaItem(ACCOUNT_CODE, eventCode, multipleFilters);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, priority = 3, groups = {"AgendaItem"}, dependsOnGroups = {"post-agenda-item", "get-agenda-item"})
    public void testDELETEAgendaItemDetails() throws Exception {
        TestCase testCase = agendaItemObjSvc.deleteAgendaItem(ACCOUNT_CODE, eventCode, ACTIVITY_TYPE_CODE);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"updated-agenda-item-required", "AgendaItem"})
    public void testPOSTUpdateAgendaItem() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.agendaItem.AgendaItemObj agendaItemObj = agendaTypeTestData.get(1);
        agendaItemObj.setActivityCode(ACTIVITY_TYPE_CODE_REQ);
        agendaItemObj.setName(agendaItemObj.getName() + " updated");
        TestCase testCase = agendaItemObjSvc.updateAgendaItem(ACCOUNT_CODE, eventCode, ACTIVITY_TYPE_CODE_REQ, agendaItemObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, priority = 3, groups = {"get-agenda-item", "AgendaItem"}, dependsOnGroups = "updated-agenda-item-required")
    public void testDELETEAgendaItemDetailsRequiredFieldsOnly() throws Exception {
        TestCase testCase = agendaItemObjSvc.deleteAgendaItem(ACCOUNT_CODE, eventCode, ACTIVITY_TYPE_CODE_REQ);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
