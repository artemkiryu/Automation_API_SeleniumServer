package com.certain.External.service.v1;

import com.certain.external.dto.event.EventObj;
import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.services.EventObjSvc;
import internal.qaauto.certain.platform.services.SpeakerObjSvc;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class SpeakerObj extends CertainAPIBase {

    private SpeakerObjSvc speakerObjSvc = new SpeakerObjSvc();
    private EventObjSvc eventObjSvc = new EventObjSvc();
    private List<EventObj> eventObjList = new ArrayList<>();
    private List<com.certain.external.dto.speakerobj.SpeakerObj> speakerObjects = new ArrayList<>();
    private TestDataLoad testDataLoad = new TestDataLoad();
    private String accountCode;
    private String eventCode;
    private String speakerCode;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        eventCode = USER_EVENT_CODE;
        accountCode = ACCOUNT_CODE;
        try {
            speakerObjects = testDataLoad.getSpeakersObjData(accountCode);
            eventObjList = testDataLoad.getEventObjData("location", accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        this.speakerObjects = null;
        this.eventObjList = null;
    }

    @org.testng.annotations.Test(enabled = true, groups = {"post-event-for-speaker", "SpeakerObj"})
    public void testAddNewEvent() throws Exception {
        EventObj eventObj = eventObjList.get(0);
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

    @org.testng.annotations.Test(enabled = true, groups = {"post-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-event-for-speaker")
    public void testPOSTSpeaker() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.speakerobj.SpeakerObj speakerObj = speakerObjects.get(0);
        String uniqueCode = randomString(15);
        speakerObj.setFirstName("SPK" + uniqueCode);
        speakerObj.setEventCode(eventCode);
        speakerObj.setEmail(uniqueCode + "@certain.com");
        TestCase testCase = speakerObjSvc.createSpeaker(accountCode, eventCode, speakerObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerCode = testCase.getSpeakerCode();
            speakerObjects.get(0).setSpeakerCode(Long.valueOf(speakerCode));
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerDetails() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, speakerCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEvent() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventUsingMaxResults() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventUsingMaxResultsWithStartIndex() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, 1, 1);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-speakers", parallel = false)
    public Object[][] getAttendees() throws Exception {
        Object obj = testDataLoad.speakerFilters(speakerObjects.get(0));
        return testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-speakers", enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventUsingSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-speakers-multiple")
    public Object[][] getAttendeeTypeMultipleFilters() throws Exception {
        Object obj = testDataLoad.speakerFilters(speakerObjects.get(0));
        return testDataLoad.getKeyValuePairFromObjectMultiple((obj), 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-speakers-multiple", enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventUsingMultipleSearchFilters(HashMap<String, Object> searchFilters) throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, searchFilters);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventOrderBySupplierCodeAsc() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, "speakerCode_asc", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"get-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-speakerObj")
    public void testGETSpeakerForEventOrderBySupplierCodeDesc() throws Exception {
        TestCase testCase = speakerObjSvc.getSpeaker(accountCode, eventCode, "speakerCode_desc", 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"update-speakerObj", "SpeakerObj"},
            dependsOnGroups = "post-event-for-speaker")
    public void testPOSTUpdateSpeaker() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.speakerobj.SpeakerObj speakerObj = speakerObjects.get(1);
        speakerObj.setEventCode(eventCode);
        TestCase testCase = speakerObjSvc.updateSpeaker(accountCode, eventCode, speakerCode, speakerObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerCode = testCase.getSpeakerCode();
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"delete-speakerObj", "SpeakerObj"},
            dependsOnGroups = "update-speakerObj")
    public void testDELETESpeaker() throws Exception {
        ExpectedCondition expectedCondition = new ExpectedCondition(200, null);
        com.certain.external.dto.speakerobj.SpeakerObj speakerObj = speakerObjects.get(1);
        speakerObj.setEventCode(eventCode);
        TestCase testCase = speakerObjSvc.updateSpeaker(accountCode, eventCode, speakerCode, speakerObj, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerCode = testCase.getSpeakerCode();
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"delete-event-for-speaker", "SpeakerObj"},
            dependsOnGroups = {"delete-speakerObj"})
    public void testDELETEEvent() throws Exception {
        TestCase testCase = eventObjSvc.deleteEvent(accountCode, eventCode);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

}

