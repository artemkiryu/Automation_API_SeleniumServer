package com.certain.External.service.v1;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.AttendeeTypeObjSvc;
import internal.qaauto.certain.platform.services.EventObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class AttendeeType extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    String accountCode;
    String eventCode;
    String ATTENDEE_TYPE_CODE;
    String ATTENDEE_TYPE_CODE_REQ;
    private AttendeeTypeObjSvc attendeeTypeObjSvc = new AttendeeTypeObjSvc();
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private List<com.certain.external.dto.attendeeType.AttendeeType> attendeeTypeData = new ArrayList<>();
    private List<com.certain.external.dto.event.EventObj> eventObjList = new ArrayList<>();
    private String[] orderBy = {"attendeeTypeCode_asc", "attendeeTypeCode_desc", "eventCode_asc", "eventCode_desc"};

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;
        try {
            attendeeTypeData = testDataLoad.getAttendeeTypeObjData(accountCode);
            eventObjList = testDataLoad.getEventObjData("location", accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(enabled = true, groups = {"post-event-for-attendee", "AttendeeType"})
    public void testAddNewEvent() throws Exception {
        com.certain.external.dto.event.EventObj eventObj = eventObjList.get(0);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        String uniqueCode = randomString(15);
        eventObj.setEventName("Event " + uniqueCode);
        eventObj.setEventCode(uniqueCode);
        TestCase testCase = eventObjSvc.postEvent(accountCode, eventObj, expectedCondition, true);
        eventCode = eventObj.getEventCode();
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-attendee-type", "AttendeeType"}, dependsOnGroups = "post-event-for-attendee")
    public void testPOSTAttendeeType() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(0);
        String uniqueCode = randomString(15);
        attendeeType.setAttendeeTypeCode(uniqueCode);
        attendeeType.setName("Attendee " + uniqueCode);
        attendeeType.setEventCode(eventCode);
        ATTENDEE_TYPE_CODE = uniqueCode;
        TestCase testCase = attendeeTypeObjSvc.createAttendeeType(accountCode, eventCode, attendeeType, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testUpdateAttendeeType() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(2);
        attendeeType.setAttendeeTypeCode(ATTENDEE_TYPE_CODE);
        attendeeType.setName("AttendeeUpdated");
        attendeeType.setEventCode(eventCode);
        TestCase testCase = attendeeTypeObjSvc.updateAttendeeType(accountCode, eventCode, ATTENDEE_TYPE_CODE, attendeeType, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"post-attendee-type-required", "AttendeeType"}, dependsOnGroups = "post-event-for-attendee")
    public void testPOSTAttendeeTypeMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(0);
        String uniqueCode = randomString(15);
        attendeeType.setAttendeeTypeCode(uniqueCode);
        attendeeType.setName("Attendee " + uniqueCode);
        ATTENDEE_TYPE_CODE_REQ = uniqueCode;
        TestCase testCase = attendeeTypeObjSvc.createAttendeeType(accountCode, eventCode, attendeeType, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testUpdateAttendeeTypeMandatoryFieldsOnly() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.attendeeType.AttendeeType attendeeType = attendeeTypeData.get(2);
        attendeeType.setAttendeeTypeCode(ATTENDEE_TYPE_CODE_REQ);
        attendeeType.setName("AttendeeUpdated");
        attendeeType.setEventCode(eventCode);
        TestCase testCase = attendeeTypeObjSvc.updateAttendeeType(accountCode, eventCode, ATTENDEE_TYPE_CODE_REQ, attendeeType, expectedCondition, true);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testGETAttendeeTypeDetails() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, ATTENDEE_TYPE_CODE);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-attendees-orderby", parallel = false)
    public Object[][] getEventsOrderBy() throws Exception {
        Object[][] orderByList = new Object[orderBy.length][1];
        int i = 0;
        for (String item : orderBy) {
            orderByList[i][0] = item;
            i++;
        }
        return orderByList;
    }

    @Test(dataProvider = "get-attendees-orderby", enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"})
    public void testGETAttendeeTypeForEventUsingOrderBy(String orderByField) throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, orderByField, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else
            Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testGETAttendeeTypeForEvent() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testGETAttendeeTypeForEventUsingMaxResults() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testGETAttendeeTypeForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, 2, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-attendees", parallel = false)
    public Object[][] getAttendees() throws Exception {
        Object obj = testDataLoad.getAttendeeTypeObjFilters(attendeeTypeData.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @Test(dataProvider = "get-attendees", enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testGETAttendeeTypeForEventUsingSearchFilter(String filter, String value) throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-attendee-multiple")
    public Object[][] getAttendeeTypeMultipleFilters() throws Exception {
        Object obj = testDataLoad.getAttendeeTypeObjFilters(attendeeTypeData.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @Test(dataProvider = "get-attendee-multiple", enabled = true, priority = 2, groups = {"get-attendee-type", "AttendeeType"},
            dependsOnGroups = {"post-event-for-attendee", "post-attendee-type"})
    public void testGETAttendeeTypeForEventUsingMultipleSearchFilters(HashMap<String, Object> multipleFilters) throws Exception {
        TestCase testCase = attendeeTypeObjSvc.getAttendeeType(accountCode, eventCode, multipleFilters, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 3, groups = {"delete-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type")
    public void testDELETEAttendeeTypeDetails() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.deleteAttendeeType(accountCode, eventCode, ATTENDEE_TYPE_CODE);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, priority = 3, groups = {"delete-attendee-type", "AttendeeType"}, dependsOnGroups = "post-attendee-type-required")
    public void testDELETEAttendeeTypeDetailsRequiredFieldsOnly() throws Exception {
        TestCase testCase = attendeeTypeObjSvc.deleteAttendeeType(accountCode, eventCode, ATTENDEE_TYPE_CODE_REQ);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @Test(enabled = true, groups = {"delete-event-for-attendee", "AttendeeType"},
            dependsOnGroups = {"post-attendee-type", "post-event-for-attendee", "delete-attendee-type"})
    public void testDELETEEvent() throws Exception {
        TestCase testCase = eventObjSvc.deleteEvent(accountCode, eventCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}
