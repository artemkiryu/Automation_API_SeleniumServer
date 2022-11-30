package com.certain.standard.api;


import internal.qaauto.certain.platform.dataprovider.TestDataLoad;
import internal.qaauto.certain.platform.pojo.CertainAPIBase;
import internal.qaauto.certain.platform.pojo.ExpectedCondition;
import internal.qaauto.certain.platform.pojo.TestCase;
import internal.qaauto.certain.platform.pojo.TestCaseData;
import internal.qaauto.certain.platform.pojo.speakers.SpeakersObject;
import internal.qaauto.certain.platform.services.SpeakerManagement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class Speakers extends CertainAPIBase {

    private final TestDataLoad testDataLoad = new TestDataLoad();
    private SpeakerManagement speakerManagement = new SpeakerManagement();
    private List<SpeakersObject> speakersObjects = new ArrayList<>();
    private String firstName, speakerPin, speakerEmail;
    private String speakerId;
    private SpeakersObject speakersObject = new SpeakersObject();
    private String accountCode;
    private String eventCode;

    @Override
    public void loadData() {
        super.loadData();
        accountCode = ACCOUNT_CODE;
        eventCode = USER_EVENT_CODE;

    }
    @BeforeClass(alwaysRun = true)
    public void setup() {
        loadData();
        try {
            speakersObjects = testDataLoad.getUCSpeakersObjData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postSpeaker", "UC", "Speakers"})
    public void testPostSpeaker() throws Exception {
        String uniqueCode = randomString(12);
        speakersObject = speakersObjects.get(0);
        speakerEmail = uniqueCode + "@gmail.com";
        speakersObject.setEmail(speakerEmail);
        speakersObject.setPin(speakersObject.getPin() + randomNumber(3));
        speakersObject.setFirstName("Speaker " + uniqueCode);
        speakerPin = speakersObject.getPin();
        firstName = speakersObject.getFirstName();
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " added to event successfully");
        TestCase testCase = speakerManagement.postSpeaker(accountCode, eventCode, speakersObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            speakerId = String.valueOf(testCase.getSpeakerId());
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"postSpeaker", "UC", "Speakers"})
    public void testPostSpeakerWithType() throws Exception {
        String uniqueCode = randomString(12);
        SpeakersObject speakersObject = speakersObjects.get(0);
        //speakersObject.setEmail(speakerEmail);
        speakersObject.setPin(speakersObject.getPin() + randomNumber(3));
        speakersObject.setFirstName("Speaker " + uniqueCode);
        speakersObject.setSpeakerType("Speaker1");
        speakerEmail = "vini" + uniqueCode + "@gmail.com";
        speakersObject.setEmail(speakerEmail);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " added to event successfully");
        TestCase testCase = speakerManagement.postSpeaker(accountCode, eventCode, speakersObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getSpeaker", "UC", "Speakers"}, dependsOnGroups = "postSpeaker")
    public void testGETSpeakerBySpeakerId() throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, Integer.valueOf(speakerId));
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getSpeaker", "UC", "Speakers"})
    public void testGETSpeakerByInvaidSpeakerId() throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, 99991);
        if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-filters-dp1", parallel = false)
    public Object[][] getSpeakerFilters() throws Exception {
        Object obj = speakerManagement.getUCSpeakersObjFilters(speakersObject);
        return this.testDataLoad.getKeyValuePairFromObject(obj);
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp1", enabled = true, groups = {"getSpeaker", "UC", "Speakers"}, dependsOnGroups = "postSpeaker")
    public void testGETSpeakerWithSearchFilters(String filter, String value) throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No speakers found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp1", enabled = true, groups = {"getSpeaker", "UC", "Speakers"}, dependsOnGroups = "postSpeaker")
    public void testGETSpeakerWithSearchFiltersUsingMaxResults(String filter, String value) throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, filter, value, 1, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No speakers found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-filters-dp2")
    public Object[][] getAccountAPIFilters() throws Exception {
        Object obj = speakerManagement.getUCSpeakersObjFilters(speakersObject);
        return testDataLoad.getKeyValuePairFromObjectMultiple(obj, 0);
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp2", enabled = true, groups = {"getSpeaker", "UC", "Speakers"}, dependsOnGroups = "postSpeaker")
    public void testGETSpeakerWithMultipleSearchFilters(HashMap<String, Object> f) throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, f, null, 0, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log("HTTP 404 No speakers found matching criteria", true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @DataProvider(name = "get-filters-dp3", parallel = false)
    public Object[][] getNegSessionFilters() throws Exception {
        return this.testDataLoad.getNegativeFiltersData("Speakers");
    }

    @org.testng.annotations.Test(dataProvider = "get-filters-dp3", enabled = true, groups = {"getSpeaker", "UC", "Speakers"})
    public void testGETSpeakerWithNegativeSearchFilters(String filter, String value, String statusCode, String Message) throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, filter, value, 0, 0);
        if (testCase.getStatusCode() == Integer.valueOf(statusCode)) {
            if (testCase.getPayload().contains(Message)) {
                Reporter.log(testCase.getMessage(), true);
                Assert.assertTrue(true);
            } else Assert.assertTrue(false, testCase.getMessage());
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getSpeaker", "UC", "Speakers"})
    public void testGETSpeakerWithMaxLimit() throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, 3, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"getSpeaker", "UC", "Speakers"})
    public void testGETSpeakerWithMaxLimitAndPage() throws Exception {
        TestCase testCase = speakerManagement.getSpeakers(accountCode, eventCode, 2, 0);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else if (testCase.getStatusCode() == 404) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"updateSpeaker", "UC", "Speakers"}, dependsOnGroups = "postSpeaker")
    public void testPutUpdateSpeaker() throws Exception {
        SpeakersObject speakersObject = speakersObjects.get(1);
        speakersObject.setPin(speakerPin);
        speakersObject.setFirstName(firstName + " edited");
        speakersObject.setEmail(speakerEmail);
        ExpectedCondition expectedCondition = new ExpectedCondition(200, "Speaker " + speakerEmail + " updated successfully");
        TestCase testCase = speakerManagement.putSpeaker(accountCode, eventCode, speakerId, speakersObject, expectedCondition);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }

    @org.testng.annotations.Test(enabled = true, groups = {"deleteSpeaker", "UC", "Speakers"}, dependsOnGroups = "updateSpeaker")
    public void testDeleteSpeaker() throws Exception {
        TestCase testCase = speakerManagement.deleteSpeaker(accountCode, eventCode, speakerId);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(true);
    }

    //Negative Post tests
    @DataProvider(name = "post-speakers-dp1", parallel = false)
    public Object[][] postSpeakerNegative() throws Exception {
        List<TestCaseData> testCaseDataList = testDataLoad.getSpeakersData();
        int startRow = 3;
        Object[][] objects = new Object[testCaseDataList.size() - startRow][4];
        int index = 0;
        for (int i = startRow; i < testCaseDataList.size(); i++) {
            objects[index][0] = testCaseDataList.get(i).getObject();
            objects[index][1] = testCaseDataList.get(i).getDescription();
            objects[index][2] = testCaseDataList.get(i).getStatusCode();
            objects[index][3] = testCaseDataList.get(i).getExpectedMessage();
            index++;
        }
        return objects;
    }

    @org.testng.annotations.Test(dataProvider = "post-speakers-dp1", enabled = true, groups = {"postSpeaker-neg", "UC", "Speakers"})
    public void testPostSpeakerNegative(SpeakersObject speakersObject, String description, int statusCode, String expectedMessage) throws Exception {
        Reporter.log("Test Scenario:  " + description);
        if (description.equalsIgnoreCase("POST no pin")) {
            speakersObject.setPin(null);
        }
        ExpectedCondition expectedCondition = new ExpectedCondition(statusCode, expectedMessage);
        TestCase testCase = speakerManagement.postSpeaker(accountCode, eventCode, speakersObject, expectedCondition, false);
        if (testCase.isPassed()) {
            Reporter.log(testCase.getMessage(), true);
            Assert.assertTrue(true);
        } else Assert.assertTrue(false, testCase.getMessage());
    }
}